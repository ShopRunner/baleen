package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult

class StringType(val min: Int = 0, val max: Int = Int.MAX_VALUE) : BaleenType {
    override fun name() = "string"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when {
                value == null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                value !is String -> sequenceOf(ValidationError(dataTrace, "is not a string", value))
                value.length < min -> sequenceOf(ValidationError(dataTrace, "is not at least $min characters", value))
                value.length > max -> sequenceOf(ValidationError(dataTrace, "is more than $max characters", value))
                else -> emptySequence()
            }
}