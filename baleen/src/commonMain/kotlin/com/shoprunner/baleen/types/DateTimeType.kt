package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult
import com.soywiz.klock.DateTime

class DateTimeType : BaleenType {
    override val name = "datateTime"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when (value) {
                null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                !is DateTime -> sequenceOf(ValidationError(dataTrace, "is not a DateTime", value))
                else -> emptySequence()
            }
}