package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult

class DoubleType(val min: Double = Double.NEGATIVE_INFINITY, val max: Double = Double.POSITIVE_INFINITY) : BaleenType {
    override fun name() = "double"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when {
                value == null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                value !is Double -> sequenceOf(ValidationError(dataTrace, "is not a double", value))
                value < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                value > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                else -> emptySequence()
            }
}