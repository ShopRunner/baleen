package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult

class BooleanType() : BaleenType {
    override fun name() = "boolean"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when {
                value == null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                value !is Boolean -> sequenceOf(ValidationError(dataTrace, "is not a boolean", value))
                else -> emptySequence()
            }
}