package com.shoprunner.baleen.script

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.ValidationSuccess
import com.shoprunner.baleen.ValidationSummary
import com.shoprunner.baleen.ValidationWarning
import java.io.File

fun baleen(outputDir: String? = null, vararg outputs: Output, body: BaleenValidation.() -> Unit) {
    val results = BaleenValidation().apply(body).results
        .filterIsInstance<ValidationSummary>()
        .toList()

    outputDir?.let {
        File(it).deleteRecursively()
    }

    (outputs.takeIf { it.isNotEmpty() } ?: arrayOf(Output.console)).forEach { output ->
        when (output) {
            Output.console -> results.forEach { it.toConsole() }

            Output.csv -> {
                val dir = File(outputDir!!)
                dir.mkdirs()
                val summaryFile = File(dir, "summary.csv")
                println("Writing to ${summaryFile.absolutePath}")
                summaryFile.writeText("summary,numSuccesses,numInfos,numWarnings,numErrors,tags,dataTrace\n")
                results.forEach { summary ->
                    summaryFile.appendText("${summary.toCsv(",")}\n")

                    val errorsFile = File(dir, "${summary.summary}_${summary.dataTrace.tags.entries.joinToString("_") { "${it.key}-${it.value}" }}.csv")
                    println("Writing to ${errorsFile.absolutePath}")
                    errorsFile.writeText("type,message,value,tags,dataTrace\n")
                    summary.topErrorsAndWarnings.forEach { errorsFile.appendText("${it.toCsv()}\n") }
                }
            }

            Output.html -> {
                val dir = File(outputDir!!)
                dir.mkdirs()
                val file = File(dir, "summary.html")
                println("Writing to ${file.absolutePath}")
                file.writeText(
                    """
                    <html>
                       <head>
                         <title>Baleen Results</title>
                         <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x" crossorigin="anonymous">
                         <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/js/bootstrap.bundle.min.js" integrity="sha384-gtEjrD/SeCtmISkJkNUaaKMoLD0//ElJ19smozuHV6z3Iehds+3Ulb9Bn9Plx0x4" crossorigin="anonymous"></script>
                       </head>
                       <body>
                         <h2>Summary</h2>
                         <table class="table table-striped">
                            <thead>
                              <tr><th scope="col">summary</th><th scope="col">numSuccesses</th><th scope="col">numInfos</th><th scope="col">numWarnings</th><th scope="col">numErrors</th><th scope="col">tags</th><th scope="col">dataTrace</th></tr>
                            </thead>
                            <tbody>
                    """.trimIndent()
                )
                results.forEach { file.appendText("${it.toHtmlRow()}\n") }
                file.appendText(
                    """
                        </tbody>
                     </table>
                    """.trimIndent()
                )

                results.forEach { summary ->
                    file.appendText(
                        """
                        <h2>${summary.summary} - Top Errors and Warnings
                        <table class="table table-striped">
                          <thead>
                            <tr><th scope="col">type</th><th scope="col">message</th><th scope="col">value</th><th scope="col">tags</th><th scope="col">dataTrace</th></tr>
                          </thead>
                          <tbody>
                        """.trimIndent()
                    )
                    summary.topErrorsAndWarnings.forEach { file.appendText("${it.toHtmlRow()}\n") }
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

            Output.text -> {
                val dir = File(outputDir!!)
                dir.mkdirs()
                val file = File(dir, "summary.txt")
                println("Writing to ${file.absolutePath}")
                file.writeText("")
                results.forEach { file.appendText("${it.toText()}\n") }
            }
        }
    }
}

internal fun ValidationResult.toCsv(separator: String = ","): String =
    when (this) {
        is ValidationInfo ->
            listOf(
                "INFO",
                this.message,
                this.value?.toString(),
                dataTrace.tags.entries.joinToString("|") { "${it.key}=${it.value}" },
                dataTrace.toList().joinToString("|")
            ).joinToString(separator)
        is ValidationSuccess ->
            listOf(
                "SUCCESS",
                "",
                this.value?.toString(),
                dataTrace.tags.entries.joinToString("|") { "${it.key}=${it.value}" },
                dataTrace.toList().joinToString("|")
            ).joinToString(separator)

        is ValidationWarning ->
            listOf(
                "WARNING",
                this.message,
                this.value?.toString(),
                dataTrace.tags.entries.joinToString("|") { "${it.key}=${it.value}" },
                dataTrace.toList().joinToString("|")
            ).joinToString(separator)

        is ValidationError ->
            listOf(
                "ERROR",
                this.message,
                this.value?.toString(),
                dataTrace.tags.entries.joinToString("|") { "${it.key}=${it.value}" },
                dataTrace.toList().joinToString("|")
            ).joinToString(separator)

        is ValidationSummary ->
            listOf(
                this.summary,
                this.numSuccesses,
                this.numInfos,
                this.numWarnings,
                this.numErrors,
                dataTrace.tags.entries.joinToString("|") { "${it.key}=${it.value}" },
                dataTrace.toList().joinToString("|")
            ).joinToString(separator)

        else -> ""
    }

internal fun ValidationResult.toHtmlRow(): String =
    when (this) {
        is ValidationInfo ->
            listOf(
                "INFO",
                this.message,
                this.value?.toString(),
                dataTrace.tags.tagsToHtml(),
                dataTrace.toList().joinToString("|")
            ).joinToString(prefix = "<tr><td>", separator = "</td><td>", postfix = "</td></tr>")
        is ValidationSuccess ->
            listOf(
                "SUCCESS",
                "",
                this.value?.toString(),
                dataTrace.tags.tagsToHtml(),
                dataTrace.toList().joinToString("|")
            ).joinToString(prefix = "<tr><td>", separator = "</td><td>", postfix = "</td></tr>")

        is ValidationWarning ->
            listOf(
                "WARNING",
                this.message,
                this.value?.toString(),
                dataTrace.tags.tagsToHtml(),
                dataTrace.toList().joinToString("|")
            ).joinToString(prefix = "<tr><td>", separator = "</td><td>", postfix = "</td></tr>")

        is ValidationError ->
            listOf(
                "ERROR",
                this.message,
                this.value?.toString(),
                dataTrace.tags.tagsToHtml(),
                dataTrace.toList().joinToString("|")
            ).joinToString(prefix = "<tr><td>", separator = "</td><td>", postfix = "</td></tr>")

        is ValidationSummary ->
            listOf(
                this.summary,
                this.numSuccesses,
                this.numInfos,
                this.numWarnings,
                this.numErrors,
                dataTrace.tags.tagsToHtml(),
                dataTrace.toList().joinToString("|")
            ).joinToString(prefix = "<tr><td>", separator = "</td><td>", postfix = "</td></tr>")

        else -> ""
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

internal fun ValidationResult.toText(prettyPrint: Boolean = false): String =
    if (prettyPrint) {
        "${this.javaClass.name}(${jacksonObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this)})"
    } else {
        this.toString()
    }

internal fun ValidationResult.toConsole(): Unit = println(this.toText())
