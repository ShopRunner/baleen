package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult

class FloatType(val min: Float = Float.NEGATIVE_INFINITY, val max: Float = Float.POSITIVE_INFINITY) : BaleenType {
    override fun name() = "float"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when {
                value == null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                value !is Float -> sequenceOf(ValidationError(dataTrace, "is not a float", value))
                value < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                value > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                else -> emptySequence()
            }
}