package com.shoprunner.baleen.printer

import com.shoprunner.baleen.ValidationResult
import java.util.logging.Level
import java.util.logging.Logger

class LogPrinter(
    val logger: Logger = Logger.getLogger(LogPrinter::class.qualifiedName),
    val logLevel: Level = Level.INFO
) : Printer {

    fun print(validationResult: ValidationResult) {
        logger.log(logLevel, "$validationResult")
    }

    override fun print(validationResults: Iterable<ValidationResult>) {
        validationResults.forEach { print(it) }
    }
}
