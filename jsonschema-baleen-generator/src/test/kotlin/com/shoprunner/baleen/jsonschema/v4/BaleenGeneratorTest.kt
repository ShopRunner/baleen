package com.shoprunner.baleen.jsonschema.v4

import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.jsonschema.v4.BaleenGenerator.parseJsonSchema
import java.io.File
import java.io.StringWriter
import java.net.URL
import java.net.URLClassLoader
import java.net.UnknownHostException
import java.util.logging.Logger
import org.assertj.core.api.Assertions
import org.jetbrains.kotlin.cli.common.arguments.K2JVMCompilerArguments
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageLocation
import org.jetbrains.kotlin.cli.common.messages.CompilerMessageSeverity
import org.jetbrains.kotlin.cli.common.messages.MessageCollector
import org.jetbrains.kotlin.cli.jvm.K2JVMCompiler
import org.jetbrains.kotlin.config.Services
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BaleenGeneratorTest {

    @Nested
    inner class Utils {
        @Test
        fun `#getNamepaceAndName parses namespace and name from "id"`() {
            val schema = RootJsonSchema("com.shoprunner.data.Dog", emptyMap(), "", "")

            val (namespace, name) = BaleenGenerator.getNamespaceAndName(schema)

            Assertions.assertThat(namespace).isEqualTo("com.shoprunner.data")
            Assertions.assertThat(name).isEqualTo("Dog")
        }

        @Test
        fun `#getNamepaceAndName parses name without namespace from "id"`() {
            val schema = RootJsonSchema("Dog", emptyMap(), "", "")

            val (namespace, name) = BaleenGenerator.getNamespaceAndName(schema)

            Assertions.assertThat(namespace).isEqualTo("")
            Assertions.assertThat(name).isEqualTo("Dog")
        }

        @Test
        fun `#getNamepaceAndName parses name and namespace from "self"`() {
            val schema = RootJsonSchema(null, emptyMap(), "", "", SelfDescribing(
                    "com.shoprunner.data",
                    "Dog",
                    "0-0-0"
            ))

            val (namespace, name) = BaleenGenerator.getNamespaceAndName(schema)

            Assertions.assertThat(namespace).isEqualTo("com.shoprunner.data")
            Assertions.assertThat(name).isEqualTo("Dog")
        }

        @Test
        fun `#getNamepaceAndName parses name and namespace from "$ref"`() {
            val schema = RootJsonSchema(null, emptyMap(), "#/definitions/record:com.shoprunner.data.Dog", "")

            val (namespace, name) = BaleenGenerator.getNamespaceAndName(schema)

            Assertions.assertThat(namespace).isEqualTo("com.shoprunner.data")
            Assertions.assertThat(name).isEqualTo("Dog")
        }

        @Test
        fun `#getNamepaceAndName parses name without namespace from "$ref"`() {
            val schema = RootJsonSchema(null, emptyMap(), "#/definitions/record:Dog", "")

            val (namespace, name) = BaleenGenerator.getNamespaceAndName(schema)

            Assertions.assertThat(namespace).isEqualTo("")
            Assertions.assertThat(name).isEqualTo("Dog")
        }

        @Test
        fun `#getNamepaceAndName parses name without any information`() {
            val schema = RootJsonSchema(null, emptyMap(), null, "")

            val (namespace, name) = BaleenGenerator.getNamespaceAndName(schema)

            Assertions.assertThat(namespace).isEqualTo("")
            Assertions.assertThat(name).isEqualTo("NoName")
        }

        @Test
        fun `#getNamepaceAndName parses name and namespace from record`() {
            val (namespace, name) = BaleenGenerator.getNamespaceAndName("#/definitions/record:com.shoprunner.data.Dog")

            Assertions.assertThat(namespace).isEqualTo("com.shoprunner.data")
            Assertions.assertThat(name).isEqualTo("Dog")
        }

        @Test
        fun `#getNamepaceAndName parses name without namespace from record`() {
            val (namespace, name) = BaleenGenerator.getNamespaceAndName("#/definitions/record:Dog")

            Assertions.assertThat(namespace).isEqualTo("")
            Assertions.assertThat(name).isEqualTo("Dog")
        }
    }

    @Nested
    inner class JsonType2Baleen {
        @Test
        fun `json boolean converts to Baleen`() {
            val schemaStr = """
            {
              "id" : "com.shopunner.data.Dog",
              "definitions" : {
                "record:com.shopunner.data.Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "hasLegs" : {
                      "type": "boolean"
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:com.shopunner.data.Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                package com.shopunner.data

                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.BooleanType

                val Dog: DataDescription = describe("Dog", "com.shopunner.data", "") {
                    it.attr(
                            name = "hasLegs",
                            type = BooleanType()
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json boolean with default value converts to Baleen`() {
            val schemaStr = """
            {
              "id" : "com.shopunner.data.Dog",
              "definitions" : {
                "record:com.shopunner.data.Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "hasLegs" : {
                      "type": "boolean",
                      "default": true
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:com.shopunner.data.Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                package com.shopunner.data

                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.BooleanType

                val Dog: DataDescription = describe("Dog", "com.shopunner.data", "") {
                    it.attr(
                            name = "hasLegs",
                            type = BooleanType(),
                            default = true
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json number converts to double in Baleen`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "number" : {
                      "type": "number"
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.NumericType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "number",
                            type = NumericType()
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json number with min and max converts to numeric in Baleen`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "number" : {
                      "type": "number",
                      "minimum" : 0,
                      "maximum" : 100
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.NumericType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "number",
                            type = NumericType(min = 0.toBigDecimal(), max = 100.toBigDecimal())
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json number with default value converts to double in Baleen`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "number" : {
                      "type": "number",
                      "default" : 1.1
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.NumericType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "number",
                            type = NumericType(),
                            default = 1.1
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json integer converts to long in Baleen`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "numLegs" : {
                      "type": "integer"
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.IntegerType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "numLegs",
                            type = IntegerType()
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json integer with min and max converts to long in Baleen`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "numLegs" : {
                      "type": "integer",
                      "minimum" : 0,
                      "maximum" : 100
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.IntegerType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "numLegs",
                            type = IntegerType(min = 0.toBigInteger(), max = 100.toBigInteger())
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json integer with default value converts to long in Baleen`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "numLegs" : {
                      "type": "integer",
                      "default" : 10
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.IntegerType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "numLegs",
                            type = IntegerType(),
                            default = 10L
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json string converts to string in Baleen`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "type": "string"
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.StringType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "name",
                            type = StringType()
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json string with default value converts to string in Baleen`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "type": "string",
                      "default" : "Hello"
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.StringType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "name",
                            type = StringType(),
                            default = "Hello"
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json string with min and max lengths converts to string in Baleen`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "type": "string",
                      "minLength" : 5,
                      "maxLength" : 20
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.StringType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "name",
                            type = StringType(5, 20)
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json string with enums converts to enum in Baleen`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "color" : {
                      "type": "string",
                      "enum" : [ "black", "brown", "white" ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.EnumType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "color",
                            type = EnumType("EnumBBW", listOf("black", "brown", "white"))
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json string with enum of 1 converts to string constant in Baleen`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "name" : {
                      "type": "string",
                      "enum" : [ "Fido" ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.StringConstantType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "name",
                            type = StringConstantType("Fido")
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json string with format date-time converts to instant in Baleen`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "birthday" : {
                      "type": "string",
                      "format" : "date-time"
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.InstantType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "birthday",
                            type = InstantType()
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json string with unsupported format converts to string in Baleen`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "email" : {
                      "type": "string",
                      "format" : "email"
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.StringType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "email",
                            type = StringType()
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json array converts to occurences in Baleen`() {
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

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.OccurrencesType
                import com.shoprunner.baleen.types.StringType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "favorite_items",
                            type = OccurrencesType(StringType())
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json object with additionalProperties converts to map in Baleen`() {
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

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.MapType
                import com.shoprunner.baleen.types.StringType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "favorite_items",
                            type = MapType(StringType(), StringType())
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json oneOf converts to union in Baleen`() {
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
                          "type" : "string"
                        },
                        {
                          "type" : "integer"
                        }
                      ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.IntegerType
                import com.shoprunner.baleen.types.StringType
                import com.shoprunner.baleen.types.UnionType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "num_legs",
                            type = UnionType(StringType(), IntegerType())
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json anyOf fails`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "num_legs" : {
                      "anyOf" : [
                        {
                          "type" : "string"
                        },
                        {
                          "type" : "integer"
                        }
                      ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.IntegerType
                import com.shoprunner.baleen.types.StringType
                import com.shoprunner.baleen.types.UnionType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "num_legs",
                            type = UnionType(StringType(), IntegerType())
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json allOf fails`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "num_legs" : {
                      "allOf" : [
                        {
                          "type" : "string"
                        },
                        {
                          "type" : "integer"
                        }
                      ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            Assertions
                    .assertThatThrownBy { BaleenGenerator.encode(schemaStr.parseJsonSchema()) }
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessageContaining("json type AllOf not supported")
        }

        @Test
        fun `json not fails`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "num_legs" : {
                      "not" : {
                          "type" : "string"
                      }
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            Assertions
                    .assertThatThrownBy { BaleenGenerator.encode(schemaStr.parseJsonSchema()) }
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessageContaining("json type Not not supported")
        }

        @Test
        fun `json oneOf with duplicate types converts to duplicate types in Baleen`() {
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
                          "type" : "integer"
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

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.IntegerType
                import com.shoprunner.baleen.types.UnionType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "num_legs",
                            type = UnionType(
                                IntegerType(), 
                                IntegerType(
                                    min = -9223372036854775808.toBigInteger(), 
                                    max = 9223372036854775807.toBigInteger()
                                )
                            )
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json oneOf with duplicate types but different maxes converts to union type in Baleen`() {
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

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.IntegerType
                import com.shoprunner.baleen.types.UnionType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                        name = "num_legs",
                        type = UnionType(
                            IntegerType(
                                min = -2147483648.toBigInteger(), 
                                max = 2147483647.toBigInteger()
                            ), 
                            IntegerType(
                                min = -9223372036854775808.toBigInteger(), 
                                max = 9223372036854775807.toBigInteger()
                            )
                        )
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json oneOf one value converts to a single type Baleen`() {
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
                          "type" : "integer"
                        }
                      ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.IntegerType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "num_legs",
                            type = IntegerType()
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json oneOf with null converts to AllowsNull in Baleen`() {
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
                          "type" : "integer"
                        }
                      ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.AllowsNull
                import com.shoprunner.baleen.types.IntegerType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "num_legs",
                            type = AllowsNull(IntegerType())
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }

        @Test
        fun `json oneOf with null and default null converts to AllowsNull in Baleen`() {
            val schemaStr = """
            {
              "id" : "Dog",
              "definitions" : {
                "record:Dog" : {
                  "type" : "object",
                  "additionalProperties" : false,
                  "properties" : {
                    "num_legs" : {
                      "default": null,
                      "oneOf" : [
                        {
                          "type" : "null"
                        },
                        {
                          "type" : "integer"
                        }
                      ]
                    }
                  }
                }
              },
              "${'$'}ref" : "#/definitions/record:Dog",
              "${'$'}schema" : "http://json-schema.org/draft-04/schema"
            }""".trimIndent()

            val descriptionStr = """
                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.AllowsNull
                import com.shoprunner.baleen.types.IntegerType

                val Dog: DataDescription = describe("Dog", "", "") {
                    it.attr(
                            name = "num_legs",
                            type = AllowsNull(IntegerType()),
                            default = null
                    )
                }
            """.trimIndent()

            val outputStream = StringWriter()
            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            descriptions.first().writeTo(outputStream)
            val outputStr = outputStream.toString()

            Assertions.assertThat(descriptions).hasSize(1)
            Assertions.assertThat(outputStr).isEqualToIgnoringWhitespace(descriptionStr)
        }
    }

    @Nested
    inner class Models {
        @Test
        fun `nested models convert to multiple Baleen entries`() {
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
                    },
                    "num_legs" : {
                      "description" : "The number of legs a dog has",
                      "type" : "integer",
                      "default": 4
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
            }
            """.trimIndent()

            val dogDescriptionStr = """
                package com.shoprunner.data.dogs

                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.IntegerType
                import com.shoprunner.baleen.types.StringType

                /**
                 * It's a dog. Ruff Ruff! */
                val Dog: DataDescription = describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
                    it.attr(
                            name = "name",
                            type = StringType(),
                            markdownDescription = "The name of the dog",
                            required = true
                    )
                    it.attr(
                            name = "num_legs",
                            type = IntegerType(),
                            markdownDescription = "The number of legs a dog has",
                            default = 4L
                    )
                }
            """.trimIndent()

            val packDescriptionStr = """
                package com.shoprunner.data.dogs

                import com.shoprunner.baleen.Baleen.describe
                import com.shoprunner.baleen.DataDescription
                import com.shoprunner.baleen.types.OccurrencesType
                import com.shoprunner.baleen.types.StringType

                /**
                 * It's a pack of Dogs! */
                val Pack: DataDescription = describe("Pack", "com.shoprunner.data.dogs", "It's a pack of Dogs!") {
                    it.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the pack",
                        required = true
                    )
                    it.attr(
                        name = "dogs",
                        type = OccurrencesType(com.shoprunner.data.dogs.Dog),
                        markdownDescription = "The dogs in the pack",
                        required = true
                    )
                }
            """.trimIndent()

            val descriptions = BaleenGenerator.encode(schemaStr.parseJsonSchema())
            val dogOutputStream = StringWriter()
            descriptions.first().writeTo(dogOutputStream)
            val dogOutputStr = dogOutputStream.toString()

            val packOutputStream = StringWriter()
            descriptions[1].writeTo(packOutputStream)
            val packOutputStr = packOutputStream.toString()

            Assertions.assertThat(descriptions).hasSize(2)
            Assertions.assertThat(dogOutputStr).isEqualToIgnoringWhitespace(dogDescriptionStr)
            Assertions.assertThat(packOutputStr).isEqualToIgnoringWhitespace(packDescriptionStr)
        }
    }

    @Nested
    inner class CheckFileGeneration {
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
                },
                "num_legs" : {
                  "description" : "The number of legs a dog has",
                  "type" : "integer",
                  "default": 4
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
        }
        """.trimIndent()

        inner class LogMessageCollector : MessageCollector {
            val logger = Logger.getLogger("LogMessageCollector")

            override fun clear() = Unit

            override fun hasErrors() = false

            override fun report(severity: CompilerMessageSeverity, message: String, location: CompilerMessageLocation?) {
                when (severity) {
                    CompilerMessageSeverity.ERROR -> logger.severe("$message : $location")
                    CompilerMessageSeverity.EXCEPTION -> logger.severe("$message : $location")
                    CompilerMessageSeverity.STRONG_WARNING -> logger.warning("$message : $location")
                    CompilerMessageSeverity.WARNING -> logger.warning("$message : $location")
                    else -> logger.info("$severity: $message : $location")
                }
            }
        }

        @Test
        fun `generate code from Json Schema that compiles`() {
            // Setup
            val dir = File("build/baleen-gen-test")
            val sourceDir = File(dir, "src/main/kotlin")
            sourceDir.mkdirs()
            val classesDir = File(dir, "classes/main/kotlin")
            classesDir.mkdirs()

            // Generate Baleen Kotlin Files
            BaleenGenerator.encode(schemaStr.parseJsonSchema()).forEach {
                it.writeTo(sourceDir)
            }

            val dogFile = File(sourceDir, "com/shoprunner/data/dogs/Dog.kt")
            Assertions.assertThat(dogFile).exists()

            val packFile = File(sourceDir, "com/shoprunner/data/dogs/Pack.kt")
            Assertions.assertThat(packFile).exists()

            // Needs the Environment Variable passed in in order to compile. Gradle can give us this.
            Assertions.assertThat(System.getenv("GEN_CLASSPATH")).isNotBlank()

            val compiler = K2JVMCompiler()
            val args = K2JVMCompilerArguments().apply {
                destination = classesDir.path
                freeArgs = listOf(sourceDir.path)
                classpath = System.getenv("GEN_CLASSPATH")
                noStdlib = true
            }
            compiler.exec(LogMessageCollector(), Services.EMPTY, args)

            // Check that compilation worked
            Assertions.assertThat(File(classesDir, "com/shoprunner/data/dogs/DogKt.class")).exists()
            Assertions.assertThat(File(classesDir, "com/shoprunner/data/dogs/PackKt.class")).exists()

            // Check if the compiled files can be loaded
            val cl = URLClassLoader(arrayOf(classesDir.toURI().toURL()))

            val dogType = cl.loadClass("com.shoprunner.data.dogs.DogKt")
            val dogDescription = dogType.getDeclaredField("Dog").type
            Assertions.assertThat(dogDescription).isEqualTo(DataDescription::class.java)

            val packType = cl.loadClass("com.shoprunner.data.dogs.PackKt")
            val packDescription = packType.getDeclaredField("Pack").type
            Assertions.assertThat(packDescription).isEqualTo(DataDescription::class.java)
        }

        @Test
        fun `generate code from url that compiles`() {
            try {
                // Setup
                val dir = File("build/baleen-gen-test")
                val sourceDir = File(dir, "src/main/kotlin")
                sourceDir.mkdirs()
                val classesDir = File(dir, "classes/main/kotlin")
                classesDir.mkdirs()

                // Generate Baleen Kotlin Files
                val schemaUrl =
                    URL("https://raw.githubusercontent.com/snowplow/iglu-central/master/schemas/com.google.analytics/cookies/jsonschema/1-0-0")
                BaleenGenerator.encode(schemaUrl.parseJsonSchema()).forEach {
                    it.writeTo(sourceDir)
                }

                val cookiesFile = File(sourceDir, "com/google/analytics/cookies.kt")
                Assertions.assertThat(cookiesFile).exists()

                // Needs the Environment Variable passed in in order to compile. Gradle can give us this.
                val genClassPath = System.getenv("GEN_CLASSPATH")
                if (genClassPath?.isNotBlank() == true) {
                    val compiler = K2JVMCompiler()
                    val args = K2JVMCompilerArguments().apply {
                        destination = classesDir.path
                        freeArgs = listOf(sourceDir.path)
                        classpath = genClassPath
                        noStdlib = true
                    }
                    compiler.exec(LogMessageCollector(), Services.EMPTY, args)

                    // Check that compilation worked
                    Assertions.assertThat(File(classesDir, "com/google/analytics/CookiesKt.class")).exists()

                    // Check if the compiled files can be loaded
                    val cl = URLClassLoader(arrayOf(classesDir.toURI().toURL()))

                    val cookiesType = cl.loadClass("com.google.analytics.CookiesKt")
                    val cookiesDescription = cookiesType.getDeclaredField("cookies").type
                    Assertions.assertThat(cookiesDescription).isEqualTo(DataDescription::class.java)
                }
            } catch (e: UnknownHostException) {
                // Don't fail the test if there is no connection
                return
            }
        }
    }
}
