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
import com.shoprunner.baleen.generator.BaseGenerator
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.BooleanType
import com.shoprunner.baleen.types.CoercibleType
import com.shoprunner.baleen.types.DoubleType
import com.shoprunner.baleen.types.EnumType
import com.shoprunner.baleen.types.ErrorsAreWarnings
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

object JsonSchemaGenerator : BaseGenerator<JsonSchema, JsonSchemaOptions> {

    fun encodeDescription(dataDescription: DataDescription, typeMapper: JsonSchemaTypeMapper, options: JsonSchemaOptions): ObjectSchema {
        return ObjectSchema().apply {
            id = "${dataDescription.nameSpace}.${dataDescription.name}"
            description = dataDescription.markdownDescription
            dataDescription.attrs.forEach {
                val subTypeSchema = typeMapper(it.type, options).apply {
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

    override fun defaultTypeMapper(
        typeMapper: JsonSchemaTypeMapper,
        baleenType: BaleenType,
        options: JsonSchemaOptions
    ): JsonSchema =
        when (baleenType) {
            is DataDescription -> encodeDescription(baleenType, typeMapper, options)
            is CoercibleType<*, *> -> typeMapper(baleenType.toSubType(options.coercibleHandlerOption), options)
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
                    additionalProperties = ObjectSchema.SchemaAdditionalProperties(typeMapper(baleenType.valueType, options))
                }
            }
            is OccurrencesType -> ArraySchema().apply {
                setItemsSchema(typeMapper(baleenType.memberType, options))
            }
            is UnionType -> {
                val subTypeSchemas = baleenType.types.map { typeMapper(it, options) }.distinct()
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
            is ErrorsAreWarnings<*> -> typeMapper(baleenType.type, options)
        // V3 Does not support
            is AllowsNull<*> -> typeMapper(baleenType.type, options)
            else -> throw Exception("Unknown type: " + baleenType::class.simpleName)
        }

    fun encode(dataDescription: DataDescription, options: JsonSchemaOptions = JsonSchemaOptions(), typeMapper: JsonSchemaTypeMapper = JsonSchemaGenerator::defaultTypeMapper): ObjectSchema {
        return encodeDescription(dataDescription, typeMapper, options).apply {
            `$schema` = "http://json-schema.org/draft-03/schema"
        }
    }

    fun ObjectSchema.writeTo(directory: File, prettyPrint: Boolean = false): File {
        val lastDot = this.id.lastIndexOf('.')
        val namespace = this.id.substring(0, lastDot)
        val name = this.id.substring(lastDot)
        val packageDir = File(directory, namespace.replace(".", "/"))
        packageDir.mkdirs()
        val schemaFile = File(packageDir, "$name.schema.json")

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
