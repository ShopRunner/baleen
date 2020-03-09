package com.shoprunner.baleen.poet

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.CoercibleType
import com.shoprunner.baleen.types.DoubleType
import com.shoprunner.baleen.types.EnumType
import com.shoprunner.baleen.types.ErrorsAreWarnings
import com.shoprunner.baleen.types.FloatType
import com.shoprunner.baleen.types.InstantType
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.LongCoercibleToInstant
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.NumericType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringCoercibleToInstant
import com.shoprunner.baleen.types.StringCoercibleToTimestamp
import com.shoprunner.baleen.types.StringConstantType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.UnionType
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.asClassName
import java.time.Instant
import java.time.format.DateTimeFormatter

typealias TypeMapper = (CodeBlock.Builder, BaleenType) -> CodeBlock.Builder

fun recursiveTypeMapper(typeMapper: TypeMapper): TypeMapper {
    return { codeBlockBuilder, baleenType -> defaultTypeMapper(codeBlockBuilder, baleenType, typeMapper) }
}

fun defaultTypeMapper(codeBlockBuilder: CodeBlock.Builder, baleenType: BaleenType): CodeBlock.Builder {
    return defaultTypeMapper(codeBlockBuilder, baleenType, recursiveTypeMapper(::defaultTypeMapper))
}

