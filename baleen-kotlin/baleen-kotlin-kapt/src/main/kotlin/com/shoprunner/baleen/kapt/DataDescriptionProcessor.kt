package com.shoprunner.baleen.kapt

import com.google.auto.service.AutoService
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.annotation.DataDescription
import com.shoprunner.baleen.annotation.DataTest
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import javax.annotation.processing.AbstractProcessor
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.annotation.processing.Processor
import javax.annotation.processing.RoundEnvironment
import javax.annotation.processing.SupportedOptions
import javax.annotation.processing.SupportedSourceVersion
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.ExecutableElement
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Processor for @DataDescription and @DataTests
 */
@AutoService(Processor::class) // For registering the service
@SupportedSourceVersion(SourceVersion.RELEASE_8) // to support Java 8
@SupportedOptions(DataDescriptionProcessor.KAPT_KOTLIN_GENERATED_OPTION_NAME)
@KotlinPoetMetadataPreview
class DataDescriptionProcessor : AbstractProcessor() {
    companion object {
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"
    }

    lateinit var elementUtils: Elements
    lateinit var typeUtils: Types
    lateinit var messager: Messager
    lateinit var kaptKotlinGeneratedDir: String

    internal lateinit var dataDescriptionBuilder: DataDescriptionBuilder
    internal lateinit var dataClassExtensionBuilder: DataClassExtensionBuilder

    val allSchemas = mutableMapOf<String, DataDescriptionElement>()

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        elementUtils = processingEnv!!.elementUtils
        typeUtils = processingEnv.typeUtils
        messager = processingEnv.messager
        kaptKotlinGeneratedDir = processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]!!

        dataDescriptionBuilder = DataDescriptionBuilder(kaptKotlinGeneratedDir, elementUtils, typeUtils, messager)
        dataClassExtensionBuilder = DataClassExtensionBuilder(kaptKotlinGeneratedDir, elementUtils, typeUtils, messager)
    }

    override fun process(annotations: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        messager.note { "Running DataDescriptionProcessor" }

        val annotatedWithDataDescription = roundEnv.getElementsAnnotatedWith(DataDescription::class.java)
        val annotatedWithDataTest = roundEnv.getElementsAnnotatedWith(DataTest::class.java)

        annotatedWithDataDescription.forEach {
            when {
                it.kind != ElementKind.CLASS ->
                    messager.error { "Element annotated with @DataDescription is not a class: `$it`" }
                it is TypeElement && it.asClassName().canonicalName in allSchemas ->
                    messager.warning { "Class annotated with @DataDescription already processed: `$it`" }
                else -> {
                    messager.note { "@DataDescription identified: `$it`" }
                }
            }
        }

        val schemas = annotatedWithDataDescription
            .filter { it.kind == ElementKind.CLASS }
            .map {
                val typeElement = it as TypeElement
                val ddAnn = typeElement.getAnnotation(DataDescription::class.java)
                val packageName = ddAnn.namespace.takeIf { it.isNotBlank() } ?: typeElement.asClassName().packageName
                val name = ddAnn.name.takeIf { it.isNotBlank() } ?: typeElement.asClassName().simpleName
                DataDescriptionElement(typeElement, packageName, name)
            }
            .map { it.typeElement.asClassName().canonicalName to it }
            .toMap()

        // We already warned when name collisions happen.
        allSchemas.putAll(schemas)

        // Validation
        annotatedWithDataTest.forEach {
            when {
                it.kind != ElementKind.METHOD ->
                    messager.error { "Element annotated with @DataTest is not a method: `$it`" }
                !returnValidationResultIterable(it as ExecutableElement) ->
                    messager.error { "Method annotated with @DataTest does not return Iterable<ValidationResult> or Sequence<ValidationResult>: `$it`" }
                !isValidDataTestFunction(it) ->
                    messager.error { "Method annotated with @DataTest is not a valid function in format `@DataTest fun name(data: DataClass, dataTrace: DataTrace): Sequence<ValidationResult>` : $it" }
                it.parameters.first().asType().toString() !in allSchemas ->
                    messager.error { "Method annotated with @DataTest does not have a valid data class to apply to: `$it`. Class `${it.parameters.first().asType()}` is not annotated with @DataDescription" }
                else ->
                    messager.note { "@DataTest '$it' identified for '${it.parameters.first().asType()}'" }
            }
        }

        val allExtraTests = annotatedWithDataTest
            .filter { it.kind == ElementKind.METHOD }
            .map { it as ExecutableElement }
            .filter(::returnValidationResultIterable)
            .filter(::isValidDataTestFunction)
            .map { DataTestElement(it, it.parameters.first().asType() as DeclaredType, it.isExtension()) }
            .groupBy { it.receiverType.toString() }

        schemas.forEach {
            val fullName = it.key
            messager.note { "Generating data description for $fullName" }
            dataDescriptionBuilder.generateDataDescription(it.value, allExtraTests[fullName] ?: emptyList(), allSchemas)
            dataClassExtensionBuilder.generateExtensionFile(it.value, allSchemas)
        }

        messager.note { "Finished DataDescriptionProcessor" }
        return schemas.isNotEmpty() || allExtraTests.isNotEmpty()
    }

    // Function with a two parameters: testFun(dog: Dog, dataTrace: DataTrace)
    private fun isValidDataTestFunction(func: ExecutableElement): Boolean {
        return (func.parameters?.size == 2 &&
                func.parameters?.first()?.asType()?.kind == TypeKind.DECLARED &&
                typeUtils.isSameType(
                    func.parameters[1]?.asType(),
                    elementUtils.getTypeElement(DataTrace::class.java.canonicalName).asType()
        ))
    }

    private fun ExecutableElement.isExtension(): Boolean = this.getAnnotation(DataTest::class.java).isExtension

    private fun returnValidationResultIterable(func: ExecutableElement): Boolean =
        func.returnType is DeclaredType &&
                (typeUtils.isSubtype(
                    func.returnType,
                    typeUtils.getDeclaredType(
                        elementUtils.getTypeElement(Iterable::class.java.canonicalName),
                        typeUtils.getWildcardType(null, elementUtils.getTypeElement(ValidationResult::class.java.canonicalName).asType())
                    )
                ) || typeUtils.isSubtype(
                    func.returnType,
                    typeUtils.getDeclaredType(
                        elementUtils.getTypeElement(Sequence::class.java.canonicalName),
                        typeUtils.getWildcardType(null, elementUtils.getTypeElement(ValidationResult::class.java.canonicalName).asType())
                    )
                ))

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(DataDescription::class.java.canonicalName, DataTest::class.java.canonicalName)
    }
}
