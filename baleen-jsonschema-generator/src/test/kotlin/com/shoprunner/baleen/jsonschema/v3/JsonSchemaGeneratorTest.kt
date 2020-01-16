package com.shoprunner.baleen.jsonschema.v3

import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat
import com.fasterxml.jackson.module.jsonSchema.JsonSchema
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema
import com.fasterxml.jackson.module.jsonSchema.types.IntegerSchema
import com.fasterxml.jackson.module.jsonSchema.types.NumberSchema
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema
import com.fasterxml.jackson.module.jsonSchema.types.StringSchema
import com.fasterxml.jackson.module.jsonSchema.types.UnionTypeSchema
import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.generator.CoercibleHandlerOption
import com.shoprunner.baleen.generator.Options
import com.shoprunner.baleen.jsonschema.v3.JsonSchemaGenerator.writeTo
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.BooleanType
import com.shoprunner.baleen.types.DoubleType
import com.shoprunner.baleen.types.EnumType
import com.shoprunner.baleen.types.FloatType
import com.shoprunner.baleen.types.InstantType
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.NumericType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringCoercibleToBoolean
import com.shoprunner.baleen.types.StringCoercibleToFloat
import com.shoprunner.baleen.types.StringConstantType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.TimestampMillisType
import com.shoprunner.baleen.types.UnionType
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class JsonSchemaGeneratorTest {

    fun JsonSchemaGenerator.getJsonSchema(baleenType: BaleenType, options: JsonSchemaOptions = JsonSchemaOptions()): JsonSchema {
        return defaultTypeMapper(JsonSchemaGenerator::defaultTypeMapper, baleenType, options)
    }

    @Nested
    inner class Types {
        @Test
        fun `getJsonSchema encodes the resulting coerced type`() {
            val schema = JsonSchemaGenerator.getJsonSchema(StringCoercibleToFloat(FloatType()))
            Assertions.assertThat(schema.isStringSchema).isTrue()
        }

        @Test
        fun `getJsonSchema encodes the resulting coerced type and ignores AllowsNull`() {
            val schema = JsonSchemaGenerator.getJsonSchema(AllowsNull(StringCoercibleToFloat(FloatType())))
            Assertions.assertThat(schema.isStringSchema).isTrue()
        }

        @Test
        fun `getJsonSchema encodes the resulting coerced type with coerced handler property set to TO `() {
            val schema = JsonSchemaGenerator.getJsonSchema(StringCoercibleToFloat(FloatType()), JsonSchemaOptions(coercibleHandlerOption = CoercibleHandlerOption.TO))
            Assertions.assertThat(schema.isNumberSchema).isTrue()
        }

        @Test
        fun `getJsonSchema encodes the resulting coerced type and ignores AllowsNull with coerced handler property set to TO`() {
            val schema = JsonSchemaGenerator.getJsonSchema(AllowsNull(StringCoercibleToFloat(FloatType())), JsonSchemaOptions(coercibleHandlerOption = CoercibleHandlerOption.TO))
            Assertions.assertThat(schema.isNumberSchema).isTrue()
        }

        @Test
        fun `getJsonSchema encodes boolean type`() {
            val schema = JsonSchemaGenerator.getJsonSchema(BooleanType())
            Assertions.assertThat(schema.isBooleanSchema).isTrue()
        }

        @Test
        fun `getJsonSchema encodes float type`() {
            val schema = JsonSchemaGenerator.getJsonSchema(FloatType())
            Assertions.assertThat(schema.isNumberSchema).isTrue()
            Assertions.assertThat((schema as NumberSchema).minimum).isEqualTo(Float.NEGATIVE_INFINITY.toDouble())
            Assertions.assertThat(schema.maximum).isEqualTo(Float.POSITIVE_INFINITY.toDouble())
        }

        @Test
        fun `getJsonSchema encodes double type`() {
            val schema = JsonSchemaGenerator.getJsonSchema(DoubleType())
            Assertions.assertThat(schema.isNumberSchema).isTrue()
            Assertions.assertThat((schema as NumberSchema).minimum).isEqualTo(Double.NEGATIVE_INFINITY)
            Assertions.assertThat(schema.maximum).isEqualTo(Double.POSITIVE_INFINITY)
        }

        @Test
        fun `getJsonSchema encodes int type`() {
            val schema = JsonSchemaGenerator.getJsonSchema(IntType())
            Assertions.assertThat(schema.isIntegerSchema).isTrue()
            Assertions.assertThat((schema as IntegerSchema).minimum).isEqualTo(Integer.MIN_VALUE.toDouble())
            Assertions.assertThat(schema.maximum).isEqualTo(Integer.MAX_VALUE.toDouble())
        }

        @Test
        fun `getJsonSchema encodes long type`() {
            val schema = JsonSchemaGenerator.getJsonSchema(LongType())
            Assertions.assertThat(schema.isIntegerSchema).isTrue()
            Assertions.assertThat((schema as IntegerSchema).minimum).isEqualTo(Long.MIN_VALUE.toDouble())
            Assertions.assertThat(schema.maximum).isEqualTo(Long.MAX_VALUE.toDouble())
        }

        @Test
        @Suppress("USELESS_CAST")
        fun `getJsonSchema encodes integer type`() {
            val schema = JsonSchemaGenerator.getJsonSchema(IntegerType())
            Assertions.assertThat(schema.isIntegerSchema).isTrue()
            Assertions.assertThat((schema as IntegerSchema).minimum as Double?).isNull()
            Assertions.assertThat(schema.maximum as Double?).isNull()
        }

        @Test
        fun `getJsonSchema encodes integer type with limits`() {
            val schema = JsonSchemaGenerator.getJsonSchema(IntegerType(min = 0.toBigInteger(), max = 10.toBigInteger()))
            Assertions.assertThat(schema.isIntegerSchema).isTrue()
            Assertions.assertThat((schema as IntegerSchema).minimum).isEqualTo(0.0)
            Assertions.assertThat(schema.maximum).isEqualTo(10.0)
        }

        @Test
        @Suppress("USELESS_CAST")
        fun `getJsonSchema encodes numeric type`() {
            val schema = JsonSchemaGenerator.getJsonSchema(NumericType())
            Assertions.assertThat(schema.isNumberSchema).isTrue()
            Assertions.assertThat((schema as NumberSchema).minimum as Double?).isNull()
            Assertions.assertThat(schema.maximum as Double?).isNull()
        }

        @Test
        fun `getJsonSchema encodes numeric type with limits`() {
            val schema = JsonSchemaGenerator.getJsonSchema(NumericType(min = 0.toBigDecimal(), max = 10.toBigDecimal()))
            Assertions.assertThat(schema.isNumberSchema).isTrue()
            Assertions.assertThat((schema as NumberSchema).minimum).isEqualTo(0.0)
            Assertions.assertThat(schema.maximum).isEqualTo(10.0)
        }

        @Test
        fun `getJsonSchema encodes string type`() {
            val schema = JsonSchemaGenerator.getJsonSchema(StringType())
            Assertions.assertThat(schema.isStringSchema).isTrue()
            Assertions.assertThat((schema as StringSchema).minLength).isEqualTo(0)
            Assertions.assertThat(schema.maxLength).isEqualTo(Int.MAX_VALUE)
        }

        @Test
        fun `getJsonSchema encodes string constant type`() {
            val schema = JsonSchemaGenerator.getJsonSchema(StringConstantType("abc"))
            Assertions.assertThat(schema.isStringSchema).isTrue()
            Assertions.assertThat((schema as StringSchema).enums).containsExactly("abc")
        }

        @Test
        fun `getJsonSchema encodes enum type`() {
            val schema = JsonSchemaGenerator.getJsonSchema(EnumType("MyEmail", "a", "b", "c"))
            Assertions.assertThat(schema.isStringSchema).isTrue()
            Assertions.assertThat((schema as StringSchema).enums).containsExactly("a", "b", "c")
        }

        @Test
        fun `getJsonSchema encodes instant type`() {
            val schema = JsonSchemaGenerator.getJsonSchema(InstantType())
            Assertions.assertThat(schema.isStringSchema).isTrue()
            Assertions.assertThat((schema as StringSchema).format).isEqualTo(JsonValueFormat.DATE_TIME)
        }

        @Test
        fun `getJsonSchema encodes timestamp-millis type`() {
            val schema = JsonSchemaGenerator.getJsonSchema(TimestampMillisType())
            Assertions.assertThat(schema.isStringSchema).isTrue()
            Assertions.assertThat((schema as StringSchema).format).isEqualTo(JsonValueFormat.DATE_TIME)
        }

        @Test
        fun `getJsonSchema encodes map type`() {
            val schema = JsonSchemaGenerator.getJsonSchema(MapType(StringType(), IntType()))
            Assertions.assertThat(schema.isObjectSchema).isTrue()
            Assertions.assertThat((schema as ObjectSchema).additionalProperties).isInstanceOf(ObjectSchema.SchemaAdditionalProperties::class.java)
            Assertions.assertThat((schema.additionalProperties as ObjectSchema.SchemaAdditionalProperties).jsonSchema).isInstanceOf(IntegerSchema::class.java)
        }

        @Test
        fun `getJsonSchema encodes map type and ignores AllowsNull`() {
            val schema = JsonSchemaGenerator.getJsonSchema(AllowsNull(MapType(StringType(), IntType())))
            Assertions.assertThat(schema.isObjectSchema).isTrue()
            Assertions.assertThat((schema as ObjectSchema).additionalProperties).isInstanceOf(ObjectSchema.SchemaAdditionalProperties::class.java)
            Assertions.assertThat((schema.additionalProperties as ObjectSchema.SchemaAdditionalProperties).jsonSchema).isInstanceOf(IntegerSchema::class.java)
        }

        @Test
        fun `getJsonSchema fails map type with non-string key`() {
            org.junit.jupiter.api.Assertions.assertThrows(Exception::class.java) {
                JsonSchemaGenerator.getJsonSchema(MapType(IntType(), IntType()))
            }
        }

        @Test
        fun `getJsonSchema encodes occurences type`() {
            val schema = JsonSchemaGenerator.getJsonSchema(OccurrencesType(StringType()))
            Assertions.assertThat(schema.isArraySchema).isTrue()
            Assertions.assertThat((schema as ArraySchema).items).isInstanceOf(ArraySchema.SingleItems::class.java)
            Assertions.assertThat((schema.items as ArraySchema.SingleItems).schema).isInstanceOf(StringSchema::class.java)
        }

        @Test
        fun `getJsonSchema encodes occurences type and ignores AllowsNull`() {
            val schema = JsonSchemaGenerator.getJsonSchema(AllowsNull(OccurrencesType(StringType())))
            Assertions.assertThat(schema.isArraySchema).isTrue()
            Assertions.assertThat((schema as ArraySchema).items).isInstanceOf(ArraySchema.SingleItems::class.java)
            Assertions.assertThat((schema.items as ArraySchema.SingleItems).schema).isInstanceOf(StringSchema::class.java)
        }

        @Test
        fun `getJsonSchema encodes non-nullable union type`() {
            val schema = JsonSchemaGenerator.getJsonSchema(UnionType(IntType(), LongType()))
            Assertions.assertThat(schema.isUnionTypeSchema).isTrue()
            Assertions.assertThat((schema as UnionTypeSchema).elements).hasSize(2)
            Assertions.assertThat(schema.elements).hasOnlyElementsOfType(IntegerSchema::class.java)
        }

        @Test
        fun `getJsonSchema encodes union type of one not as a union`() {
            val schema = JsonSchemaGenerator.getJsonSchema(UnionType(IntType()))
            Assertions.assertThat(schema.isIntegerSchema).isTrue()
        }

        @Test
        fun `getJsonSchema encodes union type and ignores AllowNull`() {
            val schema = JsonSchemaGenerator.getJsonSchema(AllowsNull(UnionType(IntType(), LongType())))
            Assertions.assertThat(schema.isUnionTypeSchema).isTrue()
            Assertions.assertThat((schema as UnionTypeSchema).elements).hasSize(2)
            Assertions.assertThat(schema.elements).hasOnlyElementsOfType(IntegerSchema::class.java)
        }

        @Test
        fun `getJsonSchema fails union type with complex objact`() {
            val dogType = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") { p ->
                p.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog",
                        required = true
                )
            }

            org.junit.jupiter.api.Assertions.assertThrows(Exception::class.java) {
                JsonSchemaGenerator.getJsonSchema(UnionType(IntType(), dogType))
            }
        }

        @Test
        fun `getJsonSchema fails with an unsupported type`() {
            class BadType : BaleenType {
                override fun name() = "bad"
                override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> = emptySequence()
            }

            org.junit.jupiter.api.Assertions.assertThrows(Exception::class.java) {
                JsonSchemaGenerator.getJsonSchema(BadType())
            }
        }
    }

    @Nested
    inner class Models {

        @Test
        fun `model with single attribute`() {
            val description = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") { p ->
                p.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog",
                        required = true
                )
            }

            val schemaStr = """
            {
              "type" : "object",
              "id" : "com.shoprunner.data.dogs.Dog",
              "${'$'}schema" : "http://json-schema.org/draft-03/schema",
              "description" : "It's a dog. Ruff Ruff!",
              "properties" : {
                "name" : {
                  "type" : "string",
                  "required" : true,
                  "description" : "The name of the dog",
                  "maxLength" : 2147483647,
                  "minLength" : 0
                }
              }
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `model with multiple attribute`() {
            val description = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") { p ->
                p.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog"
                )

                p.attr(
                        name = "legs",
                        type = IntType(),
                        markdownDescription = "The number of legs the dog has"
                )
            }

            val schemaStr = """
            {
              "type" : "object",
              "id" : "com.shoprunner.data.dogs.Dog",
              "${'$'}schema" : "http://json-schema.org/draft-03/schema",
              "description" : "It's a dog. Ruff Ruff!",
              "properties" : {
                "name" : {
                  "type" : "string",
                  "description" : "The name of the dog",
                  "maxLength" : 2147483647,
                  "minLength" : 0
                },
                "legs" : {
                  "type" : "integer",
                  "description" : "The number of legs the dog has",
                  "maximum" : 2.147483647E9,
                  "minimum" : -2.147483648E9
                }
              }
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `model with optional attribute`() {
            val description = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") { p ->
                p.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog",
                        required = false
                )
            }

            val schemaStr = """
            {
              "type" : "object",
              "id" : "com.shoprunner.data.dogs.Dog",
              "${'$'}schema" : "http://json-schema.org/draft-03/schema",
              "description" : "It's a dog. Ruff Ruff!",
              "properties" : {
                "name" : {
                  "type" : "string",
                  "description" : "The name of the dog",
                  "maxLength" : 2147483647,
                  "minLength" : 0
                }
              }
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `model with nullable attribute`() {
            val description = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") { p ->
                p.attr(
                        name = "name",
                        type = AllowsNull(StringType()),
                        markdownDescription = "The name of the dog"
                )
            }

            val schemaStr = """
            {
              "type" : "object",
              "id" : "com.shoprunner.data.dogs.Dog",
              "${'$'}schema" : "http://json-schema.org/draft-03/schema",
              "description" : "It's a dog. Ruff Ruff!",
              "properties" : {
                "name" : {
                  "type" : "string",
                  "description" : "The name of the dog",
                  "maxLength" : 2147483647,
                  "minLength" : 0
                }
              }
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `nested model`() {
            val dogDescription = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") { p ->
                p.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog",
                        required = true
                )
            }

            val packDescription = Baleen.describe("Pack", "com.shoprunner.data.dogs", "It's a pack of Dogs!") { p ->
                p.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the pack",
                        required = true
                )

                p.attr(
                        name = "dogs",
                        type = OccurrencesType(dogDescription),
                        markdownDescription = "The dogs in the pack",
                        required = true
                )
            }

            val schemaStr = """
            {
              "type" : "object",
              "id" : "com.shoprunner.data.dogs.Pack",
              "${'$'}schema" : "http://json-schema.org/draft-03/schema",
              "description" : "It's a pack of Dogs!",
              "properties" : {
                "name" : {
                  "type" : "string",
                  "required" : true,
                  "description" : "The name of the pack",
                  "maxLength" : 2147483647,
                  "minLength" : 0
                },
                "dogs" : {
                  "type" : "array",
                  "required" : true,
                  "description" : "The dogs in the pack",
                  "items" : {
                    "type" : "object",
                    "id" : "com.shoprunner.data.dogs.Dog",
                    "description" : "It's a dog. Ruff Ruff!",
                    "properties" : {
                      "name" : {
                        "type" : "string",
                        "required" : true,
                        "description" : "The name of the dog",
                        "maxLength" : 2147483647,
                        "minLength" : 0
                      }
                    }
                  }
                }
              }
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(packDescription).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }
    }

    @Nested
    inner class Overrides {
        @Test
        fun `add an override when generating schema`() {
            val description = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
                it.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog"
                )

                it.attr(
                        name = "hasSpots",
                        type = StringCoercibleToBoolean(BooleanType()),
                        markdownDescription = "Does the dog have spots"
                )
            }

            fun mapStringCoercibleToBooleanAsStringSchema(baleenType: BaleenType, options: Options): JsonSchema =
                when (baleenType) {
                    is StringCoercibleToBoolean -> StringSchema()
                    else -> JsonSchemaGenerator.recursiveTypeMapper(::mapStringCoercibleToBooleanAsStringSchema, baleenType, options)
                }

            val schemaStr = """
                {
                  "type" : "object",
                  "id" : "com.shoprunner.data.dogs.Dog",
                  "${'$'}schema" : "http://json-schema.org/draft-03/schema",
                  "description" : "It's a dog. Ruff Ruff!",
                  "properties" : {
                    "name" : {
                      "type" : "string",
                      "description" : "The name of the dog",
                      "maxLength" : 2147483647,
                      "minLength" : 0
                    },
                    "hasSpots" : {
                      "type" : "string",
                      "description" : "Does the dog have spots"
                    }
                  }
                }
            """.trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description, JsonSchemaOptions(), ::mapStringCoercibleToBooleanAsStringSchema)
                .writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `add an override for a nested type when generating schema`() {
            val description = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
                it.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog"
                )

                it.attr(
                        name = "attributes",
                        type = Baleen.describe("Attributes", "com.shoprunner.data.dogs", "Attributes") { p ->
                            p.attr(
                                    name = "hasSpots",
                                    type = AllowsNull(StringCoercibleToBoolean(BooleanType())),
                                    markdownDescription = "Does the dog have spots"
                            )
                        },
                        markdownDescription = "Attributes"
                )
            }

            fun mapStringCoercibleToBooleanAsStringSchema(baleenType: BaleenType, options: Options): JsonSchema =
                when (baleenType) {
                    is StringCoercibleToBoolean -> StringSchema()
                    else -> JsonSchemaGenerator.recursiveTypeMapper(::mapStringCoercibleToBooleanAsStringSchema, baleenType, options)
                }

            val schemaStr = """
                {
                  "type" : "object",
                  "id" : "com.shoprunner.data.dogs.Dog",
                  "${'$'}schema" : "http://json-schema.org/draft-03/schema",
                  "description" : "It's a dog. Ruff Ruff!",
                  "properties" : {
                    "name" : {
                      "type" : "string",
                      "description" : "The name of the dog",
                      "maxLength" : 2147483647,
                      "minLength" : 0
                    },
                    "attributes" : {
                      "type" : "object",
                      "id" : "com.shoprunner.data.dogs.Attributes",
                      "description" : "Attributes",
                      "properties" : {
                        "hasSpots" : {
                          "type" : "string",
                          "description" : "Does the dog have spots"
                        }
                      }
                    }
                  }
                }
            """.trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description, JsonSchemaOptions(), ::mapStringCoercibleToBooleanAsStringSchema)
                .writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }
    }
}
