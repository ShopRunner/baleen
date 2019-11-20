package com.shoprunner.baleen.kapt

import com.shoprunner.baleen.annotation.DefaultValue
import com.shoprunner.baleen.annotation.DefaultValueType
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import com.squareup.kotlinpoet.metadata.ImmutableKmConstructor
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.declaresDefaultValue
import com.squareup.kotlinpoet.metadata.isNullable
import com.squareup.kotlinpoet.metadata.isPrimary
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import java.time.Instant
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Name
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.type.MirroredTypeException
import kotlin.reflect.KClass
import kotlinx.metadata.KmClassifier

internal data class DefaultValueContainer(
    val defaultValueInstance: CodeBlock,
    val attributes: Map<Name, CodeBlock>
)

@KotlinPoetMetadataPreview
internal fun TypeElement.defaultValues(): DefaultValueContainer {
    val kmClass = this.toImmutableKmClass()

    val primaryConstructor = kmClass.constructors.first { it.isPrimary }

    // Create a Default Value class based on the best constructor so that only mandortory fields are set
    // And the rest can be defaulted to the class's default value
    val defaultValInstance = CodeBlock.builder()
        .add("%T(", this.asClassName())
        .apply {
            primaryConstructor.valueParameters.filterNot { it.declaresDefaultValue }.forEachIndexed { i, it ->
                if (i != 0) add(", ")
                add("%L = ", it.name)
                if (it.type?.isNullable == true) {
                    add("null")
                } else {
                    val kmClassifier = it.type?.classifier as KmClassifier.Class
                    val className = kmClassifier.name
                    when (className) {
                        "kotlin/String" -> add("%S", "IGNORE")
                        "kotlin/Int" -> add("-1")
                        "kotlin/Long" -> add("-1L")
                        "kotlin/Short" -> add("%T.MIN_VALUE", Short::class)
                        "kotlin/Byte" -> add("%T.MIN_VALUE", Byte::class)
                        "java/math/BigInteger" -> add("(-1L).toBigInteger()")
                        "kotlin/Float" -> add("-1.0f")
                        "kotlin/Double" -> add("-1.0")
                        "java/math/BigDecimal" -> add("(-1L).toBigDecimal()")
                        "kotlin/Boolean" -> add("false")
                        "java/time/Instant" -> add("%T.now()", Instant::class)
                        "kotlin/Array" -> add("emptyArray()")
                        "kotlin/collections/List" -> add("emptyList()")
                        "kotlin/collections/Map" -> add("emptyMap()")
                        "kotlin/collections/Set" -> add("emptySet()")
                        "kotlin/collections/Iterable" -> add("emptyList()")
                        else -> {
                            val n = className.split("/")
                            val pkg = n.subList(0, n.size - 1).joinToString(".")
                            val name = n.last()

                            add("%M", MemberName(pkg, "${name}Defaults"))
                        }
                    }
                }
            }
        }
        .add(")")
        .build()

    val fieldsWithDefaults = (this.enclosedElements ?: emptyList<Element>())
        .filter { it.kind == ElementKind.FIELD }
        .map { it as VariableElement }
        .filter { it.declaresDefault(primaryConstructor) }
        .map { field ->
            if (field.isAnnotationPresent(DefaultValue::class.java)) {
                field.simpleName to field.getDefaultValueFromAnnotation()
            } else {
                val n = this.qualifiedName.toString().split(".")
                val pkg = n.subList(0, n.size - 1).joinToString(".")
                val name = n.last()
                field.simpleName to CodeBlock.of("%M.%L", MemberName(pkg, "${name}Defaults"), field.simpleName)
            }
        }
        .toMap()

    return DefaultValueContainer(defaultValInstance, fieldsWithDefaults)
}

@KotlinPoetMetadataPreview
fun VariableElement.declaresDefault(primaryConstructor: ImmutableKmConstructor): Boolean {
    return this.isAnnotationPresent(DefaultValue::class.java) || primaryConstructor
        .valueParameters
        .filter { it.name == this.simpleName.toString() }
        .any { it.declaresDefaultValue }
}

fun VariableElement.getDefaultValueFromAnnotation(): CodeBlock {
    val defaultValueAnnotation = this.getAnnotation(DefaultValue::class.java)
    return when (defaultValueAnnotation.type) {
        DefaultValueType.Null -> CodeBlock.of("null")
        DefaultValueType.Boolean -> CodeBlock.of("%L", defaultValueAnnotation.defaultBooleanValue)
        DefaultValueType.String -> CodeBlock.of("%S", defaultValueAnnotation.defaultStringValue)
        DefaultValueType.Int -> CodeBlock.of("%L", defaultValueAnnotation.defaultIntValue)
        DefaultValueType.Long -> CodeBlock.of("%LL", defaultValueAnnotation.defaultLongValue)
        DefaultValueType.BigInteger -> CodeBlock.of("%S.toBigInteger()", defaultValueAnnotation.defaultStringValue)
        DefaultValueType.Float -> CodeBlock.of("%Lf", defaultValueAnnotation.defaultFloatValue)
        DefaultValueType.Double -> CodeBlock.of("%L", defaultValueAnnotation.defaultDoubleValue)
        DefaultValueType.BigDecimal -> CodeBlock.of("%S.toBigDecimal()", defaultValueAnnotation.defaultStringValue)
        DefaultValueType.Instant -> CodeBlock.of(
            "%T.parse(%S)",
            Instant::class,
            defaultValueAnnotation.defaultStringValue
        )
        DefaultValueType.DataClass -> CodeBlock.of("%T()", toTypeName {
            defaultValueAnnotation.defaultDataClassValue
        })
        DefaultValueType.EmptyArray -> CodeBlock.of(
            "emptyArray<%T>()",
            toTypeName { defaultValueAnnotation.defaultElementClass }.javaToKotlinType()
        )
        DefaultValueType.EmptyList -> CodeBlock.of(
            "emptyList<%T>()",
            toTypeName { defaultValueAnnotation.defaultElementClass }.javaToKotlinType()
        )
        DefaultValueType.EmptySet -> CodeBlock.of(
            "emptySet<%T>()",
            toTypeName { defaultValueAnnotation.defaultElementClass }.javaToKotlinType()
        )
        DefaultValueType.EmptyMap -> CodeBlock.of(
            "emptyMap<%T, %T>()",
            toTypeName { defaultValueAnnotation.defaultKeyClass }.javaToKotlinType(),
            toTypeName { defaultValueAnnotation.defaultElementClass }.javaToKotlinType()
        )
    }
}

private inline fun toTypeName(clazzFun: () -> KClass<*>): TypeName = try {
    clazzFun().asTypeName()
} catch (e: MirroredTypeException) {
    e.typeMirror.asTypeName()
}
