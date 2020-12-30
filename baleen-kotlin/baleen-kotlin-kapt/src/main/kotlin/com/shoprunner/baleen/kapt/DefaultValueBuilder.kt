package com.shoprunner.baleen.kapt

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.metadata.KotlinPoetMetadataPreview
import com.squareup.kotlinpoet.metadata.declaresDefaultValue
import com.squareup.kotlinpoet.metadata.isNullable
import com.squareup.kotlinpoet.metadata.isPrimary
import com.squareup.kotlinpoet.metadata.toImmutableKmClass
import kotlinx.metadata.KmClassifier
import java.time.Instant
import javax.lang.model.element.TypeElement

internal data class DefaultValueContainer(
    val defaultValueInstance: CodeBlock,
    val attributes: Map<String, CodeBlock>
)

@KotlinPoetMetadataPreview
internal fun DataDescriptionElement.defaultValues(): DefaultValueContainer = this.typeElement.defaultValues()

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
                val kmType = it.type
                val kmClassifier = kmType?.classifier as KmClassifier.Class
                val className = kmClassifier.name
                when {
                    it.type?.isNullable == true -> add("null")
                    else -> when (className) {
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

    val fieldsWithDefaults = primaryConstructor.valueParameters
        .filter { it.declaresDefaultValue }
        .map {
            val n = this.qualifiedName.toString().split(".")
            val pkg = n.subList(0, n.size - 1).joinToString(".")
            val name = n.last()
            it.name to CodeBlock.of("%M.%L", MemberName(pkg, "${name}Defaults"), it.name)
        }
        .toMap()

    return DefaultValueContainer(defaultValInstance, fieldsWithDefaults)
}
/*
if (param.isAnnotationPresent(DefaultValue::class.java)) {
                val defaultValueAnnotation = param.getAnnotation(DefaultValue::class.java)
                add(",\n")
                when (defaultValueAnnotation.type) {
                    DefaultValueType.Null -> add("default = null")
                    DefaultValueType.Boolean -> add("default = %L", defaultValueAnnotation.defaultBooleanValue)
                    DefaultValueType.String -> add("default = %S", defaultValueAnnotation.defaultStringValue)
                    DefaultValueType.Int -> add("default = %L", defaultValueAnnotation.defaultIntValue)
                    DefaultValueType.Long -> add("default = %LL", defaultValueAnnotation.defaultLongValue)
                    DefaultValueType.BigInteger -> add("default = %S.toBigInteger()", defaultValueAnnotation.defaultStringValue)
                    DefaultValueType.Float -> add("default = %Lf", defaultValueAnnotation.defaultFloatValue)
                    DefaultValueType.Double -> add("default = %L", defaultValueAnnotation.defaultDoubleValue)
                    DefaultValueType.BigDecimal -> add("default = %S.toBigDecimal()", defaultValueAnnotation.defaultStringValue)
                    DefaultValueType.DataClass -> add("default = %T()", toTypeName {
                        defaultValueAnnotation.defaultDataClassValue
                    })
                    DefaultValueType.EmptyArray -> add("default = emptyArray<%T>()",
                        toTypeName { defaultValueAnnotation.defaultElementClass }.javaToKotlinType()
                    )
                    DefaultValueType.EmptyList -> add("default = emptyList<%T>()",
                        toTypeName { defaultValueAnnotation.defaultElementClass }.javaToKotlinType()
                    )
                    DefaultValueType.EmptySet -> add("default = emptySet<%T>()",
                        toTypeName { defaultValueAnnotation.defaultElementClass }.javaToKotlinType()
                    )
                    DefaultValueType.EmptyMap -> add("default = emptyMap<%T, %T>()",
                        toTypeName { defaultValueAnnotation.defaultKeyClass }.javaToKotlinType(),
                        toTypeName { defaultValueAnnotation.defaultElementClass }.javaToKotlinType()
                    )
                }
            }

 */
