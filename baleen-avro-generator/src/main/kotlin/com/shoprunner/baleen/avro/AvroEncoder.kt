package com.shoprunner.baleen.avro

import com.shoprunner.baleen.DataDescription
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema
import java.io.PrintStream

object AvroEncoder {

    fun getAvroType(dataDescription: DataDescription): Schema {
        val fields = dataDescription.attrs.map { attr ->

            val type = attr.type
            val avroType = if (type is DataDescription) {
                getAvroType(type)
            } else {
                val typeName = attr.type.name()
                when (typeName) {
                    "date" -> LogicalTypes.date().addToSchema(Schema.create(Schema.Type.INT))
                    "float" -> Schema.create(Schema.Type.FLOAT)
                    "long" -> Schema.create(Schema.Type.LONG)
                    "string" -> Schema.create(Schema.Type.STRING)
                    "timestampMillis" -> LogicalTypes.timestampMillis().addToSchema(Schema.create(Schema.Type.LONG))
                    else -> throw Exception("Unknown type: " + typeName)
                }
            }

            val optionalAvroType = if (attr.required) {
                avroType
            } else {
                Schema.createUnion(avroType, Schema.create(Schema.Type.NULL))
            }

            val field = Schema.Field(attr.name, optionalAvroType, attr.markdownDescription.trim(), null as Any? )

            attr.aliases.forEach { x -> field.addAlias(x) }
            field
        }

        return Schema.createRecord(dataDescription.name,
                dataDescription.markdownDescription.trim(),
                dataDescription.nameSpace, false, fields)
    }

    fun encode(dataDescription: DataDescription, outputStream: PrintStream) {

        // TODO namespace

        val schema = getAvroType(dataDescription)

        outputStream.println(schema.toString(true))
    }
}