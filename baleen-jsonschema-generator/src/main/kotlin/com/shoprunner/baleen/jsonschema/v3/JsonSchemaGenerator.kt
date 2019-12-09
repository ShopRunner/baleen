package com.shoprunner.baleen.jsonschema.v3

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat
import com.fasterxml.jackson.module.jsonSchema.JsonSchema
import com.fasterxml.jackson.module.jsonSchema.types.ArraySchema
import com.fasterxml.jackson.module.jsonSchema.types.BooleanSchema
import com.fasterxml.jackson.module.jsonSchema.types.IntegerSchema
import com.fasterxml.jackson.module.jsonSchema.types.NumberSchema
import com.fasterxml.jackson.module.jsonSchema.types.ObjectSchema
import com.fasterxml.jackson.module.jsonSchema.types.StringSchema
import com.fasterxml.jackson.module.jsonSchema.types.UnionTypeSchema
import com.fasterxml.jackson.module.jsonSchema.types.ValueTypeSchema
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataDescription
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

object JsonSchemaGenerator {

    private fun encodeDescription(dataDescription: DataDescription): ObjectSchema {
        return ObjectSchema().apply {
            id = "${dataDescription.nameSpace}.${dataDescription.name}"
            description = dataDescription.markdownDescription
            dataDescription.attrs.forEach {
                val subTypeSchema = getJsonSchema(it.type).apply {
                    description = it.markdownDescription
                }
                if (it.required) {
                    putProperty(it.name, subTypeSchema)
                } else {
                    putOptionalProperty(it.name, subTypeSchema)
                }
            }
        }
    }

    fun getJsonSchema(baleenType: BaleenType): JsonSchema {
        return when (baleenType) {
            is DataDescription -> encodeDescription(baleenType)
            is CoercibleType<*, *> -> getJsonSchema(baleenType.type)
            is BooleanType -> BooleanSchema()
            is FloatType -> NumberSchema().apply {
                maximum = baleenType.max.toDouble()
                minimum = baleenType.min.toDouble()
            }
            is DoubleType -> NumberSchema().apply {
                maximum = baleenType.max
                minimum = baleenType.min
            }
            is IntType -> IntegerSchema().apply {
                maximum = baleenType.max.toDouble()
                minimum = baleenType.min.toDouble()
            }
            is IntegerType -> IntegerSchema().apply {
                maximum = baleenType.max?.toDouble()
                minimum = baleenType.min?.toDouble()
            }
            is LongType -> IntegerSchema().apply {
                maximum = baleenType.max.toDouble()
                minimum = baleenType.min.toDouble()
            }
            is NumericType -> NumberSchema().apply {
                maximum = baleenType.max?.toDouble()
                minimum = baleenType.min?.toDouble()
            }
            is StringType -> StringSchema().apply {
                maxLength = baleenType.max
                minLength = baleenType.min
            }
            is StringConstantType -> StringSchema().apply {
                enums = setOf(baleenType.constant)
            }
            is EnumType -> StringSchema().apply {
                enums = baleenType.enum.toSet()
            }
            is InstantType -> StringSchema().apply {
                format = JsonValueFormat.DATE_TIME
            }
            is TimestampMillisType -> StringSchema().apply {
                format = JsonValueFormat.DATE_TIME
            }
        /* TODO: More Logical Types */
            is MapType -> {
                if (baleenType.keyType !is StringType) throw Exception("Map keys can only be String in RootJsonSchema")
                ObjectSchema().apply {
                    additionalProperties = ObjectSchema.SchemaAdditionalProperties(getJsonSchema(baleenType.valueType))
                }
            }
            is OccurrencesType -> ArraySchema().apply {
                setItemsSchema(getJsonSchema(baleenType.memberType))
            }
            is UnionType -> {
                val subTypeSchemas = baleenType.types.map { getJsonSchema(it) }.distinct()
                if (subTypeSchemas.size == 1) {
                    subTypeSchemas[0]
                } else {
                    UnionTypeSchema().apply {
                        if (subTypeSchemas.any { !it.isValueTypeSchema }) {
                            throw Exception("Union types only accept primitive types")
                        }
                        elements = subTypeSchemas.map { it as ValueTypeSchema }.toTypedArray()
                    }
                }
            }
        // V3 Does not support
            is AllowsNull<*> -> getJsonSchema(baleenType.type)
            else -> throw Exception("Unknown type: " + baleenType::class.simpleName)
        }
    }

    fun encode(dataDescription: DataDescription): ObjectSchema {
        return encodeDescription(dataDescription).apply {
            `$schema` = "http://json-schema.org/draft-03/schema"
        }
    }

    fun ObjectSchema.writeTo(directory: File, prettyPrint: Boolean = false): File {
        val lastDot = this.id.lastIndexOf('.')
        val namespace = this.id.substring(0, lastDot)
        val name = this.id.substring(lastDot)
        val packageDir = java.io.File(directory, namespace.replace(".", "/"))
        packageDir.mkdirs()
        val schemaFile = java.io.File(packageDir, "$name.schema.json")

        if (prettyPrint) {
            ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(schemaFile, this)
        } else {
            ObjectMapper().writeValue(schemaFile, this)
        }
        return directory
    }

    fun ObjectSchema.writeTo(directory: Path, prettyPrint: Boolean = false): Path {
        return this.writeTo(directory.toFile(), prettyPrint).toPath()
    }

    fun ObjectSchema.writeTo(out: Appendable, prettyPrint: Boolean = false): Appendable {
        if (prettyPrint) {
            out.append(ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this))
        } else {
            out.append(ObjectMapper().writeValueAsString(this))
        }
        return out
    }
}
