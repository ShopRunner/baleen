package com.shoprunner.baleen.script

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.groupByTag
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.StringCoercibleToLong
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files.createTempDirectory

internal class CsvTest {
    @Test
    fun `test csv script`() {
        val outDir = createTempDirectory("csv-test").toFile()
        val testCsv = File.createTempFile("csv-test", ".csv")

        testCsv.writeText(
            """
            id,firstName,lastName
            0,Jon,Smith
            1,Jane,Doe
            2,Billy,Idol
            """.trimIndent()
        )

        val desc = "person".describeAs {
            "id".type(StringCoercibleToLong(LongType()), required = true)
            "firstName".type(StringType(0, 32), required = true)
            "lastName".type(StringType(0, 32), required = true)
        }

        validate(
            description = desc,
            data = csv(testCsv),
            outputDir = outDir,
            groupBy = groupByTag("file"),
            outputs = arrayOf(Output.text),
        )

        val output = File(outDir, "summary.txt").readText()

        assertThat(output).isEqualToIgnoringWhitespace(
            """
            ValidationSummary(
              dataTrace=DataTrace(stack=[], tags={file=${testCsv.name}}),
              summary=Summary for file=${testCsv.name},
              numSuccesses=3,
              numInfos=9,
              numErrors=0,
              numWarnings=0,
              topErrorsAndWarnings=[
              ]
            )
            """.trimIndent()
        )
    }
}
