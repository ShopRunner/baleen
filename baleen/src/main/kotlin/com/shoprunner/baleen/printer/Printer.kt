package com.shoprunner.baleen.printer

import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.ValidationSuccess
import com.shoprunner.baleen.ValidationSummary
import com.shoprunner.baleen.ValidationWarning
import java.io.File

sealed class Printer {
    abstract fun print(validationResult: ValidationResult)
    abstract fun print(validationResults: Iterable<ValidationResult>)

    fun Iterable<ValidationResult>.printAll() = print(this)
}

object ConsolePrinter : Printer() {
    override fun print(validationResult: ValidationResult) {
        println(validationResult)
    }

    override fun print(validationResults: Iterable<ValidationResult>) {
        validationResults.forEach {
            print(it)
        }
    }
}

class TextPrinter(val file: File, val prettyPrint: Boolean = false) : Printer() {
    override fun print(validationResult: ValidationResult) {
        if (prettyPrint) {
            val output = validationResult.toString()
                .replace("(", "(\n")
                .replace("[", "[\n")
                .replace(",", ",\n")
                .replace(")", "\n)")
                .replace("]", "\n]")
            file.writeText("$output\n")
        } else {
            file.writeText("$validationResult\n")
        }
    }

    override fun print(validationResults: Iterable<ValidationResult>) {
        validationResults.forEach {
            print(it)
        }
    }
}

class HtmlPrinter(private val file: File) : Printer() {
    override fun print(validationResult: ValidationResult) {
        val output = when (validationResult) {
            is ValidationInfo ->
                listOf(
                    "INFO",
                    validationResult.message,
                    validationResult.value?.toString(),
                    validationResult.dataTrace.tags.tagsToHtml(),
                    validationResult.dataTrace.toList().joinToString("|")
                ).joinToString(prefix = "<tr><td>", separator = "</td><td>", postfix = "</td></tr>")
            is ValidationSuccess ->
                listOf(
                    "SUCCESS",
                    "",
                    validationResult.value?.toString(),
                    validationResult.dataTrace.tags.tagsToHtml(),
                    validationResult.dataTrace.toList().joinToString("|")
                ).joinToString(prefix = "<tr><td>", separator = "</td><td>", postfix = "</td></tr>")

            is ValidationWarning ->
                listOf(
                    "WARNING",
                    validationResult.message,
                    validationResult.value?.toString(),
                    validationResult.dataTrace.tags.tagsToHtml(),
                    validationResult.dataTrace.toList().joinToString("|")
                ).joinToString(prefix = "<tr><td>", separator = "</td><td>", postfix = "</td></tr>")

            is ValidationError ->
                listOf(
                    "ERROR",
                    validationResult.message,
                    validationResult.value?.toString(),
                    validationResult.dataTrace.tags.tagsToHtml(),
                    validationResult.dataTrace.toList().joinToString("|")
                ).joinToString(prefix = "<tr><td>", separator = "</td><td>", postfix = "</td></tr>")

            is ValidationSummary ->
                listOf(
                    validationResult.summary,
                    validationResult.numSuccesses,
                    validationResult.numInfos,
                    validationResult.numWarnings,
                    validationResult.numErrors,
                    validationResult.dataTrace.tags.tagsToHtml(),
                    validationResult.dataTrace.toList().joinToString("|")
                ).joinToString(prefix = "<tr><td>", separator = "</td><td>", postfix = "</td></tr>")

            else -> ""
        }
        file.writeText("$output\n")
    }

    override fun print(validationResults: Iterable<ValidationResult>) {
        file.writeText(
            """
                    <html>
                       <head>
                         <title>Baleen Results</title>
                         <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x" crossorigin="anonymous">
                         <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-gtEjrD/SeCtmISkJkNUaaKMoLD0//ElJ19smozuHV6z3Iehds+3Ulb9Bn9Plx0x4" crossorigin="anonymous"></script>
                       </head>
                       <body>
                """
        )

        val output = validationResults.toList()
        val summaryOutput = validationResults.filterIsInstance<ValidationSummary>()
        if (summaryOutput.isNotEmpty()) {
            print(summaryOutput)
        }
        val otherOutput = output.filterNot { it is ValidationSummary }
        if (otherOutput.isNotEmpty()) {
            file.appendText(
                """
                <h2>All results</h2>
                <table class="table table-striped">
                  <thead>
                    <tr><th scope="col">type</th><th scope="col">message</th><th scope="col">value</th><th scope="col">tags</th><th scope="col">dataTrace</th></tr>
                  </thead>
                  <tbody>
                """.trimIndent()
            )
            otherOutput.forEach { print(it) }
            file.appendText(
                """
                    </tbody>
                  </table>
                """.trimIndent()
            )
        }

        file.appendText(
            """
               </body>
             </html>
            """.trimIndent()
        )
    }

