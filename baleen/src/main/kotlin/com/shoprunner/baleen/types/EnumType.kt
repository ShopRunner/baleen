package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult

class EnumType(vararg val enum: String) : BaleenType {
    override fun name() = "enum"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when {
                value == null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                value is String && !enum.contains(value) ->
                    sequenceOf(ValidationError(dataTrace,
                            "is not contained in enum [${enum.joinToString(",")}]",
                            value))
                value is Enum<*> && !enum.contains(value.name) ->
                    sequenceOf(ValidationError(dataTrace,
                            "is not contained in enum [${enum.joinToString(",")}]",
                            value))
                else -> emptySequence()
            }
}