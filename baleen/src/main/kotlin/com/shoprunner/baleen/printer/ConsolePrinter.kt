package com.shoprunner.baleen.printer

import com.shoprunner.baleen.ValidationResult

/**
 * Prints to stdout. It erases the line before writing the next on
 */
object ConsolePrinter : Printer {
    override fun print(validationResult: ValidationResult) {
        kotlin.io.print(validationResult)
    }

    override fun print(validationResults: Iterable<ValidationResult>) {
        validationResults.forEachIndexed { index, validationResult ->
            if (index != 0) {
                print("\r")
            }
            print(validationResult)
        }
        println()
    }
}
