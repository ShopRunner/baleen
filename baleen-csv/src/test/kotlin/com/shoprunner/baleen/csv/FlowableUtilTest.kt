package com.shoprunner.baleen.csv

import com.shoprunner.baleen.Context
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.DataValue
import com.shoprunner.baleen.TraceLocation
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.datawrappers.HashData
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.StringReader

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class FlowableUtilTest {
    private val dogsCsv = """
        |name,license
        |fido,F4K9
        |spot,WOOF""".trimMargin()

    private fun String.toDV(value: Any?, lineNumber: Int) = Pair(this, DataValue(value, lineNumber, null))

    @Test
    fun `parses xml`() {
        val dataTrace = dataTrace("testFile")

        val validationResults = FlowableUtil.fromCsvWithHeader(dataTrace, { StringReader(dogsCsv) })

        validationResults.test().assertResult(
                Context(
                    HashData(mapOf("name".toDV("fido", 2), "license".toDV("F4K9", 2))),
                    DataTrace(TraceLocation("testFile"), TraceLocation("line 2", 2))
                ),
                Context(
                    HashData(mapOf("name".toDV("spot", 3), "license".toDV("WOOF", 3))),
                    DataTrace(TraceLocation("testFile"), TraceLocation("line 3", 3))
                )
        )
    }
}