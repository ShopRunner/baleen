package com.shoprunner.baleen.script

import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.StringCoercibleToLong
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files

internal class XmlTest {
    @Test
    fun `test xml script`() {
        val outDir = Files.createTempDirectory("xml-test").toFile()
        val testXml = File.createTempFile("xml-test", ".xml")

        testXml.writeText(
            """
            <example>
                <id>1</id>
                <firstName>Jon</firstName>
                <lastName>Smith</lastName>
            </example>
            """.trimIndent()
        )

        baleen(outDir, Output.console, Output.text) {
            xml(testXml) {
                "example".type(required = true) {
                    "id".type(StringCoercibleToLong(LongType()), required = true)
                    "firstName".type(StringType(0, 32), required = true)
                    "lastName".type(StringType(0, 32), required = true)
                }
            }
        }

        val output = File(outDir, "summary.txt").readText()

        assertThat(output).isEqualToIgnoringWhitespace(
            """
            ValidationSummary(
              dataTrace=DataTrace(stack=[], tags={file=${testXml.name}}),
              summary=Summary for file=${testXml.name},
              numSuccesses=1,
              numInfos=4,
              numErrors=0,
              numWarnings=0,
              topErrorsAndWarnings=[
              ]
            )
            """.trimIndent()
        )
    }
}
