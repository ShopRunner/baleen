package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.ValidationWarning
import com.shoprunner.baleen.Validator

/**
 * Baleen Type wrapper that turns all `ValidationError`s into `ValidationWarning`s
 */
class ErrorsAreWarnings<out T : BaleenType>(val type: T) : BaleenType {
    override fun name(): String = type.name()

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
        type.validate(dataTrace, value).asWarnings()
}

/**
 * Transforms the validation errors into warnings
 */
fun <T : BaleenType> T.asWarnings(): ErrorsAreWarnings<T> = ErrorsAreWarnings(this)

/**
 * Wraps the Validator function with a Validator function that maps errors into warnings.
 */
fun Validator.asWarnings(): Validator = { dataTrace, data ->
    this(dataTrace, data).asWarnings()
}

/**
 * Iterates through the validation results and transforms errors into warnings
 */
fun Sequence<ValidationResult>.asWarnings(): Sequence<ValidationResult> = this.map {
    when (it) {
        is ValidationError -> ValidationWarning(it.dataTrace, it.message, it.value)
        else -> it
    }
}
