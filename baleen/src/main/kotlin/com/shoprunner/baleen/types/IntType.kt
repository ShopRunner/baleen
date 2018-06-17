package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult

class IntType(val min: Int = Int.MIN_VALUE, val max: Int = Int.MAX_VALUE) : BaleenType {
    override fun name() = "int"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when {
                value == null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                value !is Int -> sequenceOf(ValidationError(dataTrace, "is not an Int", value))
                value < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                value > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                else -> sequenceOf()
            }
}