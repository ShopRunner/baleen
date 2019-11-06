package com.shoprunner.baleen.jsonschema.v4

import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.jsonschema.v4.JsonSchemaGenerator.writeTo
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
import com.shoprunner.baleen.types.StringCoercibleToFloat
import com.shoprunner.baleen.types.StringConstantType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.TimestampMillisType
import com.shoprunner.baleen.types.UnionType
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class JsonSchemaGeneratorTest {

    @Nested
    inner class Types {
        @Test
        fun `getJsonSchema encodes the resulting coerced type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "number",
                        type = StringCoercibleToFloat(FloatType())
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties": false,
                  "properties" : {
                    "number" : {
                      "type" : "number"
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes the resulting nullable coerced type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "number",
                        type = AllowsNull(StringCoercibleToFloat(FloatType()))
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "number" : {
                      "oneOf" : [
                        {
                          "type": "null"
                        },
                        {
                          "type": "number"
                        }
                      ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes boolean type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "hasLegs",
                        type = BooleanType()
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "hasLegs" : {
                      "type": "boolean"
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes float type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "number",
                        type = FloatType(min = -10.0f, max = 10.0f)
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties": false,
                  "properties" : {
                    "number" : {
                      "type" : "number",
                      "maximum" : 10.0,
                      "minimum" : -10.0
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes double type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "number",
                        type = DoubleType(min = -10.0, max = 10.0)
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties": false,
                  "properties" : {
                    "number" : {
                      "type" : "number",
                      "maximum" : 10.0,
                      "minimum" : -10.0
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes int type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "numLegs",
                        type = IntType()
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties": false,
                  "properties" : {
                    "numLegs" : {
                      "type" : "integer",
                      "maximum" : 2147483647,
                      "minimum" : -2147483648
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes long type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "numLegs",
                        type = LongType()
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties": false,
                  "properties" : {
                    "numLegs" : {
                      "type" : "integer",
                      "maximum" : 9223372036854775807,
                      "minimum" : -9223372036854775808
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes integer type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                    name = "numLegs",
                    type = IntegerType()
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties": false,
                  "properties" : {
                    "numLegs" : {
                      "type" : "integer"
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes integer type with limits`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                    name = "numLegs",
                    type = IntegerType(min = 0.toBigInteger(), max = 10.toBigInteger())
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties": false,
                  "properties" : {
                    "numLegs" : {
                      "type" : "integer",
                      "maximum" : 10,
                      "minimum" : 0
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes numeric type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                    name = "numLegs",
                    type = NumericType()
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties": false,
                  "properties" : {
                    "numLegs" : {
                      "type" : "number"
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes numeric type with limits`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                    name = "numLegs",
                    type = NumericType(min = 0.toBigDecimal(), max = 10.toBigDecimal())
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties": false,
                  "properties" : {
                    "numLegs" : {
                      "type" : "number",
                      "maximum" : 10,
                      "minimum" : 0
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes string type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "name",
                        type = StringType(min = 1, max = 10)
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "type" : "string",
                      "maxLength" : 10,
                      "minLength" : 1
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes string constant type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "name",
                        type = StringConstantType("Fido")
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "type" : "string",
                      "enum" : [ "Fido" ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes enum type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "color",
                        type = EnumType("Color", "brown", "black", "white")
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "color" : {
                      "type" : "string",
                      "enum" : [ "brown", "black", "white" ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes instant type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "birthday",
                        type = InstantType()
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "birthday" : {
                      "type" : "string",
                      "format" : "date-time"
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes timestamp-millis type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "birthday",
                        type = TimestampMillisType()
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "birthday" : {
                      "type" : "string",
                      "format" : "date-time"
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes map type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "favorite_items",
                        type = MapType(StringType(), StringType())
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "favorite_items" : {
                      "type" : "object",
                      "additionalProperties" : {
                        "type" : "string",
                        "maxLength" : 2147483647,
                        "minLength" : 0
                      }
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes map type with AllowsNull`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "favorite_items",
                        type = AllowsNull(MapType(StringType(), StringType()))
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "favorite_items" : {
                      "oneOf" : [
                        {
                          "type" : "null"
                        },
                        {
                          "type" : "object",
                          "additionalProperties" : {
                            "type" : "string",
                            "maxLength" : 2147483647,
                            "minLength" : 0
                          }
                        }
                      ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema fails map type with non-string key`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "bad_map",
                        type = MapType(IntType(), IntType())
                )
            }

            org.junit.jupiter.api.Assertions.assertThrows(Exception::class.java) {
                JsonSchemaGenerator.encode(description)
            }
        }

        @Test
        fun `getJsonSchema encodes occurences type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "favorite_items",
                        type = OccurrencesType(StringType())
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "favorite_items" : {
                      "type" : "array",
                      "items" : {
                        "type" : "string",
                        "maxLength" : 2147483647,
                        "minLength" : 0
                      }
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes occurences type with AllowsNull`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "favorite_items",
                        type = AllowsNull(OccurrencesType(StringType()))
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "favorite_items" : {
                      "oneOf" : [
                        {
                          "type" : "null"
                        },
                        {
                          "type" : "array",
                          "items" : {
                            "type" : "string",
                            "maxLength" : 2147483647,
                            "minLength" : 0
                          }
                        }
                      ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes occurence type with complex object`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "color",
                        type = OccurrencesType(
                                Baleen.describe("Color") { n ->
                                    n.attr(
                                            name = "color",
                                            type = StringType()
                                    )
                                }
                        )
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Color" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "color" : {
                      "type" : "string",
                      "maxLength" : 2147483647,
                      "minLength" : 0
                    }
                  }
                },
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "color" : {
                      "type" : "array",
                      "items" : {
                        "${'$'}ref" : "#/definitions/record:Color"
                      }
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes non-nullable union type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "num_legs",
                        type = UnionType(IntType(), LongType())
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "num_legs" : {
                      "oneOf" : [
                        {
                          "type" : "integer",
                          "maximum" : 2147483647,
                          "minimum" : -2147483648
                        },
                        {
                          "type" : "integer",
                          "maximum" : 9223372036854775807,
                          "minimum" : -9223372036854775808
                        }
                      ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes union type of one not as a union`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "num_legs",
                        type = UnionType(IntType())
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "num_legs" : {
                      "type" : "integer",
                      "maximum" : 2147483647,
                      "minimum" : -2147483648
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes union type with AllowNull`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "num_legs",
                        type = AllowsNull(UnionType(IntType(), LongType()))
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "num_legs" : {
                      "oneOf" : [
                        {
                          "type" : "null"
                        },
                        {
                          "type" : "integer",
                          "maximum" : 2147483647,
                          "minimum" : -2147483648
                        },
                        {
                          "type" : "integer",
                          "maximum" : 9223372036854775807,
                          "minimum" : -9223372036854775808
                        }
                      ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema encodes union type with complex object`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "name",
                        type = UnionType(
                                StringType(),
                                Baleen.describe("Name") { n ->
                                    n.attr(
                                            name = "name",
                                            type = StringType()
                                    )
                                }
                        )
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "oneOf" : [
                        {
                          "type" : "string",
                          "maxLength" : 2147483647,
                          "minLength" : 0
                        },
                        {
                          "${'$'}ref" : "#/definitions/record:Name"
                        }
                      ]
                    }
                  }
                },
                "record:Name" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "type" : "string",
                      "maxLength" : 2147483647,
                      "minLength" : 0
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `getJsonSchema fails with an unsupported type`() {
            class BadType : BaleenType {
                override fun name() = "bad"
                override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> = emptySequence()
            }

            val description = Baleen.describe("Dog") {
                it.attr(
                        name = "bad_type",
                        type = BadType()
                )
            }

            org.junit.jupiter.api.Assertions.assertThrows(Exception::class.java) {
                JsonSchemaGenerator.encode(description)
            }
        }

        @Test
        fun `getJsonSchema encodes overriden type`() {
            val description = Baleen.describe("Dog") {
                it.attr(
                    name = "birthday",
                    type = InstantType()
                )
            }

            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "birthday" : {
                      "oneOf" : [ {
                        "type" : "string",
                        "format" : "date-time"
                      }, {
                        "type" : "integer"
                      } ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            val typeOverride = TypeOverride(
                isOverridable = { t -> t.name() == InstantType().name() },
                override = { OneOf(listOf(DateTimeSchema(), IntegerSchema())) }
            )
            JsonSchemaGenerator.encode(description, Options(typeOverrides = listOf(typeOverride))).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }
    }

    @Nested
    inner class Models {

        @Test
        fun `model with single attribute`() {
            val description = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
                it.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog"
                )
            }

            val schemaStr = """
            {
              "id" : "com.shoprunner.data.dogs.Dog",
              "definitions" : {
                "record:com.shoprunner.data.dogs.Dog" : {
                  "description" : "It's a dog. Ruff Ruff!",
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "description" : "The name of the dog",
                      "type" : "string",
                      "maxLength" : 2147483647,
                      "minLength" : 0
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `model with multiple attribute`() {
            val description = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
                it.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog"
                )

                it.attr(
                        name = "legs",
                        type = IntType(),
                        markdownDescription = "The number of legs the dog has"
                )
            }

            val schemaStr = """
            {
              "id" : "com.shoprunner.data.dogs.Dog",
              "definitions" : {
                "record:com.shoprunner.data.dogs.Dog" : {
                  "description" : "It's a dog. Ruff Ruff!",
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "description" : "The name of the dog",
                      "type" : "string",
                      "maxLength" : 2147483647,
                      "minLength" : 0
                    },
                    "legs" : {
                      "description" : "The number of legs the dog has",
                      "type" : "integer",
                      "maximum" : 2147483647,
                      "minimum" : -2147483648
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `model with required attribute`() {
            val description = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
                it.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog",
                        required = true
                )
            }

            val schemaStr = """
            {
              "id" : "com.shoprunner.data.dogs.Dog",
              "definitions" : {
                "record:com.shoprunner.data.dogs.Dog" : {
                  "description" : "It's a dog. Ruff Ruff!",
                  "type" : "object",
                  "required" : [ "name" ],
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "description" : "The name of the dog",
                      "type" : "string",
                      "maxLength" : 2147483647,
                      "minLength" : 0
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `model with nullable attribute`() {
            val description = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
                it.attr(
                        name = "name",
                        type = AllowsNull(StringType()),
                        markdownDescription = "The name of the dog"
                )
            }

            val schemaStr = """
            {
              "id" : "com.shoprunner.data.dogs.Dog",
              "definitions" : {
                "record:com.shoprunner.data.dogs.Dog" : {
                  "description" : "It's a dog. Ruff Ruff!",
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "description" : "The name of the dog",
                      "oneOf" : [
                        {
                          "type" : "null"
                        },
                        {
                          "type" : "string",
                          "maxLength" : 2147483647,
                          "minLength" : 0
                        }
                      ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"

            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `model with attribute with default`() {
            val description = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
                it.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog",
                        default = "Fido"
                )
            }

            val schemaStr = """
            {
              "id" : "com.shoprunner.data.dogs.Dog",
              "definitions" : {
                "record:com.shoprunner.data.dogs.Dog" : {
                  "description" : "It's a dog. Ruff Ruff!",
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "description" : "The name of the dog",
                      "default": "Fido",
                      "type" : "string",
                      "maxLength" : 2147483647,
                      "minLength" : 0
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"

            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `model with nullable attribute default to null`() {
            val description = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
                it.attr(
                        name = "name",
                        type = AllowsNull(StringType()),
                        markdownDescription = "The name of the dog",
                        default = null
                )
            }

            val schemaStr = """
            {
              "id" : "com.shoprunner.data.dogs.Dog",
              "definitions" : {
                "record:com.shoprunner.data.dogs.Dog" : {
                  "description" : "It's a dog. Ruff Ruff!",
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "description" : "The name of the dog",
                      "default": null,
                      "oneOf" : [
                        {
                          "type" : "null"
                        },
                        {
                          "type" : "string",
                          "maxLength" : 2147483647,
                          "minLength" : 0
                        }
                      ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"

            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `model with nullable attribute is not required if option set`() {
            val description = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
                it.attr(
                    name = "name",
                    type = StringType(),
                    markdownDescription = "The name of the dog",
                    required = true
                )

                it.attr(
                    name = "address",
                    type = AllowsNull(StringType()),
                    markdownDescription = "The address of the dog",
                    required = true
                )
            }

            val schemaStr = """
            {
              "id" : "com.shoprunner.data.dogs.Dog",
              "definitions" : {
                "record:com.shoprunner.data.dogs.Dog" : {
                  "description" : "It's a dog. Ruff Ruff!",
                  "type" : "object",
                  "required" : [ "name" ],
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "description" : "The name of the dog",
                      "type" : "string",
                      "maxLength" : 2147483647,
                      "minLength" : 0
                    },
                    "address" : {
                      "description" : "The address of the dog",
                      "oneOf" : [
                        {
                          "type" : "null"
                        },
                        {
                          "type" : "string",
                          "maxLength" : 2147483647,
                          "minLength" : 0
                        }
                      ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"

            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(description, Options(nullableFieldsNotRequired = true)).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `nested model`() {
            val dogDescription = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
                it.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog",
                        required = true
                )
            }

            val packDescription = Baleen.describe("Pack", "com.shoprunner.data.dogs", "It's a pack of Dogs!") {
                it.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the pack",
                        required = true
                )

                it.attr(
                        name = "dogs",
                        type = OccurrencesType(dogDescription),
                        markdownDescription = "The dogs in the pack",
                        required = true
                )
            }

            val schemaStr = """
            {
              "id" : "com.shoprunner.data.dogs.Pack",
              "definitions" : {
                "record:com.shoprunner.data.dogs.Dog" : {
                  "description" : "It's a dog. Ruff Ruff!",
                  "type" : "object",
                  "required" : [ "name" ],
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "description" : "The name of the dog",
                      "type" : "string",
                      "maxLength" : 2147483647,
                      "minLength" : 0
                    }
                  }
                },
                "record:com.shoprunner.data.dogs.Pack" : {
                  "description" : "It's a pack of Dogs!",
                  "type" : "object",
                  "required" : [ "name", "dogs" ],
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "description" : "The name of the pack",
                      "type" : "string",
                      "maxLength" : 2147483647,
                      "minLength" : 0
                    },
                    "dogs" : {
                      "description" : "The dogs in the pack",
                      "type" : "array",
                      "items" : {
                        "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Dog"
                      }
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Pack",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(packDescription).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `support data with additional attributes`() {
            val dogDescription = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
                it.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog",
                        required = true
                )
            }

            val packDescription = Baleen.describe("Pack", "com.shoprunner.data.dogs", "It's a pack of Dogs!") {
                it.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the pack",
                        required = true
                )

                it.attr(
                        name = "dogs",
                        type = OccurrencesType(dogDescription),
                        markdownDescription = "The dogs in the pack",
                        required = true
                )
            }

            val schemaStr = """
            {
              "id" : "com.shoprunner.data.dogs.Pack",
              "definitions" : {
                "record:com.shoprunner.data.dogs.Dog" : {
                  "description" : "It's a dog. Ruff Ruff!",
                  "type" : "object",
                  "required" : [ "name" ],
                  "additionalProperties" : true,
                  "properties" : {
                    "name" : {
                      "description" : "The name of the dog",
                      "type" : "string",
                      "maxLength" : 2147483647,
                      "minLength" : 0
                    }
                  }
                },
                "record:com.shoprunner.data.dogs.Pack" : {
                  "description" : "It's a pack of Dogs!",
                  "type" : "object",
                  "required" : [ "name", "dogs" ],
                  "additionalProperties" : true,
                  "properties" : {
                    "name" : {
                      "description" : "The name of the pack",
                      "type" : "string",
                      "maxLength" : 2147483647,
                      "minLength" : 0
                    },
                    "dogs" : {
                      "description" : "The dogs in the pack",
                      "type" : "array",
                      "items" : {
                        "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Dog"
                      }
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Pack",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator.encode(packDescription, true).writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `model as self-describing`() {
            val description = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
                it.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog"
                )
            }

            val schemaStr = """
            {
              "id" : "com.shoprunner.data.dogs.Dog",
              "definitions" : {
                "record:com.shoprunner.data.dogs.Dog" : {
                  "description" : "It's a dog. Ruff Ruff!",
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "description" : "The name of the dog",
                      "type" : "string",
                      "maxLength" : 2147483647,
                      "minLength" : 0
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Dog",
              "${'$'}schema" : "http://iglucentral.com/schemas/com.snowplowananalytics.self-desc/schema/jsonschema/1-0-0",
              "self" : {
                "vendor" : "com.shoprunner.data.dogs",
                "name" : "Dog",
                "version" : "0-1-0",
                "format" : "jsonschema"
              }
            }""".trimIndent()

            val outputStream = ByteArrayOutputStream()
            JsonSchemaGenerator
                    .encodeAsSelfDescribing(description, "0-1-0")
                    .writeTo(PrintStream(outputStream), true)

            Assertions.assertThat(outputStream.toString()).isEqualToIgnoringWhitespace(schemaStr)
        }
    }

    @Nested
    inner class Writers {

        @Test
        fun `write to file`() {
            val Dog = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
                it.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog",
                        required = true
                )
                it.attr(
                        name = "legs",
                        type = AllowsNull(IntType()),
                        markdownDescription = "The number of legs",
                        required = false,
                        default = null
                )
            }

            val schemaStr = """
            {
              "id" : "com.shoprunner.data.dogs.Dog",
              "definitions" : {
                "record:com.shoprunner.data.dogs.Dog" : {
                  "description" : "It's a dog. Ruff Ruff!",
                  "type" : "object",
                  "required" : [ "name" ],
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "description" : "The name of the dog",
                      "type" : "string",
                      "maxLength" : 2147483647,
                      "minLength" : 0
                    },
                    "legs" : {
                      "description" : "The number of legs",
                      "default" : null,
                      "oneOf" : [ {
                        "type" : "null"
                      }, {
                        "type" : "integer",
                        "maximum" : 2147483647,
                        "minimum" : -2147483648
                      } ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }
            """.trimIndent()

            val dir = File("build/jsonschema-gen-test/tofile")
            dir.mkdirs()
            JsonSchemaGenerator.encode(Dog).writeTo(dir, true)

            val dogFile = File(dir, "com/shoprunner/data/dogs/Dog.schema.json")
            Assertions.assertThat(dogFile).exists()
            val content = dogFile.readText()

            Assertions.assertThat(content).isEqualToIgnoringWhitespace(schemaStr)
        }

        @Test
        fun `write to path`() {
            val Dog = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
                it.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog",
                        required = true
                )
                it.attr(
                        name = "legs",
                        type = AllowsNull(IntType()),
                        markdownDescription = "The number of legs",
                        required = false,
                        default = null
                )
            }

            val schemaStr = """
            {
              "id" : "com.shoprunner.data.dogs.Dog",
              "definitions" : {
                "record:com.shoprunner.data.dogs.Dog" : {
                  "description" : "It's a dog. Ruff Ruff!",
                  "type" : "object",
                  "required" : [ "name" ],
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "description" : "The name of the dog",
                      "type" : "string",
                      "maxLength" : 2147483647,
                      "minLength" : 0
                    },
                    "legs" : {
                      "description" : "The number of legs",
                      "default" : null,
                      "oneOf" : [ {
                        "type" : "null"
                      }, {
                        "type" : "integer",
                        "maximum" : 2147483647,
                        "minimum" : -2147483648
                      } ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }
            """.trimIndent()

            val dir = File("build/jsonschema-gen-test/topath")
            dir.mkdirs()
            JsonSchemaGenerator.encode(Dog).writeTo(dir.toPath(), true)

            val dogFile = File(dir, "com/shoprunner/data/dogs/Dog.schema.json")
            Assertions.assertThat(dogFile).exists()
            val content = dogFile.readText()

            Assertions.assertThat(content).isEqualToIgnoringWhitespace(schemaStr)
        }
    }
}
