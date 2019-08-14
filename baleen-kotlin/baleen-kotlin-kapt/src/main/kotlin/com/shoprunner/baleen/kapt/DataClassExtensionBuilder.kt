package com.shoprunner.baleen.kapt

import com.shoprunner.baleen.Context
import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.Validation
import com.shoprunner.baleen.datawrappers.HashData
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import java.io.File
import java.util.SortedSet
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.ArrayType
import javax.lang.model.type.DeclaredType
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import org.jetbrains.annotations.NotNull

internal class DataClassExtensionBuilder(
    private val kaptKotlinGeneratedDir: String,
    private val elementUtils: Elements,
    private val typeUtils: Types,
    private val messager: Messager
) {
    fun generateExtensionFile(
        dataDescriptionElement: DataDescriptionElement,
        allSchemas: Map<String, DataDescriptionElement>
    ) {
        val (typeElement, packageName, name) = dataDescriptionElement

        val members = (typeElement.enclosedElements ?: emptyList<Element>())
            .filter { it.kind == ElementKind.FIELD }

        val commonPackagePrefix = allSchemas.values
            .map {
                typeElement.asClassName().packageName.split(".").map { it.capitalize() }.joinToString("")
            }
            .reduce { lhs, rhs -> lhs.commonPrefixWith(rhs, ignoreCase = true) }
        val classPackage = typeElement.asClassName().packageName.split(".").map { it.capitalize() }.joinToString("")
        val jvmNamePrefix = classPackage.replaceFirst(commonPackagePrefix, "")

        val extensionName = "${jvmNamePrefix}${name}Extension"
        FileSpec.builder("com.shoprunner.baleen.kotlin", extensionName)
            .addAnnotation(
                AnnotationSpec.builder(JvmName::class)
                    .useSiteTarget(AnnotationSpec.UseSiteTarget.FILE)
                    .addMember("%S", extensionName)
                    .build()
            )
            .addFunction(
                generateDataDescriptionFun(typeElement, packageName, name)
            )
            .addFunction(
                generateValidateFun(typeElement)
            )
            .addFunction(
                generateAsHashDataFun(typeElement, members, allSchemas)
            )
            .addFunction(
                generateToDataClassFun(typeElement, members, allSchemas)
            )
            .build()
            .writeTo(File(kaptKotlinGeneratedDir))
    }

    private fun generateDataDescriptionFun(
        typeElement: TypeElement,
        packageName: String,
        name: String
    ): FunSpec {
        return FunSpec.builder("dataDescription")
            .receiver(typeElement.asClassName())
            .returns(DataDescription::class.java)
            .addCode(CodeBlock.of("return %M\n", MemberName(packageName, "${name.capitalize()}Type")))
            .build()
    }

    private fun generateValidateFun(typeElement: TypeElement): FunSpec {
        return FunSpec.builder("validate")
            .receiver(typeElement.asClassName())
            .addParameter(
                ParameterSpec.builder("dataDescription", DataDescription::class)
                    .defaultValue("this.dataDescription()")
                    .build()
            )
            .addParameter(
                ParameterSpec.builder("dataTrace", DataTrace::class)
                    .defaultValue("%M()", MemberName("com.shoprunner.baleen", "dataTrace"))
                    .build()
            )
            .returns(Validation::class)
            .addCode(
                "return dataDescription.validate(%T(this.asHashData(), dataTrace))\n",
                Context::class
            )
            .build()
    }

    private fun generateAsHashDataFun(
        typeElement: TypeElement,
        members: List<Element>,
        allSchemas: Map<String, DataDescriptionElement>
    ): FunSpec {
        return FunSpec.builder("asHashData")
            .addModifiers(KModifier.INTERNAL)
            .receiver(typeElement.asClassName())
            .returns(HashData::class)
            .addCode(CodeBlock.builder().apply {
                add("return %T(mapOf(\n", HashData::class.java)
                indent()
                add(members
                    .mapIndexed { i, m ->
                        val memberType = m.asType()
                        CodeBlock.builder().apply {
                            if (i != 0) {
                                add(",\n")
                            }
                            add("%S to ${m.simpleName}", m.simpleName)
                            val opt = if (!m.isAnnotationPresent(NotNull::class.java)) "?" else ""
                            when {
                                // Occurences Types
                                memberType is ArrayType && allSchemas.containsKey(memberType.componentType.toString()) ->
                                    add(
                                        "$opt.map{ it.%M() }$opt.toList()",
                                        MemberName("com.shoprunner.baleen.kotlin", "asHashData")
                                    )

                                memberType is ArrayType ->
                                    add("$opt.toList()")

                                // Iterable
                                memberType is DeclaredType && isIterable(typeUtils, elementUtils, memberType)
                                        && allSchemas.containsKey(memberType.componentTypes()?.first()?.canonicalName) ->
                                    add(
                                        "$opt.map { it.%M() }",
                                        MemberName("com.shoprunner.baleen.kotlin", "asHashData")
                                    )

                                memberType is DeclaredType && isMap(typeUtils, elementUtils, memberType) -> {
                                    add("$opt.map{ (k, v) -> ")
                                    val componentTypes = memberType.componentTypes()
                                    if (allSchemas.containsKey(componentTypes?.first()?.canonicalName)) {
                                        add("k.%M()", MemberName("com.shoprunner.baleen.kotlin", "asHashData"))
                                    } else {
                                        add("k")
                                    }
                                    add(" to ")
                                    if (allSchemas.containsKey(componentTypes?.get(1)?.canonicalName)) {
                                        add("v.%M()", MemberName("com.shoprunner.baleen.kotlin", "asHashData"))
                                    } else {
                                        add("v")
                                    }
                                    add(" }$opt.toMap()")
                                }

                                allSchemas.containsKey(memberType.toString()) -> add("$opt.asHashData()")
                            }
                        }.build()
                    }
                    .fold(CodeBlock.builder()) { builder, code -> builder.add(code) }
                    .build()
                )
                unindent()
                add("\n))\n")
            }.build())
            .build()
    }

    private fun generateToDataClassFun(
        typeElement: TypeElement,
        members: List<Element>,
        allSchemas: Map<String, DataDescriptionElement>
    ): FunSpec {
        return FunSpec.builder("as${typeElement.asClassName().simpleName}")
            .addModifiers(KModifier.INTERNAL)
            .addParameter(ParameterSpec.builder("data", Data::class).build())
            .returns(typeElement.asClassName())
            .addCode(CodeBlock.builder().apply {
                add("return %T(\n", typeElement.asClassName())
                indent()
                add(members
                    .mapIndexed { i, m ->
                        CodeBlock.builder().apply {
                            if (i != 0) {
                                add(",\n")
                            }
                            add(generateAsDataClassMember(m, allSchemas))
                        }.build()
                    }
                    .fold(CodeBlock.builder()) { builder, code -> builder.add(code) }
                    .build()
                )
                unindent()
                add("\n)\n")
            }.build())
            .build()
    }

    private fun generateAsDataClassMember(
        member: Element,
        allSchemas: Map<String, DataDescriptionElement>
    ): CodeBlock {
        val memberType = member.asType()
        return CodeBlock.builder().apply {
            add("${member.simpleName} = data[%S]", member.simpleName)
            when {
                // Occurences Types
                memberType is ArrayType && allSchemas.containsKey(memberType.componentType.toString()) ->
                    add(
                        "?.let { %M<%T>(it, ::%M) }?.toTypedArray()",
                        MemberName(
                            "com.shoprunner.baleen.kotlin",
                            "castElementsInList"
                        ),
                        memberType.componentType.asTypeName().javaToKotlinType(),
                        MemberName(
                            "com.shoprunner.baleen.kotlin",
                            "as${(memberType.componentType as DeclaredType).asElement().simpleName}"
                        )
                    )

                memberType is ArrayType ->
                    add(
                        "?.let { %M<%T>(it) }?.toTypedArray()",
                        MemberName(
                            "com.shoprunner.baleen.kotlin",
                            "castElementsInList"
                        ),
                        memberType.componentType.asTypeName().javaToKotlinType()
                    )

                // Iterable
                memberType is DeclaredType && isIterable(typeUtils, elementUtils, memberType) -> {
                    val componentType = memberType.componentTypes()?.first()
                    if (allSchemas.containsKey(componentType?.canonicalName)) {
                        add(
                            "?.let { %M<%T>(it, ::%M) }",
                            MemberName(
                                "com.shoprunner.baleen.kotlin",
                                "castElementsInList"
                            ),
                            componentType?.javaToKotlinType(),
                            MemberName(
                                "com.shoprunner.baleen.kotlin",
                                "as${componentType?.simpleName}"
                            )
                        )
                    } else {
                        add(
                            "?.let { %M<%T>(it) }",
                            MemberName(
                                "com.shoprunner.baleen.kotlin",
                                "castElementsInList"
                            ),
                            componentType?.javaToKotlinType()
                        )
                    }
                    when (memberType.mainType()?.javaToKotlinType()) {
                        MutableSet::class.asClassName() -> add("?.toMutableSet()")
                        SortedSet::class.asClassName() -> add("?.toSortedSet()")
                        HashSet::class.asClassName() -> add("?.toHashSet()")
                        Set::class.asClassName() -> add("?.toSet()")

                        MutableList::class.asClassName() -> add("?.toMutableList()")
                        // List::class.asClassName() -> add(".toList()")
                    }
                }

                memberType is DeclaredType && isMap(typeUtils, elementUtils, memberType) -> {
                    // ?.let { castElementsInMap<String, Dog>(it, transformValues = ::asDog) }
                    val componentTypes = memberType.componentTypes()
                    val keyType = componentTypes?.first()
                    val valueType = componentTypes?.get(1)
                    add(
                        "?.let { %M<%T, %T>(it",
                        MemberName(
                            "com.shoprunner.baleen.kotlin",
                            "castElementsInMap"
                        ),
                        keyType?.javaToKotlinType(),
                        valueType?.javaToKotlinType()
                    )
                    if (allSchemas.containsKey(keyType?.canonicalName)) {
                        add(
                            ", transformKeys = ::%M",
                            MemberName(
                                "com.shoprunner.baleen.kotlin",
                                "as${keyType?.simpleName}"
                            )
                        )
                    }

                    if (allSchemas.containsKey(valueType?.canonicalName)) {
                        add(
                            ", transformValues = ::%M",
                            MemberName(
                                "com.shoprunner.baleen.kotlin",
                                "as${valueType?.simpleName}"
                            )
                        )
                    }
                    add(") }?.toMutableMap()")
                }

                // an annotated @DataDescription data class
                memberType is DeclaredType && allSchemas.containsKey(memberType.toString()) -> {
                    add(
                        "?.let { %M(it, ::%M) }",
                        MemberName("com.shoprunner.baleen.kotlin", "castIfData"),
                        MemberName(
                            "com.shoprunner.baleen.kotlin",
                            "as${memberType.mainType()?.simpleName}"
                        )
                    )
                }

                else ->
                    add("?.let { it as %T }", member.asType().asTypeName().javaToKotlinType())
            }

            if (member.isAnnotationPresent(NotNull::class.java)) {
                add("!!")
            }
        }.build()
    }

    private fun DeclaredType.componentTypes(): List<ClassName>? {
        return this.typeArguments.map { ((it as DeclaredType).asElement() as TypeElement).asClassName() }
    }

    private fun DeclaredType.mainType(): ClassName? {
        return (this.asElement() as TypeElement).asClassName()
    }
}
