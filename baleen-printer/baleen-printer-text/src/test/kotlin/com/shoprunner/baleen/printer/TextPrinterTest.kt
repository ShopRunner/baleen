package com.shoprunner.baleen.printer

import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationSuccess
import com.shoprunner.baleen.ValidationSummary
import com.shoprunner.baleen.ValidationWarning
import com.shoprunner.baleen.dataTrace
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

internal class TextPrinterTest {
    @Test
    fun `no pretty print`() {
        val results = sequenceOf(
            ValidationSuccess(dataTrace("test").tag("tag" to "value"), "one"),
            ValidationInfo(dataTrace("test").tag("tag" to "value"), "two", "two"),
            ValidationError(dataTrace("test").tag("tag" to "value"), "three", "three"),
            ValidationWarning(dataTrace("test").tag("tag" to "value"), "four", "four"),
            ValidationSummary(
                dataTrace(), "Summary1", 1, 1, 1, 1,
                listOf(
                    ValidationError(dataTrace("test").tag("tag" to "value"), "three", "three"),
                    ValidationWarning(dataTrace("test").tag("tag" to "value"), "four", "four"),
                )
            ),
        ).asIterable()

        val file = File.createTempFile("tmp", ".txt")

        file.writer().use {
            TextPrinter(it).print(results)
        }

        assertThat(file.readText().trim()).isEqualTo(
            """
            ValidationSuccess(dataTrace=DataTrace(stack=[test], tags={tag=value}), value=one)
            ValidationInfo(dataTrace=DataTrace(stack=[test], tags={tag=value}), message=two, value=two)
            ValidationError(dataTrace=DataTrace(stack=[test], tags={tag=value}), message=three, value=three)
            ValidationWarning(dataTrace=DataTrace(stack=[test], tags={tag=value}), message=four, value=four)
            ValidationSummary(dataTrace=DataTrace(stack=[], tags={}), summary=Summary1, numInfos=1, numSuccesses=1, numErrors=1, numWarnings=1, topErrorsAndWarnings=[ValidationError(dataTrace=DataTrace(stack=[test], tags={tag=value}), message=three, value=three), ValidationWarning(dataTrace=DataTrace(stack=[test], tags={tag=value}), message=four, value=four)])
            """.trimIndent()
        )
    }

    @Test
    fun `pretty print`() {
        val results = sequenceOf(
            ValidationSuccess(dataTrace("test").tag("tag" to "value"), "one"),
            ValidationInfo(dataTrace("test").tag("tag" to "value"), "two", "two"),
            ValidationError(dataTrace("test").tag("tag" to "value"), "three", "three"),
            ValidationWarning(dataTrace("test").tag("tag" to "value"), "four", "four"),
            ValidationSummary(
                dataTrace(), "Summary1", 1, 1, 1, 1,
                listOf(
                    ValidationError(dataTrace("test").tag("tag" to "value"), "three", "three"),
                    ValidationWarning(dataTrace("test").tag("tag" to "value"), "four", "four"),
                )
            ),
        ).asIterable()

        val file = File.createTempFile("tmp", ".txt")

        file.writer().use {
            TextPrinter(it, prettyPrint = true).print(results)
        }

        assertThat(file.readText()).isEqualTo(
"""ValidationSuccess(
  dataTrace=DataTrace(stack=[test], tags={tag=value}),
  value=one
)
ValidationInfo(
  dataTrace=DataTrace(stack=[test], tags={tag=value}),
  message=two,
  value=two
)
ValidationError(
  dataTrace=DataTrace(stack=[test], tags={tag=value}),
  message=three,
  value=three
)
ValidationWarning(
  dataTrace=DataTrace(stack=[test], tags={tag=value}),
  message=four,
  value=four
)
ValidationSummary(
  dataTrace=DataTrace(stack=[], tags={}),
  summary=Summary1,
  numSuccesses=1,
  numInfos=1,
  numErrors=1,
  numWarnings=1,
  topErrorsAndWarnings=[
    ValidationError(
      dataTrace=DataTrace(stack=[test], tags={tag=value}),
      message=three,
      value=three
    )
    ValidationWarning(
      dataTrace=DataTrace(stack=[test], tags={tag=value}),
      message=four,
      value=four
    )
  ]
)
"""
        )
    }
}
