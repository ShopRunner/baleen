package com.shoprunner.baleen.script

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.SummaryGroupBy
import com.shoprunner.baleen.createSummary
import com.shoprunner.baleen.printer.ConsolePrinter
import com.shoprunner.baleen.printer.Printer

fun validate(
    description: BaleenType,
    data: DataAccess,
    groupBy: SummaryGroupBy = { emptyMap() },
    vararg printers: Printer,
) {
    val results = data(description).createSummary(groupBy = groupBy).toList()

    (printers.takeIf { it.isNotEmpty() } ?: arrayOf(ConsolePrinter)).forEach { printer ->
        printer.print(results)
    }
}
