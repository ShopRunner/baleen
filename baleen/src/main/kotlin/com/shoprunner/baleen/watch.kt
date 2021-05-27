@file:JvmName("ResultsWatcher")

package com.shoprunner.baleen

import com.shoprunner.baleen.printer.ConsolePrinter
import com.shoprunner.baleen.printer.Printer

/**
 * Alias for function that "watches" a chunk of the Validation Results and does some side-effect.
 */
typealias WatchBody = (Iterable<ValidationResult>) -> Unit

/**
 * Given a [Sequence], watch the results and print out intermediate summaries along the way. It consolidates
 * This is a side-effect function that outputs results but does not do transformations.
 *
 * @receiver The [ValidationResult] sequence we want to watch.
 * @param windowSize The size of the window before printing a summary. Default 1000.
 * @param groupBy The group by function of which the summaries are printed too. Default is `emptyMap()`.
 * @param watchBody A function that takes an sequence of [ValidationResult] and does something with it.
 * Default is Console printing.
 * @return The sequence of [ValidationResult] with the same elements as the original.
 */
fun Sequence<ValidationResult>.watch(
    windowSize: Int = 1000,
    groupBy: SummaryGroupBy = { emptyMap() },
    watchBody: WatchBody = printWatcher(),
): Sequence<ValidationResult> {
    return sequence {
        var rollingWindow = mutableListOf<ValidationResult>()
        var isExtra = true

        this@watch.forEachIndexed { index, validationResult ->
            rollingWindow.add(validationResult)

            // Print out summary every X
            // Consolidate rolling window to the summary so not to hold too much in memory
            val idxP1 = index + 1
            val mod = idxP1 % windowSize

            if (mod == 0) {
                val summary = rollingWindow
                    .createSummary(numErrorsWarningsToKeep = 0, groupBy = groupBy)
                    .toMutableList()
                watchBody(summary)
                rollingWindow = summary
                isExtra = false
            } else {
                isExtra = true
            }

            yield(validationResult)
        }

        // Print final summary for any extra
        if (isExtra) {
            val summary = rollingWindow
                .createSummary(numErrorsWarningsToKeep = 0, groupBy = groupBy)
                .toList()
            watchBody(summary)
        }
    }
}

/**
 * Given an [Iterable], watch the results and print out intermediate summaries along the way. It consolidates
 * This is a side-effect function that outputs results but does not do transformations. The output is lazily
 * executed once the [Iterable] functions are called.
 *
 * @receiver The [ValidationResult] sequence we want to watch.
 * @param windowSize The size of the window before printing a summary. Default 1000.
 * @param groupBy The group by function of which the summaries are printed too. Default is `emptyMap()`.
 * @param watcher A function that takes an Iterable of [ValidationResult] and does something with it.
 * Default is Console printing.
 * @return The sequence as an [Iterable] of [ValidationResult] with the same elements as the original Iterable.
 */
fun Iterable<ValidationResult>.watch(
    windowSize: Int = 1000,
    groupBy: SummaryGroupBy = { emptyMap() },
    watcher: WatchBody = printWatcher(),
): Iterable<ValidationResult> {
    return this.asSequence().watch(windowSize, groupBy, watcher).asIterable()
}

/**
 * Given a [Printer], return a function that takes [ValidationResult] and prints it.
 *
 * @param printer The printer to which each summary is sent to. Default is [ConsolePrinter].
 * @return a function that takes [ValidationResult] and prints it.
 */
fun printWatcher(printer: Printer = ConsolePrinter): WatchBody = { printer.print(it) }
