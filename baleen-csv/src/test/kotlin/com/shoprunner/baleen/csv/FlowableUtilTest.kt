package com.shoprunner.baleen.csv

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.Context
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.datawrappers.HashData
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.StringCoercibleToLong
import com.shoprunner.baleen.types.StringType
import io.reactivex.rxkotlin.toFlowable
import java.io.StringReader
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class FlowableUtilTest {
    @Test
    fun `parses csv`() {
        val dogsCsv = """
            |name,license
            |fido,F4K9
            |spot,WOOF""".trimMargin()

        val dataTrace = dataTrace("testFile")

        val validationResults = FlowableUtil.fromCsvWithHeader(dataTrace, { StringReader(dogsCsv) })

        validationResults.test().assertResult(
            Context(
                data = HashData(mapOf("name" to "fido", "license" to "F4K9")),
                dataTrace = dataTrace("testFile", "line 2")
                    .tag("line", "2")
                    .tag("row", "0")),
            Context(
                data = HashData(mapOf("name" to "spot", "license" to "WOOF")),
                dataTrace = dataTrace("testFile", "line 3")
                    .tag("line", "3")
                    .tag("row", "1"))
        )
    }

    @Test
    fun `data trace includes column`() {
        val dataTrace = dataTrace("testFile")

        val dogsCsv = """
            |name,legs
            |fido,6""".trimMargin()

        val dogDescription = "Dog".describeAs {

            "name".type(StringType(),
                required = true)

            "legs".type(StringCoercibleToLong(LongType(min = 0, max = 4)),
                required = true)
        }

        val contexts = FlowableUtil.fromCsvWithHeader(dataTrace, { StringReader(dogsCsv) })

        val results = contexts
            .flatMap { dogDescription.validate(it).results.toFlowable() }

        results.test().assertResult(
            ValidationInfo(
                dataTrace = dataTrace("testFile", "line 2")
                    .tag("row", "0")
                    .tag("line", "2"),
                message = "has attribute \"name\"",
                value = HashData(mapOf(
                    "name" to "fido",
                    "legs" to "6"))),

            ValidationInfo(
                dataTrace = dataTrace("testFile", "line 2")
                    .tag("row", "0")
                    .tag("line", "2"),
                message = "has attribute \"legs\"",
                value = HashData(mapOf(
                    "name" to "fido",
                    "legs" to "6"))),

            ValidationError(
                dataTrace = dataTrace("testFile", "line 2", "attribute \"legs\"")
                    .tag("row", "0")
                    .tag("line", "2")
                    .tag("column", "1"),
                message = "is greater than 4",
                value = 6L
            )
        )
    }
}
