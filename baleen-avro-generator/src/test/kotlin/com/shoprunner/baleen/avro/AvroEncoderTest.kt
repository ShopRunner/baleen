package com.shoprunner.baleen.avro

import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.UnionType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.avro.AvroEncoder.encodeTo
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AvroEncoderTest {

    @Nested
    inner class Types {
        @Test
        fun `TODO write tests`() {
            // TODO
        }
    }

    @Nested
    inner class Models {
        private val dogDescription = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") { p ->
            p.attr(
                    name = "name",
                    type = StringType(),
                    markdownDescription = "The name of the dog",
                    required = true
            )
            p.attr(
                    name = "legs",
                    type = UnionType(LongType(), IntType()),
                    markdownDescription = "The number of legs",
                    required = false
            )
        }

        private val packDescription = Baleen.describe("Pack", "com.shoprunner.data.dogs", "It's a Pack of Dogs. Grr Grr!") { p ->
            p.attr(
                    name = "name",
                    type = StringType(),
                    markdownDescription = "The name of the pack",
                    aliases = arrayOf("packName"),
                    required = true
            )
            p.attr(
                    name = "dogs",
                    type = OccurrencesType(dogDescription),
                    markdownDescription = "The dogs in the pack",
                    required = true
            )
        }

        private val dogSchemaStr = """
        |{
        |   "type": "record",
        |   "name": "Dog",
        |   "namespace": "com.shoprunner.data.dogs",
        |   "doc": "It's a dog. Ruff Ruff!",
        |   "fields": [
        |        { "name": "name", "type": "string", "doc": "The name of the dog" },
        |        { "name": "legs", "type": [ "null", "long", "int" ], "doc": "The number of legs", "default": null }
        |   ]
        |}
        """.trimMargin()

        private val packSchemaByReferenceStr = """
        |{
        |   "type": "record",
        |   "namespace": "com.shoprunner.data.dogs",
        |   "name": "Pack",
        |   "doc": "It's a Pack of Dogs. Grr Grr!",
        |   "fields": [
        |        { "name": "name", "type": "string", "doc": "The name of the pack", "aliases": [ "packName" ] },
        |        {
        |          "name": "dogs",
        |          "type": {"type": "array", "items": "com.shoprunner.data.dogs.Dog"},
        |          "doc": "The dogs in the pack"
        |        }
        |   ]
        |}
        """.trimMargin()

        private val packSchemaByValueStr = """
        |{
        |  "type" : "record",
        |  "name" : "Pack",
        |  "namespace" : "com.shoprunner.data.dogs",
        |  "doc" : "It's a Pack of Dogs. Grr Grr!",
        |  "fields" : [ {
        |    "name" : "name",
        |    "type" : "string",
        |    "doc" : "The name of the pack",
        |    "aliases" : [ "packName" ]
        |  }, {
        |    "name" : "dogs",
        |    "type" : {
        |      "type" : "array",
        |      "items" : {
        |        "type" : "record",
        |        "name" : "Dog",
        |        "doc" : "It's a dog. Ruff Ruff!",
        |        "fields" : [ {
        |          "name" : "name",
        |          "type" : "string",
        |          "doc" : "The name of the dog"
        |        }, {
        |          "name" : "legs",
        |          "type" : [ "null", "long", "int" ],
        |          "doc" : "The number of legs",
        |          "default" : null
        |        } ]
        |      }
        |    },
        |    "doc" : "The dogs in the pack"
        |  } ]
        |}
        """.trimMargin()

        @Test
        fun `single model`() {
            val outputStream = ByteArrayOutputStream()
            dogDescription.encodeTo(PrintStream(outputStream))

            assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(dogSchemaStr)
        }

        @Test
        fun `nested model`() {
            val outputStream = ByteArrayOutputStream()
            packDescription.encodeTo(PrintStream(outputStream))

            assertThat(outputStream.toString()).isEqualToIgnoringCase(packSchemaByValueStr)
        }

        @Test
        fun `nested model by reference is not supported`() {
            val outputStream = ByteArrayOutputStream()
            packDescription.encodeTo(PrintStream(outputStream))

            assertThat(outputStream.toString()).isNotEqualToIgnoringWhitespace(packSchemaByReferenceStr)
        }

        @Test
        fun `write to avsc file`() {
            val dir = File("build/avro-gen-test")
            val sourceDir = File(dir, "src/main/avro-file")
            dogDescription.encodeTo(sourceDir)

            val dogFile = File(sourceDir, "com/shoprunner/data/dogs/Dog.avsc")
            Assertions.assertThat(dogFile).exists()
        }

        @Test
        fun `write to avsc path`() {
            val dir = File("build/avro-gen-test")
            val sourceDir = File(dir, "src/main/avro-path")
            dogDescription.encodeTo(sourceDir.toPath())

            val dogFile = File(sourceDir, "com/shoprunner/data/dogs/Dog.avsc")
            Assertions.assertThat(dogFile).exists()
        }
    }
}