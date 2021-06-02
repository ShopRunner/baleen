package com.shoprunner.baleen.printer.jvm

import com.shoprunner.baleen.ValidationSuccess
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test

internal class LogPrinterTest {
    @Test
    fun `log printing does not fail`() {
        val results = sequenceOf(
            ValidationSuccess(dataTrace(), "one"),
            ValidationSuccess(dataTrace(), "two"),
            ValidationSuccess(dataTrace(), "three"),
            ValidationSuccess(dataTrace(), "four"),
        ).asIterable()

        LogPrinter().print(results)
    }
}
