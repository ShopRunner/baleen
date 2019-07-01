package com.shoprunner.baleen.version2

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.ValidationAssert.Companion.assertThat
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.StringType
import org.apache.avro.generic.GenericDatumReader
import org.apache.avro.generic.GenericDatumWriter
import org.apache.avro.generic.GenericRecord
import org.apache.avro.generic.GenericRecordBuilder
import org.apache.avro.io.DecoderFactory
import org.apache.avro.io.EncoderFactory
import org.assertj.core.api.Assertions.assertThat
import org.everit.json.schema.loader.SchemaLoader
import org.json.JSONObject
import org.json.JSONTokener
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import javax.xml.XMLConstants
import javax.xml.transform.stream.StreamSource
import javax.xml.validation.SchemaFactory
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * Given a Baleen schema, create external schema (Avro, XSD, Json-Schema, Kotlin data class) and
 * validate the data with the external schema
 */
class UseCase4Test {

    @Nested
    inner class `Given a Baleen schema` {
        val schema = "dog".describeAs {
            attr("name", StringType(), required = true)
            attr("numLegs", AllowsNull(IntType()))
        }

        @Test
        fun `create an XSD and validate the data against it`() {
            val data = """
                <dog>
                    <name>Fido</name>
                    <numLegs>4</numLegs>
                </dog>
            """.trimIndent()

            val xsd = schema.toXSD()

            val dataFile = with(File.createTempFile("DogData-BaleenUseCase4", ".xml")) {
                deleteOnExit()
                writeText(data)
                this
            }

            val xsdFile = with(File.createTempFile("DogSchema-BaleenUseCase4", ".xsd")) {
                deleteOnExit()
                writeText(xsd)
                this
            }

            val schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
            val schema = schemaFactory.newSchema(xsdFile)
            val validator = schema.newValidator()

            validator.validate(StreamSource(dataFile))
        }

        @Test
        fun `create an json schema and validate the data against it`() {
            val data = """
                {
                    "name":"Fido",
                    "numLegs": 4
                }
            """.trimIndent()

            val jsonSchemaStr = schema.toJsonSchema()

            val jsonSchema = JSONObject(JSONTokener(jsonSchemaStr))
            val jsonData = JSONObject(JSONTokener(data))

            val schema = SchemaLoader.load(jsonSchema)
            schema.validate(jsonData)
        }

        @Test
        fun `create an avro schema and validate the data against it`() {
            val unknownDataSchema = """
                {
                   "type": "record",
                   "name": "Dog",
                   "fields": [
                        { "name": "name", "type": "string" },
                        { "name": "numLegs", "type": ["null", "int"], "default": null }
                   ]
                }
            """.trimIndent().asAvroSchema()

            val data = GenericRecordBuilder(unknownDataSchema)
                .set("name", "Fido")
                .set("numLegs", 4)
                .build()

            val dataStream = with(ByteArrayOutputStream()) {
                val writer = GenericDatumWriter<GenericRecord>(unknownDataSchema)
                val encoder = EncoderFactory().directBinaryEncoder(this, null)
                writer.write(data, encoder)
                encoder.flush()
                ByteArrayInputStream(this.toByteArray())
            }

            val avroSchema = schema.toAvroSchema()

            // Read data stream with our generated avro schema. It should be same as the original.
            val record = with(dataStream) {
                val reader = GenericDatumReader<GenericRecord>(avroSchema)
                val decoder = DecoderFactory().directBinaryDecoder(this, null)
                reader.read(null, decoder)
            }

            // Verify that the new generic record has the right data
            assertThat(record["name"].toString()).isEqualTo("Fido")
            assertThat(record["numLegs"]).isEqualTo(4)
        }

        @Test
        fun `create an data class and validate the class structure`() {
            val dataClass = schema.toDataClass()

            assertThat(dataClass.isData).isTrue()
            assertThat(dataClass.simpleName).isEqualTo("dog")

            val nameField = dataClass.memberProperties.find { it.name == "name" }
            val numLegsField = dataClass.memberProperties.find { it.name == "numLegs" }

            assertThat(nameField?.returnType?.classifier).isEqualTo(String::class)
            assertThat(nameField?.returnType?.isMarkedNullable).isFalse()
            assertThat(numLegsField?.returnType?.classifier).isEqualTo(Int::class)
            assertThat(numLegsField?.returnType?.isMarkedNullable).isTrue()

            val instance = dataClass.primaryConstructor?.call("Fido", 4)
            assertThat(instance).isNotNull()

            assertThat(instance.validate(schema)).isValid()
        }

        @Test
        fun `create an data class instance and validate it against the schema`() {
            val data = schema.toDataClassInstance("Fido", 4)

            assertThat(data.validate(schema)).isValid()
        }
    }
}
