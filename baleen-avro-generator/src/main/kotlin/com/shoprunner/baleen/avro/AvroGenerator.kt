package com.shoprunner.baleen.avro

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.NoDefault
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.BooleanType
import com.shoprunner.baleen.types.CoercibleType
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
import com.shoprunner.baleen.types.StringConstantType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.TimestampMillisType
import com.shoprunner.baleen.types.UnionType
import java.io.File
import java.nio.file.Path
import org.apache.avro.JsonProperties
import org.apache.avro.LogicalTypes
import org.apache.avro.Schema

object AvroGenerator {

    fun getAvroSchema(baleenType: BaleenType): Schema {
        return when (baleenType) {
            is DataDescription -> encode(baleenType)
            is CoercibleType<*, *> -> getAvroSchema(baleenType.type)
            is BooleanType -> Schema.create(Schema.Type.BOOLEAN)
            is FloatType -> Schema.create(Schema.Type.FLOAT)
            is DoubleType -> Schema.create(Schema.Type.DOUBLE)
            is IntType -> Schema.create(Schema.Type.INT)
            is LongType -> Schema.create(Schema.Type.LONG)
            is IntegerType -> Schema.create(Schema.Type.LONG)
            is NumericType -> Schema.create(Schema.Type.DOUBLE)
            is StringType -> Schema.create(Schema.Type.STRING)
            is StringConstantType -> Schema.create(Schema.Type.STRING)
            is EnumType -> Schema.createEnum(baleenType.enumName, null, null, baleenType.enum.toList())
            is InstantType -> LogicalTypes.timestampMillis().addToSchema(Schema.create(Schema.Type.LONG))
            is TimestampMillisType -> LogicalTypes.timestampMillis().addToSchema(Schema.create(Schema.Type.LONG))
            /* TODO: More Logical Types */
            is MapType -> {
                if (baleenType.keyType !is StringType) throw Exception("Map keys can only be String in Avro")
                Schema.createMap(getAvroSchema(baleenType.valueType))
            }
            is OccurrencesType -> Schema.createArray(getAvroSchema(baleenType.memberType))
            is AllowsNull<*> -> {
                val subType = getAvroSchema(baleenType.type)
                val nullSchema = Schema.create(Schema.Type.NULL)
                if (subType.type == Schema.Type.UNION) {
                    Schema.createUnion((listOf(nullSchema) + subType.types).distinct())
                } else {
                    Schema.createUnion(listOf(nullSchema, subType))
                }
            }
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
            if (!attr.required && attr.default == NoDefault) {
                throw IllegalArgumentException("Optional value without the required default value for ${attr.name}")
            }

            val avroSchema =
                    if (!attr.required && attr.default == null) getAvroSchema(AllowsNull(attr.type))
                    else getAvroSchema(attr.type)

            val defaultValue = when (attr.default) {
                NoDefault -> null
                null -> JsonProperties.NULL_VALUE
                else -> attr.default
            }

            val field = Schema.Field(attr.name, avroSchema, attr.markdownDescription.trim(), defaultValue)

            attr.aliases.forEach(field::addAlias)
            field
        }

        return Schema.createRecord(dataDescription.name,
                dataDescription.markdownDescription.trim(),
                dataDescription.nameSpace, false, fields)
    }

    fun Schema.writeTo(directory: File): File {
        val packageDir = File(directory, this.namespace.replace(".", "/"))
        packageDir.mkdirs()
        val avroFile = File(packageDir, "${this.name}.avsc")

        avroFile.writeText(this.toString(true))
        return directory
    }

    fun Schema.writeTo(directory: Path): Path {
        return this.writeTo(directory.toFile()).toPath()
    }

    fun Schema.writeTo(out: Appendable): Appendable {
        out.append(this.toString(true))
        return out
    }
}
