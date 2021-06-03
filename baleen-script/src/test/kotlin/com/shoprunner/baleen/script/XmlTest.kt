package com.shoprunner.baleen.script

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.groupByTag
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
            <person>
                <id>1</id>
                <firstName>Jon</firstName>
                <lastName>Smith</lastName>
            </person>
            """.trimIndent()
        )

        val desc = "person".describeAs {
            "id".type(StringCoercibleToLong(LongType()), required = true)
            "firstName".type(StringType(0, 32), required = true)
            "lastName".type(StringType(0, 32), required = true)
        }

        validate(
            description = desc,
            data = xml(testXml),
            outputDir = outDir,
            groupBy = groupByTag("file"),
            outputs = arrayOf(Output.text),
        )

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
