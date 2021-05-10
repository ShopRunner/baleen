package com.shoprunner.baleen

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ValidationSummaryTest {
    @Test
    fun `validation summary sums up ValidationInfo`() {
        val results = sequenceOf(
            ValidationInfo(dataTrace(), "Info 1", null),
            ValidationInfo(dataTrace(), "Info 2", null),
            ValidationInfo(dataTrace(), "Info 3", null),
        )

        assertThat(results.createSummary()).containsExactly(
            ValidationSummary(dataTrace(), "Summary", 3, 0, 0, 0, emptyList())
        )
    }

    @Test
    fun `validation summary sums up ValidationError`() {
        val results = sequenceOf(
            ValidationError(dataTrace(), "Error 1", null),
            ValidationError(dataTrace(), "Error 2", null),
            ValidationError(dataTrace(), "Error 3", null),
        )

        assertThat(results.createSummary()).containsExactly(
            ValidationSummary(
                dataTrace(), "Summary", 0, 0, 3, 0,
                listOf(
                    ValidationError(dataTrace(), "Error 1", null),
                    ValidationError(dataTrace(), "Error 2", null),
                    ValidationError(dataTrace(), "Error 3", null),
                )
            )
        )
    }

    @Test
    fun `validation summary sums up ValidationWarning`() {
        val results = sequenceOf(
            ValidationWarning(dataTrace(), "Warning 1", null),
            ValidationWarning(dataTrace(), "Warning 2", null),
            ValidationWarning(dataTrace(), "Warning 3", null),
        )

        assertThat(results.createSummary()).containsExactly(
            ValidationSummary(
                dataTrace(), "Summary", 0, 0, 0, 3,
                listOf(
                    ValidationWarning(dataTrace(), "Warning 1", null),
                    ValidationWarning(dataTrace(), "Warning 2", null),
                    ValidationWarning(dataTrace(), "Warning 3", null),
                )
            )
        )
    }

    @Test
    fun `validation summary sums up ValidationSuccess`() {
        val results = sequenceOf(
            ValidationSuccess(dataTrace(), null),
            ValidationSuccess(dataTrace(), null),
            ValidationSuccess(dataTrace(), null),
        )

        assertThat(results.createSummary()).containsExactly(
            ValidationSummary(dataTrace(), "Summary", 0, 3, 0, 0, emptyList())
        )
    }

    @Test
    fun `validation summary combines ValidationSummary`() {
        val results = sequenceOf(
            ValidationSummary(dataTrace(), "Summary", 3, 0, 0, 0, emptyList()),
            ValidationSummary(dataTrace(), "Summary", 0, 3, 0, 0, emptyList()),
            ValidationSummary(
                dataTrace(), "Summary", 0, 0, 0, 3,
                listOf(
                    ValidationWarning(dataTrace(), "Warning 1", null),
                    ValidationWarning(dataTrace(), "Warning 2", null),
                    ValidationWarning(dataTrace(), "Warning 3", null),
                )
            ),
            ValidationSummary(
                dataTrace(), "Summary", 0, 0, 3, 0,
                listOf(
                    ValidationError(dataTrace(), "Error 1", null),
                    ValidationError(dataTrace(), "Error 2", null),
                    ValidationError(dataTrace(), "Error 3", null),
                )
            )
        )

        assertThat(results.createSummary()).containsExactly(
            ValidationSummary(
                dataTrace(), "Summary", 3, 3, 3, 3,
                listOf(
                    ValidationWarning(dataTrace(), "Warning 1", null),
                    ValidationWarning(dataTrace(), "Warning 2", null),
                    ValidationWarning(dataTrace(), "Warning 3", null),
                    ValidationError(dataTrace(), "Error 1", null),
                    ValidationError(dataTrace(), "Error 2", null),
                    ValidationError(dataTrace(), "Error 3", null),
                )
            )
        )
    }

    @Test
    fun `validation summary can be grouped by tags`() {
        val results = sequenceOf(
            ValidationInfo(dataTrace().tag("priority", "high"), "Info 1", null),
            ValidationInfo(dataTrace().tag("priority", "medium"), "Info 2", null),
            ValidationInfo(dataTrace().tag("priority", "low"), "Info 3", null),
            ValidationError(dataTrace().tag("priority", "high"), "Error 1", null),
            ValidationError(dataTrace().tag("priority", "medium"), "Error 2", null),
            ValidationError(dataTrace().tag("priority", "low"), "Error 3", null),
        )

        assertThat(results.createSummary(groupBy = groupByTag("priority"))).containsExactly(
            ValidationSummary(
                dataTrace().tag("priority", "high"), "Summary", 1, 0, 1, 0,
                listOf(
                    ValidationError(dataTrace().tag("priority", "high"), "Error 1", null),
                )
            ),
            ValidationSummary(
                dataTrace().tag("priority", "medium"), "Summary", 1, 0, 1, 0,
                listOf(
                    ValidationError(dataTrace().tag("priority", "medium"), "Error 2", null),
                )
            ),
            ValidationSummary(
                dataTrace().tag("priority", "low"), "Summary", 1, 0, 1, 0,
                listOf(
                    ValidationError(dataTrace().tag("priority", "low"), "Error 3", null),
                )
            )
        )
    }

    @Test
    fun `validation summary of summaries can be rolled up`() {
        val results = sequenceOf(
            ValidationInfo(dataTrace().tag("priority", "high"), "Info 1", null),
            ValidationInfo(dataTrace().tag("priority", "medium"), "Info 2", null),
            ValidationInfo(dataTrace().tag("priority", "low"), "Info 3", null),
            ValidationError(dataTrace().tag("priority", "high"), "Error 1", null),
            ValidationError(dataTrace().tag("priority", "medium"), "Error 2", null),
            ValidationError(dataTrace().tag("priority", "low"), "Error 3", null),
        )

        val summaries = results.createSummary(groupBy = groupByTag("priority")).toList()

        Assertions.assertThat(summaries).containsExactly(
            ValidationSummary(
                dataTrace().tag("priority", "high"), "Summary", 1, 0, 1, 0,
                listOf(
                    ValidationError(dataTrace().tag("priority", "high"), "Error 1", null),
                )
            ),
            ValidationSummary(
                dataTrace().tag("priority", "medium"), "Summary", 1, 0, 1, 0,
                listOf(
                    ValidationError(dataTrace().tag("priority", "medium"), "Error 2", null),
                )
            ),
            ValidationSummary(
                dataTrace().tag("priority", "low"), "Summary", 1, 0, 1, 0,
                listOf(
                    ValidationError(dataTrace().tag("priority", "low"), "Error 3", null),
                )
            )
        )

        Assertions.assertThat(summaries.asIterable().createSummary()).containsExactly(
            ValidationSummary(
                dataTrace(), "Summary", 3, 0, 3, 0,
                listOf(
                    ValidationError(dataTrace().tag("priority", "high"), "Error 1", null),
                    ValidationError(dataTrace().tag("priority", "medium"), "Error 2", null),
                    ValidationError(dataTrace().tag("priority", "low"), "Error 3", null),
                )
            )
        )
    }
}
