package com.shoprunner.baleen.printer

import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationSuccess
import com.shoprunner.baleen.ValidationSummary
import com.shoprunner.baleen.ValidationWarning
import com.shoprunner.baleen.dataTrace
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

internal class CsvPrinterTest {

    @Test
    fun `print csv`() {
        val results = sequenceOf(
            ValidationSuccess(dataTrace("test").tag("tag" to "value"), "one"),
            ValidationInfo(dataTrace("test").tag("tag" to "value"), "two", "two"),
            ValidationError(dataTrace("test").tag("tag" to "value"), "three", "three"),
            ValidationWarning(dataTrace("test").tag("tag" to "value"), "four", "four"),
        ).asIterable()

        val dir = createTempDir("tmp")

        CsvPrinter(dir).print(results)

        Assertions.assertThat(dir.list()).containsExactlyInAnyOrder(
            "results.csv"
        )

        Assertions.assertThat(File(dir, "results.csv").readText().trim())
            .isEqualTo(
                """
                type,message,value,tags,dataTrace
                SUCCESS,,one,tag=value,test
                INFO,two,two,tag=value,test
                ERROR,three,three,tag=value,test
                WARNING,four,four,tag=value,test
                """.trimIndent()
            )
    }

    @Test
    fun `print summary as multiple csv`() {
        val results = sequenceOf(
            ValidationSummary(
                dataTrace(), "Summary1", 1, 1, 1, 1,
                listOf(
                    ValidationError(dataTrace("test").tag("tag" to "value"), "three", "three"),
                    ValidationWarning(dataTrace("test").tag("tag" to "value"), "four", "four"),
                )
            ),
            ValidationSummary(
                dataTrace("test").tag("tag" to "value"), "Summary2", 1, 1, 1, 1,
                listOf(
                    ValidationError(dataTrace("test").tag("tag" to "value"), "three", "three"),
                    ValidationWarning(dataTrace("test").tag("tag" to "value"), "four", "four"),
                )
            ),
        ).asIterable()

        val dir = createTempDir("tmp")

        CsvPrinter(dir).print(results)

        Assertions.assertThat(dir.list()).containsExactlyInAnyOrder(
            "summary.csv",
            "errors_Summary1.csv",
            "errors_Summary2_tag-value.csv"
        )

        Assertions.assertThat(File(dir, "summary.csv").readText().trim())
            .isEqualTo(
                """
                summary,numSuccesses,numInfos,numWarnings,numErrors,tags,dataTrace
                Summary1,1,1,1,1,,
                Summary2,1,1,1,1,tag=value,test
                """.trimIndent()
            )

        Assertions.assertThat(File(dir, "errors_Summary1.csv").readText().trim())
            .isEqualTo(
                """
                type,message,value,tags,dataTrace
                ERROR,three,three,tag=value,test
                WARNING,four,four,tag=value,test
                """.trimIndent()
            )

        Assertions.assertThat(File(dir, "errors_Summary2_tag-value.csv").readText().trim())
            .isEqualTo(
                """
                type,message,value,tags,dataTrace
                ERROR,three,three,tag=value,test
                WARNING,four,four,tag=value,test
                """.trimIndent()
            )
    }
}