fun defaultTypeMapper(codeBlockBuilder: CodeBlock.Builder, baleenType: BaleenType, typeMapper: TypeMapper): CodeBlock.Builder = codeBlockBuilder.apply {
    when (baleenType) {
        // Data Description
        // Imports the type rather than generating nested data descriptions
        is DataDescription -> {
            add("%M", MemberName(baleenType.nameSpace, baleenType.name))
        }

        // Complex Types
        is AllowsNull<*> -> {
            add("%T(", AllowsNull::class)
            typeMapper(this, baleenType.type)
            add(")")
        }
        is OccurrencesType -> {
            add("%T(", OccurrencesType::class)
            typeMapper(this, baleenType.memberType)
            add(")")
        }
        is MapType -> {
            add("%T(", MapType::class)
            typeMapper(this, baleenType.keyType)
            add(",")
            typeMapper(this, baleenType.valueType)
            add(")")
        }
        is UnionType -> {
            add("%T(", UnionType::class)
            baleenType.types.forEachIndexed { i, t ->
                if (i > 0) add(", ")
                typeMapper(this, t)
            }
            add(")")
        }
        is ErrorsAreWarnings<*> -> {
            add("%T(", ErrorsAreWarnings::class)
            typeMapper(this, baleenType.type)
            add(")")
        }

        // Numeric Types
        is FloatType -> {
            add("%T(min = %L, max = %L)", FloatType::class,
                when {
                    baleenType.min == Float.NEGATIVE_INFINITY -> "Float.NEGATIVE_INFINITY"
                    baleenType.min == Float.POSITIVE_INFINITY -> "Float.POSITIVE_INFINITY"
                    baleenType.min == Float.MIN_VALUE -> "Float.MIN_VALUE"
                    baleenType.min == Float.MAX_VALUE -> "Float.MAX_VALUE"
                    else -> "${baleenType.min}f"
                },
                when {
                    baleenType.max == Float.NEGATIVE_INFINITY -> "Float.NEGATIVE_INFINITY"
                    baleenType.max == Float.POSITIVE_INFINITY -> "Float.POSITIVE_INFINITY"
                    baleenType.max == Float.MIN_VALUE -> "Float.MIN_VALUE"
                    baleenType.max == Float.MAX_VALUE -> "Float.MAX_VALUE"
                    else -> "${baleenType.max}f"
                }
            )
        }
        is DoubleType -> {
            add("%T(min = %L, max = %L)", DoubleType::class,
                when {
                    baleenType.min == Double.NEGATIVE_INFINITY -> "Double.NEGATIVE_INFINITY"
                    baleenType.min == Double.POSITIVE_INFINITY -> "Double.POSITIVE_INFINITY"
                    baleenType.min == Double.MIN_VALUE -> "Double.MIN_VALUE"
                    baleenType.min == Double.MAX_VALUE -> "Double.MAX_VALUE"
                    else -> baleenType.min
                },
                when {
                    baleenType.max == Double.NEGATIVE_INFINITY -> "Double.NEGATIVE_INFINITY"
                    baleenType.max == Double.POSITIVE_INFINITY -> "Double.POSITIVE_INFINITY"
                    baleenType.max == Double.MIN_VALUE -> "Double.MIN_VALUE"
                    baleenType.max == Double.MAX_VALUE -> "Double.MAX_VALUE"
                    else -> baleenType.max
                }
            )
        }
        is IntegerType -> {
            add("%T(min = %L, max = %L)", IntegerType::class,
                baleenType.min?.let { "\"$it\".toBigInteger()" },
                baleenType.max?.let { "\"$it\".toBigInteger()" }
            )
        }
        is IntType -> {
            add("%T(min = %L, max = %L)", IntType::class,
                baleenType.min.takeIf { it > Int.MIN_VALUE } ?: "Int.MIN_VALUE",
                baleenType.max.takeIf { it < Int.MAX_VALUE } ?: "Int.MAX_VALUE"
            )
        }
        is LongType -> {
            add("%T(min = %L, max = %L)", LongType::class,
                baleenType.min.takeIf { it > Long.MIN_VALUE }?.let { "${it}L" } ?: "Long.MIN_VALUE",
                baleenType.max.takeIf { it < Long.MAX_VALUE }?.let { "${it}L" } ?: "Long.MAX_VALUE"
            )
        }
        is NumericType -> {
            add("%T(min = %L, max = %L)", NumericType::class,
                baleenType.min?.let { "\"$it\".toBigDecimal()" },
                baleenType.max?.let { "\"$it\".toBigDecimal()" }
            )
        }

        // Time types
        is InstantType -> {
            add("%T(before = ", InstantType::class)
            when {
                baleenType.before == Instant.MIN -> add("%T.MIN", Instant::class)
                baleenType.before == Instant.MAX -> add("%T.MAX", Instant::class)
                baleenType.before == Instant.EPOCH -> add("%T.EPOCH", Instant::class)
                else -> add("%T.parse(%S)", Instant::class, baleenType.before.toString())
            }
            add(", after = ")
            when {
                baleenType.after == Instant.MIN -> add("%T.MIN", Instant::class)
                baleenType.after == Instant.MAX -> add("%T.MAX", Instant::class)
                baleenType.after == Instant.EPOCH -> add("%T.EPOCH", Instant::class)
                else -> add("%T.parse(%S)", Instant::class, baleenType.after.toString())
            }
            add(")")
        }

        // String types
        is EnumType -> {
            add("%T(%S, listOf(", EnumType::class, baleenType.enumName)
            baleenType.enum.forEachIndexed { i, enum ->
                if (i > 0) add(", ")
                add("%S", enum)
            }
            add("))")
        }
        is StringConstantType -> add("%T(%S)", StringConstantType::class, baleenType.constant)
        is StringType -> {
            add("%T(min = %L, max = %L)", StringType::class,
                baleenType.min,
                baleenType.max.takeIf { it < Int.MAX_VALUE } ?: "Int.MAX_VALUE"
            )
        }

        // Coercible types
        is LongCoercibleToInstant -> {
            add("%T(", LongCoercibleToInstant::class)
            typeMapper(this, baleenType.type)
            add(", %M)", MemberName(LongCoercibleToInstant.Precision::class.asClassName(), baleenType.precision.name))
        }
        is StringCoercibleToInstant -> {
            add("%T(", StringCoercibleToInstant::class)
            typeMapper(this, baleenType.type)
            if (baleenType.dateTimeFormatter == DateTimeFormatter.ISO_INSTANT) {
                add(", %M)", MemberName(DateTimeFormatter::class.asClassName(), "ISO_INSTANT"))
            } else if (baleenType.pattern != null) {
                add(
                    ", %M(%S))",
                    MemberName(DateTimeFormatter::class.asClassName(), "ofPattern"),
                    baleenType.pattern
                )
            } else {
                throw BaleenPoetException("Unable to generate Kotlin code for StringCoercibleToInstant without a DateTimeFormatter pattern string." +
                        "Use StringCoercibleToInstant(InstantType, String) constructor")
            }
        }
        is StringCoercibleToTimestamp -> {
            add("%T(", StringCoercibleToTimestamp::class)
            typeMapper(this, baleenType.type)
            if (baleenType.dateTimeFormatter == DateTimeFormatter.ISO_LOCAL_DATE_TIME) {
                add(", %M)", MemberName(DateTimeFormatter::class.asClassName(), "ISO_LOCAL_DATE_TIME"))
            } else if (baleenType.pattern != null) {
                add(
                    ", %M(%S))",
                    MemberName(DateTimeFormatter::class.asClassName(), "ofPattern"),
                    baleenType.pattern
                )
            } else {
                throw BaleenPoetException("Unable to generate Kotlin code for StringCoercibleToTimestamp without a DateTimeFormatter pattern string." +
                        "Use StringCoercibleToTimestamp(TimestampMillisType, String) constructor")
            }
        }
        // Most Coercible types default to XCoercibleToY(yType)
        is CoercibleType<*, *> -> {
            add("%T(", baleenType::class)
            typeMapper(this, baleenType.type)
            add(")")
        }

        // Simple implementations like BooleanType and TimestampMillisType default to the same
        else -> add("%T()", baleenType::class)
    }
}
