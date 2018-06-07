package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult

open class StringCoercibleToType<out T : BaleenType>(val type: T, private val converter: (String) -> Any?) : BaleenType {
    override fun name() = "string coercible to ${type.name()}"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when (value) {
                null -> type.validate(dataTrace, value)
                !is String -> sequenceOf(ValidationError(dataTrace, "is not a string", value))
                else -> {
                    val newType = converter(value)
                    if (newType == null) {
                        sequenceOf(ValidationError(dataTrace, "could not be parsed to ${type.name()}.", value))
                    } else {
                        type.validate(dataTrace, newType)
                    }
                }
            }
}