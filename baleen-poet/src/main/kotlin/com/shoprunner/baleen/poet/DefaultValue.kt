package com.shoprunner.baleen.poet

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.NoDefault
import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.NumericType
import com.shoprunner.baleen.types.OccurrencesType
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.asClassName
import java.time.Instant
import java.time.LocalDateTime

/**
 * Given the BaleenType and the default value of an attribute, write the default value to the CodeBlock.
 *
 * @receiver the CodeBlock being written to
 * @param baleenType The BaleenType of the attribute contains a default value
 * @param defaultValue The default value to write
 * @return the CodeBlock being written to
 */
fun CodeBlock.Builder.addDefaultValue(baleenType: BaleenType, defaultValue: Any?): CodeBlock.Builder = this.apply {
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
