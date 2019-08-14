package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.ValidationWarning

class ErrorsAreWarnings<out T : BaleenType>(val type: T) : BaleenType {
    override fun name() = "null or ${type.name()}"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            type.validate(dataTrace, value).map { when (it) {
                is ValidationError -> ValidationWarning(it.dataTrace, it.message, it.value)
                else -> it
                }
            }
}
