package com.shoprunner.baleen

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class ResultsWatcherTest {
    @Test
    fun `watch output outputs summary for each window`() {
        val dogDescription = "Dog".describeAs {
            "name".type(
                type = AllowsNull(StringType()),
                required = true
            )
        }
        val printer = ListPrinter()

        // Set up the results that will trigger the watch
        val results = (1..21).flatMap {
            val data = TestHelper.dataOf("name" to "Fido")
            dogDescription.validate(data).results
        }

        // Watch results
        val count = results.watch(windowSize = 10, watcher = printWatcher(printer)).count()

        // make sure the output is the correct size
        assertThat(count).isEqualTo(42) // 21 infos, 21 successes

        assertThat(printer.capturedList).containsExactly(
            ValidationSummary(dataTrace = dataTrace(), summary="Summary", numInfos=5, numSuccesses=5, numErrors=0, numWarnings=0, topErrorsAndWarnings= emptyList()),
            ValidationSummary(dataTrace = dataTrace(), summary="Summary", numInfos=10, numSuccesses=10, numErrors=0, numWarnings=0, topErrorsAndWarnings= emptyList()),
            ValidationSummary(dataTrace = dataTrace(), summary="Summary", numInfos=15, numSuccesses=15, numErrors=0, numWarnings=0, topErrorsAndWarnings= emptyList()),
            ValidationSummary(dataTrace = dataTrace(), summary="Summary", numInfos=20, numSuccesses=20, numErrors=0, numWarnings=0, topErrorsAndWarnings= emptyList()),
            ValidationSummary(dataTrace = dataTrace(), summary="Summary", numInfos=21, numSuccesses=21, numErrors=0, numWarnings=0, topErrorsAndWarnings= emptyList())
        )
    }
}