package com.shoprunner.baleen.avro

import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.avro.AvroGenerator.encode
import com.shoprunner.baleen.avro.AvroGenerator.writeTo
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.BooleanType
import com.shoprunner.baleen.types.DoubleType
import com.shoprunner.baleen.types.EnumType
import com.shoprunner.baleen.types.FloatType
import com.shoprunner.baleen.types.InstantType
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringCoercibleToFloat
import com.shoprunner.baleen.types.StringConstantType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.TimestampMillisType
import com.shoprunner.baleen.types.UnionType
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AvroGeneratorTest {

    @Nested
    inner class Types {
        @Test
        fun `getAvroSchema encodes allows null type`() {
            val schema = AvroGenerator.getAvroSchema(AllowsNull(BooleanType()))
            assertThat(schema.type).isEqualTo(Schema.Type.UNION)
            assertThat(schema.types[0].type).isEqualTo(Schema.Type.BOOLEAN)
            assertThat(schema.types[1].type).isEqualTo(Schema.Type.NULL)
        }

        @Test
        fun `getAvroSchema encodes allows null type on a union`() {
            val schema = AvroGenerator.getAvroSchema(AllowsNull(UnionType(BooleanType(), LongType())))
            assertThat(schema.type).isEqualTo(Schema.Type.UNION)
            assertThat(schema.types[0].type).isEqualTo(Schema.Type.BOOLEAN)
            assertThat(schema.types[1].type).isEqualTo(Schema.Type.LONG)
            assertThat(schema.types[2].type).isEqualTo(Schema.Type.NULL)
        }

        @Test
        fun `getAvroSchema encodes the resulting coerced type`() {
            val schema = AvroGenerator.getAvroSchema(StringCoercibleToFloat(FloatType()))
            assertThat(schema.type).isEqualTo(Schema.Type.FLOAT)
        }

        @Test
        fun `getAvroSchema encodes boolean type`() {
            val schema = AvroGenerator.getAvroSchema(BooleanType())
            assertThat(schema.type).isEqualTo(Schema.Type.BOOLEAN)
        }

        @Test
        fun `getAvroSchema encodes float type`() {
            val schema = AvroGenerator.getAvroSchema(FloatType())
            assertThat(schema.type).isEqualTo(Schema.Type.FLOAT)
        }

        @Test
        fun `getAvroSchema encodes double type`() {
            val schema = AvroGenerator.getAvroSchema(DoubleType())
            assertThat(schema.type).isEqualTo(Schema.Type.DOUBLE)
        }

        @Test
        fun `getAvroSchema encodes int type`() {
            val schema = AvroGenerator.getAvroSchema(IntType())
            assertThat(schema.type).isEqualTo(Schema.Type.INT)
        }

        @Test
        fun `getAvroSchema encodes long type`() {
            val schema = AvroGenerator.getAvroSchema(LongType())
            assertThat(schema.type).isEqualTo(Schema.Type.LONG)
        }

        @Test
        fun `getAvroSchema encodes string type`() {
            val schema = AvroGenerator.getAvroSchema(StringType())
            assertThat(schema.type).isEqualTo(Schema.Type.STRING)
        }

        @Test
        fun `getAvroSchema encodes string constant type`() {
            val schema = AvroGenerator.getAvroSchema(StringConstantType("abc"))
            assertThat(schema.type).isEqualTo(Schema.Type.STRING)
        }

        @Test
        fun `getAvroSchema encodes enum type`() {
            val schema = AvroGenerator.getAvroSchema(EnumType("MyEmail", "a", "b", "c"))
            assertThat(schema.type).isEqualTo(Schema.Type.ENUM)
            assertThat(schema.name).isEqualTo("MyEmail")
            assertThat(schema.enumSymbols).containsExactly("a", "b", "c")
        }

        @Test
        fun `getAvroSchema encodes instant type`() {
            val schema = AvroGenerator.getAvroSchema(InstantType())
            assertThat(schema.type).isEqualTo(Schema.Type.LONG)
            assertThat(schema.logicalType).isEqualTo(LogicalTypes.timestampMillis())
        }

        @Test
        fun `getAvroSchema encodes timestamp-millis type`() {
            val schema = AvroGenerator.getAvroSchema(TimestampMillisType())
            assertThat(schema.type).isEqualTo(Schema.Type.LONG)
            assertThat(schema.logicalType).isEqualTo(LogicalTypes.timestampMillis())
        }

        @Test
        fun `getAvroSchema encodes map type`() {
            val schema = AvroGenerator.getAvroSchema(MapType(StringType(), IntType()))
            assertThat(schema.type).isEqualTo(Schema.Type.MAP)
            assertThat(schema.valueType.type).isEqualTo(Schema.Type.INT)
        }

        @Test
        fun `getAvroSchema fails map type with non-string key`() {
            assertThrows(Exception::class.java) {
                AvroGenerator.getAvroSchema(MapType(IntType(), IntType()))
            }
        }

        @Test
        fun `getAvroSchema encodes occurences type`() {
            val schema = AvroGenerator.getAvroSchema(OccurrencesType(StringType()))
            assertThat(schema.type).isEqualTo(Schema.Type.ARRAY)
            assertThat(schema.elementType.type).isEqualTo(Schema.Type.STRING)
        }

        @Test
        fun `getAvroSchema encodes non-nullable union type`() {
            val schema = AvroGenerator.getAvroSchema(UnionType(IntType(), LongType()))
            assertThat(schema.type).isEqualTo(Schema.Type.UNION)
            assertThat(schema.types[0].type).isEqualTo(Schema.Type.INT)
            assertThat(schema.types[1].type).isEqualTo(Schema.Type.LONG)
        }

        @Test
        fun `getAvroSchema encodes union type of one not as a union`() {
            val schema = AvroGenerator.getAvroSchema(UnionType(IntType()))
            assertThat(schema.type).isEqualTo(Schema.Type.INT)
        }

        @Test
        fun `getAvroSchema fails with an unsupported type`() {
            class BadType : BaleenType {
                override fun name() = "bad"
                override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> = emptySequence()
            }

            assertThrows(Exception::class.java) {
                AvroGenerator.getAvroSchema(BadType())
            }
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
                    required = false,
                    default = 4
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
        |        { "name": "legs", "type": [ "long", "int" ], "doc": "The number of legs", "default": 4 }
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
        |          "type" : [ "long", "int" ],
        |          "doc" : "The number of legs",
        |          "default" : 4
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
            encode(dogDescription).writeTo(PrintStream(outputStream))

            assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(dogSchemaStr)
        }

        @Test
        fun `nested model`() {
            val outputStream = ByteArrayOutputStream()
            encode(packDescription).writeTo(PrintStream(outputStream))

            assertThat(outputStream.toString()).isEqualToIgnoringCase(packSchemaByValueStr)
        }

        @Test
        fun `nested model by reference is not supported`() {
            val outputStream = ByteArrayOutputStream()
            encode(packDescription).writeTo(PrintStream(outputStream))

            assertThat(outputStream.toString()).isNotEqualToIgnoringWhitespace(packSchemaByReferenceStr)
        }

        @Test
        fun `write to avsc file`() {
            val dir = File("build/avro-gen-test")
            val sourceDir = File(dir, "src/main/avro-file")
            encode(dogDescription).writeTo(sourceDir)

            val dogFile = File(sourceDir, "com/shoprunner/data/dogs/Dog.avsc")
            Assertions.assertThat(dogFile).exists()
        }

        @Test
        fun `write to avsc path`() {
            val dir = File("build/avro-gen-test")
            val sourceDir = File(dir, "src/main/avro-path")
            encode(dogDescription).writeTo(sourceDir.toPath())

            val dogFile = File(sourceDir, "com/shoprunner/data/dogs/Dog.avsc")
            Assertions.assertThat(dogFile).exists()
        }

        @Test
        fun `default values are added for optional fields`() {
            val descriptionWithDefault = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") { p ->
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
                        required = false,
                        default = 4
                )
            }

            val schemaWithDefaultStr = """
            |{
            |   "type": "record",
            |   "name": "Dog",
            |   "namespace": "com.shoprunner.data.dogs",
            |   "doc": "It's a dog. Ruff Ruff!",
            |   "fields": [
            |        { "name": "name", "type": "string", "doc": "The name of the dog" },
            |        { "name": "legs", "type": [ "long", "int" ], "doc": "The number of legs", "default": 4 }
            |   ]
            |}
            """.trimMargin()

            val outputStream = ByteArrayOutputStream()
            encode(descriptionWithDefault).writeTo(PrintStream(outputStream))

            assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaWithDefaultStr)
        }

        @Test
        fun `default values are added for required fields`() {
            val descriptionWithDefault = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") { p ->
                p.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog",
                        required = true,
                        default = "Fido"
                )
                p.attr(
                        name = "legs",
                        type = UnionType(LongType(), IntType()),
                        markdownDescription = "The number of legs",
                        required = false,
                        default = 4
                )
            }

            val schemaWithDefaultStr = """
            |{
            |   "type": "record",
            |   "name": "Dog",
            |   "namespace": "com.shoprunner.data.dogs",
            |   "doc": "It's a dog. Ruff Ruff!",
            |   "fields": [
            |        { "name": "name", "type": "string", "doc": "The name of the dog", "default": "Fido" },
            |        { "name": "legs", "type": [ "long", "int" ], "doc": "The number of legs", "default": 4 }
            |   ]
            |}
            """.trimMargin()

            val outputStream = ByteArrayOutputStream()
            encode(descriptionWithDefault).writeTo(PrintStream(outputStream))

            assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaWithDefaultStr)
        }
    }
}