package com.shoprunner.baleen.printer

import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.ValidationSuccess
import com.shoprunner.baleen.ValidationSummary
import com.shoprunner.baleen.ValidationWarning
import java.io.File
import java.io.OutputStreamWriter

class CsvPrinter(val outputDir: File, val separator: String = ",") : Printer {

    fun print(writer: OutputStreamWriter, validationResult: ValidationResult) {
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
        writer.append("$output\n")
    }

    override fun print(validationResults: Iterable<ValidationResult>) {
        val output = validationResults.toList()
        val summaryOutput = validationResults.filterIsInstance<ValidationSummary>()
        if (summaryOutput.isNotEmpty()) {
            File(outputDir, "summary.csv").writer().use { summaryWriter ->

                summaryWriter.write("summary,numSuccesses,numInfos,numWarnings,numErrors,tags,dataTrace\n")
                summaryOutput.forEach { summary ->
                    print(summaryWriter, summary)

                    val tagStr = if (summary.dataTrace.tags.isNotEmpty()) {
                        summary.dataTrace.tags.entries.joinToString(
                            prefix = "_",
                            separator = "_"
                        ) { "${it.key}-${it.value}" }
                    } else {
                        ""
                    }

                    File(
                        outputDir,
                        "errors_${summary.summary}$tagStr.csv"
                    ).writer().use { errorsFileWriter ->
                        errorsFileWriter.write("type,message,value,tags,dataTrace\n")
                        summary.topErrorsAndWarnings.forEach { print(errorsFileWriter, it) }
                    }
                }
            }
        }
        val otherOutput = output.filterNot { it is ValidationSummary }
        if (otherOutput.isNotEmpty()) {
            File(outputDir, "results.csv").writer().use { resultsFileWriter ->
                resultsFileWriter.write("type,message,value,tags,dataTrace\n")
                otherOutput.forEach { print(resultsFileWriter, it) }
            }
        }
    }
}
