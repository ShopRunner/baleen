package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult

class StringConstantType(val constant: String) : BaleenType {
    override fun name() = "string constant"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when (constant) {
                value -> emptySequence()
                else -> sequenceOf(ValidationError(dataTrace, "value is not '$constant'", value))
            }
}