package com.shoprunner.baleen.script

import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files

internal class JsonTest {
    @Test
    fun `test json script`() {
        val outDir = Files.createTempDirectory("json-test").toFile()
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

        baleen(outDir, Output.console, Output.text) {
            json(testJson) {
                "id".type(IntegerType(), required = true)
                "firstName".type(StringType(0, 32), required = true)
                "lastName".type(StringType(0, 32), required = true)
            }
        }

        val output = File(outDir, "summary.txt").readText()

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
