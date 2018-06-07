package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult

class LongType(val min: Long = Long.MIN_VALUE, val max: Long = Long.MAX_VALUE) : BaleenType {
    override fun name() = "long"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when {
                value == null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                value !is Long -> sequenceOf(ValidationError(dataTrace, "is not a long", value))
                value < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                value > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                else -> sequenceOf()
            }
}