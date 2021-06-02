package com.shoprunner.baleen.printer

import com.shoprunner.baleen.ValidationResult

interface Printer {
    fun print(validationResults: Iterable<ValidationResult>)

    fun Iterable<ValidationResult>.printAll() = print(this)
}
