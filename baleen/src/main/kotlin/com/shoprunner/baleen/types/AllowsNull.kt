package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationResult

class AllowsNull<out T : BaleenType>(val type: T) : BaleenType {
    override fun name() = "null or ${type.name()}"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when (value) {
                null -> emptySequence()
                else -> type.validate(dataTrace, value)
            }
}