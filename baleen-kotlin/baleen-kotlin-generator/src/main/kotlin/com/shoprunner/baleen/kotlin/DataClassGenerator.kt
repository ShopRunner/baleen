package com.shoprunner.baleen.kotlin

import com.shoprunner.baleen.AttributeDescription
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.NoDefault
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.BooleanType
import com.shoprunner.baleen.types.CoercibleType
import com.shoprunner.baleen.types.DoubleType
import com.shoprunner.baleen.types.EnumType
import com.shoprunner.baleen.types.FloatType
import com.shoprunner.baleen.types.InstantType
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.NumericType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringConstantType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.TimestampMillisType
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.time.LocalDateTime
import kotlin.IllegalArgumentException
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType

object DataClassGenerator {

    internal fun BaleenType.asTypeName(options: Options): TypeName {
        val override = options.overrides.find { it.isOverridable(this) }
        if (override != null) {
            return override.override(this).asTypeName()
        }

        return when (this) {
            is AllowsNull<*> -> this.type.asTypeName(options).copy(nullable = true)
            is DataDescription -> this.asClassName()
            is BooleanType -> Boolean::class.asClassName()
            is DoubleType -> Double::class.asClassName()
            is IntType -> Int::class.asClassName()
            is IntegerType -> BigInteger::class.asClassName()
            is EnumType -> String::class.asClassName()
            is FloatType -> Float::class.asClassName()
            is InstantType -> Instant::class.asClassName()
            is LongType -> Long::class.asClassName()
            is MapType -> Map::class.asClassName().parameterizedBy(
                this.keyType.asTypeName(options),
                this.valueType.asTypeName(options)
            )
            is NumericType -> BigDecimal::class.asClassName()
            is OccurrencesType -> List::class.asClassName().parameterizedBy(this.memberType.asTypeName(options))
            is StringType -> String::class.asClassName()
            is StringConstantType -> String::class.asClassName()
            is TimestampMillisType -> LocalDateTime::class.asClassName()
            is CoercibleType<*, *> -> when (options.coercibleHandler) {
                CoercibleHandler.FROM -> {
                    val fromBaleenType = createCoercibleFromType()
                    fromBaleenType.asTypeName(options)
                }
                CoercibleHandler.TO -> this.type.asTypeName(options)
            }
            else -> throw Exception("Unknown type: " + this::class)
        }
    }

    private fun CoercibleType<*, *>.createCoercibleFromType(): BaleenType {
        val superCoercibleType = findSuper(CoercibleType::class.starProjectedType, this::class)
        val fromClass = superCoercibleType?.arguments?.firstOrNull()?.type?.classifier

        return if (fromClass == null) {
            throw IllegalArgumentException(
                "Unable to handle CoercibleType FROM type for Type '${this::class}': null"
            )
        } else {
            try {
                (fromClass as KClass<*>).createInstance() as BaleenType
            } catch (e: Exception) {
                throw IllegalArgumentException(
                    "Unable to handle CoercibleType FROM type for Type '${this::class}': $fromClass",
                    e
                )
            }
        }
    }

    private fun findSuper(toFind: KType, clazz: KClass<*>): KType? {
        val parent = clazz.supertypes.firstOrNull { it.isSubtypeOf(toFind) }
        val projectedType = parent?.classifier?.starProjectedType
        return when {
            projectedType == null -> null
            projectedType == toFind -> parent
            else -> findSuper(toFind, parent.classifier as KClass<*>)
        }
    }

    internal fun CodeBlock.Builder.addDefaultValue(baleenType: BaleenType, defaultValue: Any?): CodeBlock.Builder =
        this.apply {
            when (defaultValue) {
                NoDefault -> {
                    // Do nothing
                }
                null -> add("null")
                else ->
                    when (baleenType) {
                        is IntegerType -> add("\"%L\".toBigInteger()", defaultValue)
                        is NumericType -> add("\"%L\".toBigDecimal()", defaultValue)
                        is OccurrencesType -> if (defaultValue is Iterable<*>) {
                            val defaultValueList = defaultValue.toList()
                            if (defaultValueList.isEmpty()) {
                                add("emptyList()")
                            } else {
                                add("listOf(")
                                defaultValueList.forEachIndexed { i, v ->
                                    if (i > 0) add(", ")
                                    addDefaultValue(baleenType.memberType, v)
                                }
                                add(")")
                            }
                        }
                        is MapType -> if (defaultValue is Map<*, *>) {
                            if (defaultValue.isEmpty()) {
                                add("emptyMap()")
                            } else {
                                add("mapOf(")
                                defaultValue.toList().forEachIndexed { i, (key, value) ->
                                    if (i > 0) add(", ")
                                    addDefaultValue(baleenType.keyType, key)
                                    add(" to ")
                                    addDefaultValue(baleenType.valueType, value)
                                }
                                add(")")
                            }
                        }
                        else ->
                            when (defaultValue) {
                                is String -> add("%S", defaultValue)
                                is Boolean -> add("%L", defaultValue)
                                is Int -> add("%L", defaultValue)
                                is Long -> add("%LL", defaultValue)
                                is Float -> add("%Lf", defaultValue)
                                is Double -> add("%L", defaultValue)
                                is Enum<*> -> add("%S", defaultValue.name)
                                is Instant -> add("%T.parse(%S)", Instant::class, defaultValue.toString())
                                is LocalDateTime -> add("%T.parse(%S)", LocalDateTime::class, defaultValue.toString())
                                else -> add("%T()", defaultValue::class.asClassName())
                            }
                    }
            }
        }

