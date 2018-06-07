package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult
import java.time.LocalDateTime

class TimestampMillisType : BaleenType {
    override fun name() = "timestampMillis"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when (value) {
                null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                !is LocalDateTime -> sequenceOf(ValidationError(dataTrace, "is not a a localDateTime", value))
                else -> emptySequence()
            }
}