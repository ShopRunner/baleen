package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult
import java.time.Instant

class InstantType(val before: Instant = Instant.MAX, val after: Instant = Instant.MIN) : BaleenType {
    override fun name() = "instant"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when (value) {
                null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                !is Instant -> sequenceOf(ValidationError(dataTrace, "is not an instant", value))
                !value.isBefore(before) -> sequenceOf(ValidationError(dataTrace, "is before $before", value))
                !value.isAfter(after) -> sequenceOf(ValidationError(dataTrace, "is after $after", value))
                else -> emptySequence()
            }
}