    internal fun TypeSpec.Builder.addAttributeDescription(attr: AttributeDescription, options: Options): TypeSpec.Builder {
        val typeName = attr.type.asTypeName(options)
        return this.addProperty(
            PropertySpec.builder(attr.name, typeName)
                .addKdoc(attr.markdownDescription)
                .initializer(attr.name)
                .build()
        )
    }

    internal fun ParameterSpec.Builder.defaultValue(baleenType: BaleenType, defaultValue: Any?): ParameterSpec.Builder =
        this.apply {
            if (defaultValue != NoDefault) {
                defaultValue(CodeBlock.builder().addDefaultValue(baleenType, defaultValue).build())
            }
        }

    internal fun FunSpec.Builder.addAttributeDescription(attr: AttributeDescription, options: Options): FunSpec.Builder {
        val typeName = attr.type.asTypeName(options)
        return addParameter(ParameterSpec.builder(attr.name, typeName).defaultValue(attr.type, attr.default).build())
    }

    internal fun TypeSpec.Builder.addDataDescription(dataDescription: DataDescription, options: Options): TypeSpec.Builder {
        return this
            .addAnnotation(com.shoprunner.baleen.annotation.DataDescription::class)
            .addKdoc(dataDescription.markdownDescription)
            .addModifiers(KModifier.DATA)
            .primaryConstructor(FunSpec.constructorBuilder()
                .apply {
                    dataDescription.attrs.forEach {
                        addAttributeDescription(it, options)
                    }
                }.build()
            )
            .apply {
                dataDescription.attrs.forEach {
                    addAttributeDescription(it, options)
                }
            }
    }

    internal fun FileSpec.Builder.addDataDescription(dataDescription: DataDescription, options: Options): FileSpec.Builder {
        return this.addType(
            TypeSpec.classBuilder(dataDescription.asClassName())
                .addDataDescription(dataDescription, options)
                .build()
        )
    }

    internal fun DataDescription.asClassName(): ClassName = ClassName(this.nameSpace, this.name)

    internal fun BaleenType.flattenElements(options: Options): List<BaleenType> = when (this) {
        is AllowsNull<*> -> type.flattenElements(options)
        is OccurrencesType -> memberType.flattenElements(options)
        is MapType -> keyType.flattenElements(options) + valueType.flattenElements(options)
        is CoercibleType<*, *> -> when (options.coercibleHandler) {
            CoercibleHandler.TO -> type.flattenElements(options)
            CoercibleHandler.FROM -> createCoercibleFromType().flattenElements(options)
        }
        else -> listOf(this)
    }

    internal fun DataDescription.allSubTypes(options: Options, alreadyProcessed: Set<ClassName> = setOf(this.asClassName())): Set<DataDescription> {
        val newDataClasses = this.attrs.map { it.type }
            .flatMap { it.flattenElements(options) }
            .filterIsInstance<DataDescription>()
            .filter { it.asClassName() !in alreadyProcessed }

        return newDataClasses.flatMap {
            val ap = alreadyProcessed + newDataClasses.map { it.asClassName() }
            it.allSubTypes(options, ap) + it
        }.toSet()
    }

    fun encode(dataDescription: DataDescription, options: Options = Options(), alreadyProcessed: Set<ClassName> = emptySet()): Map<DataDescription, FileSpec> {
        val allSubTypes = dataDescription
            .allSubTypes(options, alreadyProcessed + dataDescription.asClassName())
            .filter { child -> options.overrides.none { override -> override.isOverridable(child) } }

        return (allSubTypes + dataDescription).map {
            it to FileSpec.builder(it.nameSpace, it.name)
                .addDataDescription(it, options)
                .build()
        }.toMap()
    }

    fun DataDescription.writeDataClassesTo(dir: File, options: Options = Options()) {
        encode(this, options).forEach { (_, fileSpec) -> fileSpec.writeTo(dir) }
    }
}
