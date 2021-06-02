package com.shoprunner.baleen.printer.jvm

import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.ValidationSuccess
import com.shoprunner.baleen.ValidationSummary
import com.shoprunner.baleen.ValidationWarning
import com.shoprunner.baleen.printer.Printer
import java.io.File

class CsvPrinter(val outputDir: File, val separator: String = ",") : Printer {

    private var currentFile: File? = File(outputDir, "results.csv")

    override fun print(validationResult: ValidationResult) {
        val output = when (validationResult) {
            is ValidationInfo ->
                listOf(
                    "INFO",
                    validationResult.message,
                    validationResult.value?.toString(),
                    validationResult.dataTrace.tags.entries.joinToString("|") { "${it.key}=${it.value}" },
                    validationResult.dataTrace.toList().joinToString("|")
                ).joinToString(separator)
            is ValidationSuccess ->
                listOf(
                    "SUCCESS",
                    "",
                    validationResult.value?.toString(),
                    validationResult.dataTrace.tags.entries.joinToString("|") { "${it.key}=${it.value}" },
                    validationResult.dataTrace.toList().joinToString("|")
                ).joinToString(separator)

            is ValidationWarning ->
                listOf(
                    "WARNING",
                    validationResult.message,
                    validationResult.value?.toString(),
                    validationResult.dataTrace.tags.entries.joinToString("|") { "${it.key}=${it.value}" },
                    validationResult.dataTrace.toList().joinToString("|")
                ).joinToString(separator)

            is ValidationError ->
                listOf(
                    "ERROR",
                    validationResult.message,
                    validationResult.value?.toString(),
                    validationResult.dataTrace.tags.entries.joinToString("|") { "${it.key}=${it.value}" },
                    validationResult.dataTrace.toList().joinToString("|")
                ).joinToString(separator)

            is ValidationSummary ->
                listOf(
                    validationResult.summary,
                    validationResult.numSuccesses,
                    validationResult.numInfos,
                    validationResult.numWarnings,
                    validationResult.numErrors,
                    validationResult.dataTrace.tags.entries.joinToString("|") { "${it.key}=${it.value}" },
                    validationResult.dataTrace.toList().joinToString("|")
                ).joinToString(separator)

            else -> ""
        }
        currentFile?.appendText("$output\n")
    }

    override fun print(validationResults: Iterable<ValidationResult>) {
        val output = validationResults.toList()
        val summaryOutput = validationResults.filterIsInstance<ValidationSummary>()
        if (summaryOutput.isNotEmpty()) {
            val summaryFile = File(outputDir, "summary.csv")
            summaryFile.writeText("summary,numSuccesses,numInfos,numWarnings,numErrors,tags,dataTrace\n")
            summaryOutput.forEach { summary ->
                currentFile = summaryFile
                print(summary)

                val tagStr = if (summary.dataTrace.tags.isNotEmpty()) {
                    summary.dataTrace.tags.entries.joinToString(prefix = "_", separator = "_") { "${it.key}-${it.value}" }
                } else {
                    ""
                }

                val errorsFile = File(
                    outputDir,
                    "errors_${summary.summary}$tagStr.csv"
                )
                currentFile = errorsFile
                println("Writing to ${errorsFile.absolutePath}")
                errorsFile.writeText("type,message,value,tags,dataTrace\n")
                summary.topErrorsAndWarnings.forEach { print(it) }
            }
        }
        val otherOutput = output.filterNot { it is ValidationSummary }
        if (otherOutput.isNotEmpty()) {
            val resultsFile = File(outputDir, "results.csv")
            currentFile = resultsFile
            println("Writing to ${resultsFile.absolutePath}")
            resultsFile.writeText("type,message,value,tags,dataTrace\n")
            otherOutput.forEach { print(it) }
        }
    }
}
