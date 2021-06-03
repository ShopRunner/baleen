package com.shoprunner.baleen.printer

import com.shoprunner.baleen.ValidationSuccess
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test

class ConsolePrinterTest {
    @Test
    fun `console printing does not fail`() {
        val results = sequenceOf(
            ValidationSuccess(dataTrace(), "one"),
            ValidationSuccess(dataTrace(), "two"),
            ValidationSuccess(dataTrace(), "three"),
            ValidationSuccess(dataTrace(), "four"),
        ).asIterable()

        ConsolePrinter.print(results)
    }
}
