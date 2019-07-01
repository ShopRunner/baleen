package com.shoprunner.baleen.version2

import com.shoprunner.baleen.ValidationAssert.Companion.assertThat
import org.apache.avro.generic.GenericRecordBuilder
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

/**
 * Given a external schema (Avro, XSD, Json-Schema, Kotlin data class), build Baleen schema and validate the data
 */
class UseCase3Test {
    data class Dog(var name: String, var numLegs: Int?)

    @Nested
    inner class `Given an external` {

        @Nested
        inner class XSD {
            val xsd = """
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                    <xs:complexType name="Dog">
                        <xs:annotation>
                            <xs:documentation>Type of dog.</xs:documentation>
                        </xs:annotation>
                        <xs:sequence>
                            <xs:element name="name">
                                <xs:annotation>
                                    <xs:documentation>Name of the dog.</xs:documentation>
                                </xs:annotation>
                                <xs:simpleType type="xs:string" />
                            </xs:element>
                            <xs:element minOccurs="0" name="numLegs">
                                <xs:annotation>
                                    <xs:documentation>The number of legs.</xs:documentation>
                                </xs:annotation>
                                <xs:simpleType type="xs:int" />
                           </xs:element>
                        </xs:sequence>
                    </xs:complexType>
                    <xs:element name="dog" type="Dog">
                        <xs:annotation>
                            <xs:documentation>Type of dog.</xs:documentation>
                        </xs:annotation>
                    </xs:element>
                </xs:schema>
            """.trimIndent()

            val data = """
                <dog>
                    <name>Fido</name>
                    <numLegs>4</numLegs>
                </dog>
            """.trimIndent()

            @Test
            fun `then generate Baleen schema and validate the data`() {
                val schema = xsd.asXSD().learnSchema()
                assertThat(data.byteInputStream().validate(schema, XmlDataHandler)).isValid()
            }
        }

        @Nested
        inner class `json schema` {
            val jsonSchema = """
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
                          "type" : "string"
                        },
                        "numLegs" : {
                          "description" : "The number of legs the dog has",
                          "type" : "integer"
                        }
                      }
                    }
                  },
                  "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Dog",
                  "${'$'}schema" : "http://json-schema.org/draft-04/schema"
                }
            """.trimIndent()

            val data = """
                {
                    "name": "Fido",
                    "numLegs": 4
                }
            """.trimIndent()

            @Test
            fun `then generate Baleen schema and validate the data`() {
                val schema = jsonSchema.asJsonSchema().learnSchema()
                assertThat(data.byteInputStream().validate(schema, JsonDataHandler)).isValid()
            }
        }

        @Nested
        inner class `Avro schema` {
            val avroSchema = """
                {
                   "type": "record",
                   "name": "Dog",
                   "fields": [
                        { "name": "name", "type": "string" },
                        { "name": "numLegs", "type": ["null", "int"], "default": null }
                   ]
                }
            """.trimIndent().asAvroSchema()

            val data = GenericRecordBuilder(avroSchema)
                .set("name", "Fido")
                .set("numLegs", 4)
                .build()

            @Test
            fun `then generate Baleen schema and validate the data`() {
                val schema = avroSchema.learnSchema()
                assertThat(data.validate(schema)).isValid()
            }
        }

        @Nested
        inner class `Kotlin data class` {
            val data = Dog("Fido", 4)

            @Test
            fun `then generate Baleen schema and validate the data`() {
                val schema = Dog::class.learnSchema()
                assertThat(data.validate(schema)).isValid()
            }
        }
    }
}