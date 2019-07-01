package com.shoprunner.baleen.version2

import com.shoprunner.baleen.ValidationAssert.Companion.assertThat
import org.apache.avro.Schema
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.generic.GenericRecord
import org.apache.avro.generic.GenericRecordBuilder
import org.apache.avro.io.EncoderFactory
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * Given we have the data, we learn the schema and validate the data
 */
class UseCase2Test {

    data class Dog(var name: String, var numLegs: Int?)

    @Nested
    inner class `Start with the data and learn the schema` {

        @Nested
        inner class `then with existing map data` {

            val data = listOf(
                mapOf("name" to "Fido", "numLegs" to 4),
                mapOf("name" to "Dug", "numLegs" to 4)
            )

            @Test
            fun `the data can be validated`() {
                val schema = data.learnSchema()
                assertThat(data.validate(schema)).isValid()
            }
        }

        @Nested
        inner class `then with existing csv data` {

            val data = """
                "Fido",4
                "Dug",4
            """.trimIndent()

            @Test
            fun `the data can be validated`() {
                val dataHandler = CsvDataHandler()
                val schema = data.byteInputStream().learnSchema(dataHandler)
                assertThat(data.byteInputStream().validate(schema, dataHandler)).isValid()
            }
        }

        @Nested
        inner class `then with existing xml data` {

            val data = """
                <pack>
                    <dog>
                        <name>Fido</name>
                        <numLegs>4</numLegs>
                    </dog>
                    <dog>
                        <name>Dug</name>
                        <numLegs>4</numLegs>
                    </dog>
                </pack>
            """.trimIndent()

            @Test
            fun `the data can be validated`() {
                val schema = data.byteInputStream().learnSchema(XmlDataHandler)
                assertThat(data.byteInputStream().validate(schema, XmlDataHandler)).isValid()
            }
        }

        @Nested
        inner class `then with existing json data` {

            @Nested
            inner class `as a single json object` {
                val data = """
                    {
                        "name": "Fido",
                        "numLegs": 4
                    }
                """.trimIndent()

                @Test
                fun `the data can be validated`() {
                    val schema = data.byteInputStream().learnSchema(JsonDataHandler)
                    assertThat(data.byteInputStream().validate(schema, JsonDataHandler)).isValid()
                }
            }

            @Nested
            inner class `as one json object per line` {
                val data = """
                    { "name": "Fido", "numLegs": 4 }
                    { "name": "Dug", "numLegs": 4 }
                """.trimIndent()

                @Test
                fun `the data can be validated`() {
                    val schema = data.byteInputStream().learnSchema(JsonPerLineDataHandler)
                    assertThat(data.byteInputStream().validate(schema, JsonPerLineDataHandler)).isValid()
                }
            }

            @Nested
            inner class `as a json array` {
                val data = """
                    [
                        { "name": "Fido", "numLegs": 4 },
                        { "name": "Dug", "numLegs": 4 }
                    ]
                """.trimIndent()

                @Test
                fun `the data can be validated`() {
                    val schema = data.byteInputStream().learnSchema(JsonArrayDataHandler)
                    assertThat(data.byteInputStream().validate(schema, JsonArrayDataHandler)).isValid()
                }
            }
        }

        @Nested
        inner class `then with existing avro data` {
            val parser = Schema.Parser()
            val avroSchemaStr = """
                {
                   "type": "record",
                   "name": "Dog",
                   "fields": [
                        { "name": "name", "type": "string" },
                        { "name": "numLegs", "type": ["null", "int"], "default": null }
                   ]
                }
            """.trimIndent()
            val avroSchema = parser.parse(avroSchemaStr)

            @Nested
            inner class `from a GenericRecord` {

                val data = GenericRecordBuilder(avroSchema)
                    .set("name", "Fido")
                    .set("numLegs", 4)
                    .build()

                @Test
                fun `the data can be validated`() {
                    val schema = data.learnSchema()
                    assertThat(data.validate(schema)).isValid()
                }
            }

            @Nested
            inner class `from an Avro InputStream` {
                val data = with(ByteArrayOutputStream()) {
                    val record = GenericRecordBuilder(avroSchema)
                        .set("name", "Fido")
                        .set("numLegs", 4)
                        .build()

                    val writer = GenericDatumWriter<GenericRecord>(avroSchema)
                    val encoder = EncoderFactory().directBinaryEncoder(this, null)
                    writer.write(record, encoder)
                    encoder.flush()
                    ByteArrayInputStream(this.toByteArray())
                }

                @Test
                fun `the data can be validated`() {
                    val schema = data.learnSchema(AvroDataHandler)
                    assertThat(data.validate(schema, AvroDataHandler)).isValid()
                }
            }
        }

        @Nested
        inner class `then with existing kotlin data object` {

            val data = Dog("Fido", 4)

            val dataList = listOf(
                data,
                Dog("Dug", 4)
            )

            @Test
            fun `the data can be validated`() {
                val schema = data.learnSchema()
                assertThat(data.validate(schema)).isValid()
            }

            @Test
            fun `the data list can be validated`() {
                val schema = dataList.learnSchema()
                assertThat(dataList.validate(schema)).isValid()
            }
        }
    }
}
