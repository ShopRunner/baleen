package com.shoprunner.baleen.script

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.SummaryGroupBy
import com.shoprunner.baleen.createSummary
import com.shoprunner.baleen.printer.ConsolePrinter
import com.shoprunner.baleen.printer.CsvPrinter
import com.shoprunner.baleen.printer.HtmlPrinter
import com.shoprunner.baleen.printer.LogPrinter
import com.shoprunner.baleen.printer.TextPrinter
import java.io.File

fun validate(
    description: BaleenType,
    data: DataAccess,
    outputDir: String? = null,
    groupBy: SummaryGroupBy = { emptyMap() },
    vararg outputs: Output,
) {
    validate(description, data, outputDir?.let { File(it) }, groupBy, *outputs)
}

fun validate(
    description: BaleenType,
    data: DataAccess,
    outputDir: File? = null,
    groupBy: SummaryGroupBy = { emptyMap() },
    vararg outputs: Output,
) {

    val results = data(description).createSummary(groupBy = groupBy).toList()

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
                File(dir!!, "summary.html").writer().use {
                    HtmlPrinter(it).print(results)
                }
            }

            Output.text -> {
                File(dir!!, "summary.txt").writer().use {
                    TextPrinter(it, prettyPrint = true).print(results)
                }
            }

            Output.log -> LogPrinter().print(results)
        }
    }
}
