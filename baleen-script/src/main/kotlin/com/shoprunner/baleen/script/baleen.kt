package com.shoprunner.baleen.script

import com.shoprunner.baleen.ValidationSummary
import com.shoprunner.baleen.printer.ConsolePrinter
import com.shoprunner.baleen.printer.CsvPrinter
import com.shoprunner.baleen.printer.HtmlPrinter
import com.shoprunner.baleen.printer.LogPrinter
import com.shoprunner.baleen.printer.TextPrinter
import com.shoprunner.baleen.watch
import java.io.File

/**
 * Container for executing Baleen Script.
 *
 * @param outputDir The directory to write file based outputs. Default `null`.
 * @param outputs They type of outputs to emit. Default [Output.console]
 * @param body The data tests
 * @sample
 * baleen("output", Output.console, Output.html) {
 *   csv("./example.csv") {
 *     "ID".type(StringType())
 *     "NAME".type(StringType())
 *   }
 * }

 */
fun baleen(outputDir: String? = null, vararg outputs: Output, body: BaleenValidation.() -> Unit) {
    baleen(outputDir?.let { File(it) }, *outputs) {
        body()
    }
}

/**
 * Container for executing Baleen Script
 *
 * @param outputDir The directory to write file based outputs. Default `null`.
 * @param outputs They type of outputs to emit. Default [Output.console]
 * @param body The data tests
 * @sample
 * baleen(File("output"), Output.console, Output.html) {
 *   csv("./example.csv") {
 *     "ID".type(StringType())
 *     "NAME".type(StringType())
 *   }
 * }
 */
fun baleen(outputDir: File? = null, vararg outputs: Output, body: BaleenValidation.() -> Unit) {
    val results = BaleenValidation().apply(body).results
        .let { if (Output.console in outputs) it.watch() else it }
        .filterIsInstance<ValidationSummary>()
        .toList()

    val dir = outputDir?.apply {
        deleteRecursively()
        mkdirs()
    }

    (outputs.takeIf { it.isNotEmpty() } ?: arrayOf(Output.console)).forEach { output ->
        when (output) {
            Output.console -> ConsolePrinter.print(results)

            Output.csv -> {
                CsvPrinter(dir!!).print(results)
            }

            Output.html -> {
                val file = File(dir!!, "summary.html")
                HtmlPrinter(file).print(results)
            }
            Output.text -> {
                val file = File(dir!!, "summary.txt")
                TextPrinter(file, prettyPrint = true).print(results)
            }
            Output.log -> LogPrinter().print(results)
        }
    }
}