    fun print(validationResults: List<ValidationSummary>) {
        file.writeText(
            """
               <body>
                 <h2>Summary</h2>
                 <table class="table table-striped">
                    <thead>
                      <tr><th scope="col">summary</th><th scope="col">numSuccesses</th><th scope="col">numInfos</th><th scope="col">numWarnings</th><th scope="col">numErrors</th><th scope="col">tags</th><th scope="col">dataTrace</th></tr>
                    </thead>
                    <tbody>
            """.trimIndent()
        )
        validationResults.forEach { file.appendText("${print(it)}\n") }
        file.appendText(
            """
                        </tbody>
                     </table>
            """.trimIndent()
        )

        validationResults.forEach { summary ->
            file.appendText(
                """
                        <h2>${summary.summary} - Top Errors and Warnings</h2>
                        <table class="table table-striped">
                          <thead>
                            <tr><th scope="col">type</th><th scope="col">message</th><th scope="col">value</th><th scope="col">tags</th><th scope="col">dataTrace</th></tr>
                          </thead>
                          <tbody>
                """.trimIndent()
            )
            summary.topErrorsAndWarnings.forEach { file.appendText("${print(it)}\n") }
            file.appendText(
                """
                            </tbody>
                          </table>
                """.trimIndent()
            )
        }
    }

    private fun Map<String, String>.tagsToHtml() =
        """
        <table class="table mb-0">
          <thead>
            <tr><th scope="col">tag</th><th scope="col">value</th></tr>
          </thead>
          <tbody>
            ${entries.joinToString(
            prefix = "<tr>",
            separator = "</tr><tr>",
            postfix = "</tr>"
        ) { "<td>${it.key}</td><td>${it.value}</td>" }
        }
          </tbody>
        </table>
        """.trimIndent()
}

class CsvPrinter(val outputDir: File, val separator: String = ",") : Printer() {
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
        outputDir.writeText("$output\n")
    }

    override fun print(validationResults: Iterable<ValidationResult>) {
        val output = validationResults.toList()
        val summaryOutput = validationResults.filterIsInstance<ValidationSummary>()
        if (summaryOutput.isNotEmpty()) {
            val summaryFile = File(outputDir, "summary.csv")
            summaryFile.writeText("summary,numSuccesses,numInfos,numWarnings,numErrors,tags,dataTrace\n")
            summaryOutput.forEach { summary ->
                print(summary)

                val errorsFile = File(outputDir, "${summary.summary}_${summary.dataTrace.tags.entries.joinToString("_") { "${it.key}-${it.value}" }}.csv")
                println("Writing to ${errorsFile.absolutePath}")
                errorsFile.writeText("type,message,value,tags,dataTrace\n")
                summary.topErrorsAndWarnings.forEach { print(it) }
            }
        }
        val otherOutput = output.filterNot { it is ValidationSummary }
        if (otherOutput.isNotEmpty()) {
            val resultsFile = File(outputDir, "results.csv")
            println("Writing to ${resultsFile.absolutePath}")
            resultsFile.writeText("type,message,value,tags,dataTrace\n")
            otherOutput.forEach { print(it) }
        }
    }
}

class ListPrinter : Printer() {
    private val list = mutableListOf<ValidationResult>()
    val capturedList: List<ValidationResult> get() = list.toList()

    override fun print(validationResult: ValidationResult) {
        list.add(validationResult)
    }

    override fun print(validationResults: Iterable<ValidationResult>) {
        validationResults.forEach {
            print(it)
        }
    }
}
