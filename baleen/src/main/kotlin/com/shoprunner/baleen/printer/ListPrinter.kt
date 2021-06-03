package com.shoprunner.baleen.printer

import com.shoprunner.baleen.ValidationResult

class ListPrinter : Printer {
    private val list = mutableListOf<ValidationResult>()
    val capturedList: List<ValidationResult> get() = list.toList()

    fun print(validationResult: ValidationResult) {
        list.add(validationResult)
    }

    override fun print(validationResults: Iterable<ValidationResult>) {
        validationResults.forEach {
            print(it)
        }
    }
}
