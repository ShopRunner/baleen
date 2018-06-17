package com.shoprunner.baleen.avro

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.types.CoercibleType
import com.shoprunner.baleen.types.BooleanType
import com.shoprunner.baleen.types.FloatType
import com.shoprunner.baleen.types.DoubleType
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.StringConstantType
import com.shoprunner.baleen.types.EnumType
import com.shoprunner.baleen.types.InstantType
import com.shoprunner.baleen.types.TimestampMillisType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.UnionType
import org.apache.avro.JsonProperties
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema
import java.io.File
import java.nio.file.Path

object AvroEncoder {

    fun getAvroSchema(baleenType: BaleenType): Schema {
        return when (baleenType) {
            is DataDescription -> encode(baleenType)
            is CoercibleType -> getAvroSchema(baleenType.type)
            is BooleanType -> Schema.create(Schema.Type.BOOLEAN)
            is FloatType -> Schema.create(Schema.Type.FLOAT)
            is DoubleType -> Schema.create(Schema.Type.DOUBLE)
            is IntType -> Schema.create(Schema.Type.INT)
            is LongType -> Schema.create(Schema.Type.LONG)
            is StringType -> Schema.create(Schema.Type.STRING)
            is StringConstantType -> Schema.create(Schema.Type.STRING)
            is EnumType -> Schema.createEnum(null, null, null, baleenType.enum.toList())
            is InstantType -> LogicalTypes.timestampMillis().addToSchema(Schema.create(Schema.Type.LONG))
            is TimestampMillisType -> LogicalTypes.timestampMillis().addToSchema(Schema.create(Schema.Type.LONG))
            /* TODO: More Logical Types */
            is MapType -> {
                if (baleenType.keyType !is StringType) throw Exception("Map keys can only be String in Avro")
                Schema.createMap(getAvroSchema(baleenType.valueType))
            }
            is OccurrencesType -> Schema.createArray(getAvroSchema(baleenType.memberType))
            is UnionType -> {
                val subTypes = baleenType.types.map(::getAvroSchema).distinct()
                if (subTypes.size == 1) {
                    subTypes[0]
                } else {
                    Schema.createUnion(subTypes)
                }
            }
            else -> throw Exception("Unknown type: " + baleenType::class.simpleName)
        }
    }

    fun encode(dataDescription: DataDescription): Schema {
        val fields = dataDescription.attrs.map { attr ->

            val avroSchema = getAvroSchema(attr.type)

            val optionalAvroSchema = if (attr.required) {
                avroSchema
            } else if (avroSchema.type != Schema.Type.UNION) {
                Schema.createUnion(Schema.create(Schema.Type.NULL), avroSchema)
            } else {
                Schema.createUnion(listOf(Schema.create(Schema.Type.NULL)) + avroSchema.types)
            }

            val field = Schema.Field(attr.name, optionalAvroSchema, attr.markdownDescription.trim(),
                    if (attr.required) null else JsonProperties.NULL_VALUE)

            attr.aliases.forEach(field::addAlias)
            field
        }

        return Schema.createRecord(dataDescription.name,
                dataDescription.markdownDescription.trim(),
                dataDescription.nameSpace, false, fields)
    }

    infix fun DataDescription.encodeTo(directory: File): File {
        val packageDir = File(directory, this.nameSpace.replace(".", "/"))
        packageDir.mkdirs()
        val avroFile = File(packageDir, "${this.name}.avsc")
        val schema = encode(this)

        avroFile.writeText(schema.toString(true))
        return directory
    }

    infix fun DataDescription.encodeTo(directory: Path): Path {
        return this.encodeTo(directory.toFile()).toPath()
    }

    infix fun DataDescription.encodeTo(out: Appendable): Appendable {
        val schema = encode(this)
        out.append(schema.toString(true))
        return out
    }
}