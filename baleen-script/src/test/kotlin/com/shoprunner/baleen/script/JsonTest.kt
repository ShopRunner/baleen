package com.shoprunner.baleen.script

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.groupByTag
import com.shoprunner.baleen.printer.TextPrinter
import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File

internal class JsonTest {
    @Test
    fun `test json script`() {
        val outputFile = File.createTempFile("json-test", ".txt")
        val testJson = File.createTempFile("json-test", ".json")

        testJson.writeText(
            """
            {
              "id": 1,
              "firstName": "Jon",
              "lastName": "Smith"
            }
            """.trimIndent()
        )

        val desc = "person".describeAs {
            "id".type(IntegerType(), required = true)
            "firstName".type(StringType(0, 32), required = true)
            "lastName".type(StringType(0, 32), required = true)
        }

        outputFile.writer().use {
            validate(
                description = desc,
                data = json(testJson),
                groupBy = groupByTag("file"),
                printers = arrayOf(TextPrinter(it, prettyPrint = true)),
            )
        }

        val output = outputFile.readText()

        assertThat(output).isEqualToIgnoringWhitespace(
            """
            ValidationSummary(
              dataTrace=DataTrace(stack=[], tags={file=${testJson.name}}),
              summary=Summary for file=${testJson.name},
              numSuccesses=1,
              numInfos=3,
              numErrors=0,
              numWarnings=0,
              topErrorsAndWarnings=[
              ]
            )
            """.trimIndent()
        )
    }
}
