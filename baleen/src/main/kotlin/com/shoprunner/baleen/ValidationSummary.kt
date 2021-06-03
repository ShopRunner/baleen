package com.shoprunner.baleen

/**
 * Container for summary validation warnings and errors.
 */
data class ValidationSummary(
    val dataTrace: DataTrace,
    val summary: String,
    val numInfos: Long,
    val numSuccesses: Long,
    val numErrors: Long,
    val numWarnings: Long,
    val topErrorsAndWarnings: List<ValidationResult> = emptyList()
) : ValidationResult()

typealias SummaryGroupBy = (ValidationResult) -> Map<String, String>

/**
 * Create a ValidationSummary by combining all results. This is lazily executed and returns a Sequence of ValidationSummary.
 *
 * @param groupBy Optional group-by function that returns a map to key the summary. Will add these as tags to the ValidationSummary dataTrace.
 * @param dataTrace a DataTrace to build upon
 * @param numErrorsWarningsToKeep Defaults to 100. The number of errors and warnings to return.
 */
fun Sequence<ValidationResult>.createSummary(
    dataTrace: DataTrace = dataTrace(),
    numErrorsWarningsToKeep: Int = 100,
    groupBy: SummaryGroupBy = { emptyMap() },
): Sequence<ValidationResult> {
    val seq = this
    return sequence {
        seq.groupingBy(groupBy)
            .fold(
                { key, _ ->
                    val summaryDetail = if (key.isNotEmpty()) {
                        val keyFormatted = key.map { (k, v) -> "$k=$v" }.joinToString()
                        " for $keyFormatted"
                    } else {
                        ""
                    }
                    ValidationSummary(dataTrace.tag(key), "Summary$summaryDetail", 0, 0, 0, 0, emptyList())
                },
                { _, accumulator, element ->
                    if (element is ValidationSummary) {
                        ValidationSummary(
                            accumulator.dataTrace,
                            accumulator.summary,
                            accumulator.numInfos + element.numInfos,
                            accumulator.numSuccesses + element.numSuccesses,
                            accumulator.numErrors + element.numErrors,
                            accumulator.numWarnings + element.numWarnings,
                            if (accumulator.topErrorsAndWarnings.size < numErrorsWarningsToKeep)
                                (accumulator.topErrorsAndWarnings + element.topErrorsAndWarnings).take(numErrorsWarningsToKeep)
                            else
                                accumulator.topErrorsAndWarnings,
                        )
                    } else {
                        ValidationSummary(
                            accumulator.dataTrace,
                            accumulator.summary,
                            accumulator.numInfos + if (element is ValidationInfo) 1 else 0,
                            accumulator.numSuccesses + if (element is ValidationSuccess) 1 else 0,
                            accumulator.numErrors + if (element is ValidationError) 1 else 0,
                            accumulator.numWarnings + if (element is ValidationWarning) 1 else 0,
                            if (accumulator.topErrorsAndWarnings.size < numErrorsWarningsToKeep && (element is ValidationError || element is ValidationWarning))
                                (accumulator.topErrorsAndWarnings + element).take(numErrorsWarningsToKeep)
                            else
                                accumulator.topErrorsAndWarnings,
                        )
                    }
                }
            ).forEach { (_, summary) ->
                yield(summary)
            }
    }
}

/**
 * Create a ValidationSummary by combining all results. This is lazily executed and returns a Sequence of ValidationSummary.
 *
 * @param groupBy Optional group-by function that returns a map to key the summary. Will add these as tags to the ValidationSummary dataTrace.
 * @param dataTrace a DataTrace to build upon
 * @param numErrorsWarningsToKeep Defaults to 100. The number of errors and warnings to return.
 */
fun Iterable<ValidationResult>.createSummary(
    dataTrace: DataTrace = dataTrace(),
    numErrorsWarningsToKeep: Int = 100,
    groupBy: SummaryGroupBy = { emptyMap() },
): Iterable<ValidationResult> =
    this.asSequence().createSummary(dataTrace, numErrorsWarningsToKeep, groupBy).asIterable()

/**
 * Create a ValidationSummary by combining all results. This is lazily executed and returns a Sequence of ValidationSummary.
 *
 * @param groupBy Optional group-by function that returns a map to key the summary. Will add these as tags to the ValidationSummary dataTrace.
 * @param dataTrace a DataTrace to build upon
 * @param numErrorsWarningsToKeep Defaults to 100. The number of errors and warnings to return.
 */
fun Validation.createSummary(
    dataTrace: DataTrace = dataTrace(),
    numErrorsWarningsToKeep: Int = 100,
    groupBy: SummaryGroupBy = { emptyMap() },
): Validation = Validation(context, results.createSummary(dataTrace, numErrorsWarningsToKeep, groupBy))

/**
 * Create a ValidationSummary by combining all results.
 *
 * @param groupBy Optional group-by function that returns a map to key the summary. Will add these as tags to the ValidationSummary dataTrace.
 * @param dataTrace a DataTrace to build upon
 * @param numErrorsWarningsToKeep Defaults to 100. The number of errors and warnings to return.
 */
fun CachedValidation.createSummary(
    dataTrace: DataTrace = dataTrace(),
    numErrorsWarningsToKeep: Int = 100,
    groupBy: SummaryGroupBy = { emptyMap() },
): CachedValidation =
    CachedValidation(context, results.createSummary(dataTrace, numErrorsWarningsToKeep, groupBy))

/**
 * Group by tags. Functions like GROUP BY in Sql, were all the combinations receive its own Summary.
 */
fun groupByTag(vararg tags: String): SummaryGroupBy = {
    when (it) {
        is ValidationInfo -> it.dataTrace.tags.filterKeys { it in tags }
        is ValidationSuccess -> it.dataTrace.tags.filterKeys { it in tags }
        is ValidationError -> it.dataTrace.tags.filterKeys { it in tags }
        is ValidationWarning -> it.dataTrace.tags.filterKeys { it in tags }
        is ValidationSummary -> it.dataTrace.tags.filterKeys { it in tags }
        else -> emptyMap()
    }
}
