package com.shoprunner.baleen.avro

import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class AvroEncoderTest {
    private val dogDescription = Baleen.describe("Dog",
            markdownDescription = "Type of dog.") { p ->

            p.attr( name = "name",
                    type = StringType(),
                    markdownDescription = "Name of the dog.",
                    required = true)
    }

    @Test
    fun `simple single attribute`() {
        val outputStream = ByteArrayOutputStream()
        AvroEncoder.encode(dogDescription, PrintStream(outputStream))

        assertThat(outputStream.toString()).isEqualToIgnoringWhitespace("""
            {
              "type" : "record",
              "name" : "Dog",
              "doc" : "Type of dog.",
              "fields" : [ {
                "name" : "name",
                "type" : "string",
                "doc" : "Name of the dog."
              } ]
            }""")
    }
}