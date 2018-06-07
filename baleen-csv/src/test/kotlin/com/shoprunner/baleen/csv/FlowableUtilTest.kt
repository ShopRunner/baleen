package com.shoprunner.baleen.csv

import com.shoprunner.baleen.Context
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

    @Test
    fun `parses xml`() {
        val dataTrace = dataTrace("testFile")

        val validationResults = FlowableUtil.fromCsvWithHeader(dataTrace, { StringReader(dogsCsv) })

        validationResults.test().assertResult(
                Context(HashData(mapOf("name" to "fido", "license" to "F4K9")), dataTrace("testFile", "line 2")),
                Context(HashData(mapOf("name" to "spot", "license" to "WOOF")), dataTrace("testFile", "line 3"))
        )
    }
}