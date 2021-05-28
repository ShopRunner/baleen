package com.shoprunner.baleen.printer

import com.shoprunner.baleen.ValidationSuccess
import com.shoprunner.baleen.dataTrace
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class ListPrintTest {

    @Test
    fun `print to a list`() {
        val results = listOf(
            ValidationSuccess(dataTrace(), "one"),
            ValidationSuccess(dataTrace(), "two"),
            ValidationSuccess(dataTrace(), "three"),
            ValidationSuccess(dataTrace(), "four"),
        )

        val printer = ListPrinter()
        printer.print(results)

        Assertions.assertThat(printer.capturedList).isEqualTo(results)
    }
}
