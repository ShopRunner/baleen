package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult
import java.time.Instant

class InstantType : BaleenType {
    override fun name() = "instant"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when (value) {
                null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                !is Instant -> sequenceOf(ValidationError(dataTrace, "is not a instant", value))
                else -> emptySequence()
            }
}