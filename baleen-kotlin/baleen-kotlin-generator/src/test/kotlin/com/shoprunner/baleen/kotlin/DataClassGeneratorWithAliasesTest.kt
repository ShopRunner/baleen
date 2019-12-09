package com.shoprunner.baleen.kotlin

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.types.StringType
import java.io.StringWriter
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DataClassGeneratorWithAliasesTest {

    @Test
    fun `test simple model with StringType`() {
        val model = "Model".describeAs(
            nameSpace = "com.shoprunner.baleen.kotlin.test",
            markdownDescription = "Test Model"
        ) {
            "field1".type(
                type = StringType(),
                markdownDescription = "Test field 1",
                aliases = arrayOf("f1")
            )

            "field2".type(
                type = StringType(),
                markdownDescription = "Test field 2",
                aliases = arrayOf("f2", "key")
            )
        }

        val expectedDataClassStr = """
            package com.shoprunner.baleen.kotlin.test

            import com.shoprunner.baleen.annotation.Alias
            import com.shoprunner.baleen.annotation.DataDescription
            import kotlin.String
            
            /**
             * Test Model
             */
            @DataDescription
            data class Model(
              /**
               * Test field 1
               */
              @Alias("f1")
              val field1: String,
              /**
               * Test field 2
               */
              @Alias(
                "f2",
                "key"
              )
              val field2: String
            )

        """.trimIndent()

        val dataClassSpecs = DataClassGenerator.encode(model)
        val outputStream = StringWriter()
        dataClassSpecs[model]!!.writeTo(outputStream)
        val outputStr = outputStream.toString()

        assertThat(dataClassSpecs).hasSize(1)
        // assertThat(outputStr).isEqualToIgnoringWhitespace(expectedDataClassStr)
        assertThat(outputStr).isEqualTo(expectedDataClassStr)
    }
}
