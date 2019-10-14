package com.shoprunner.baleen.jsonschema.v4

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
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

object JsonSchemaGenerator {

    private fun encodeDescription(dataDescription: DataDescription, objectContext: Map<String, ObjectSchema>, withAdditionalAttributes: Boolean): Pair<ObjectReference, Map<String, ObjectSchema>> {
        val mutObjectContext = objectContext.toMutableMap()
        val id = if (dataDescription.nameSpace.isNotBlank()) "${dataDescription.nameSpace}.${dataDescription.name}" else dataDescription.name
        val ref = "record:$id"
        val requiredProperties = dataDescription.attrs.filter { it.required }.map { it.name }
        val objectSchema = ObjectSchema(
                required = if (requiredProperties.isNotEmpty()) requiredProperties else null,
                additionalProperties = withAdditionalAttributes,
                properties = dataDescription.attrs.map {
                    val subType = getJsonSchema(it.type, objectContext, withAdditionalAttributes)
                    val subTypeSchema = subType.first.apply {
                        description = if (it.markdownDescription.isNotBlank()) it.markdownDescription else null
                        default = when (it.default) {
                            NoDefault -> null
                            null -> Null
                            else -> it.default
                        }
                    }
                    val subTypeContext = subType.second
                    mutObjectContext.putAll(subTypeContext) // yucky side effect
                    it.name to subTypeSchema
                }.toMap()
        ).apply {
            description = if (dataDescription.markdownDescription.isNotBlank()) dataDescription.markdownDescription else null
        }
        val referenceSchema = ObjectReference("#/definitions/$ref")
        return referenceSchema to (mutObjectContext.toMap() + (ref to objectSchema))
    }

    fun getJsonSchema(baleenType: BaleenType, objectContext: Map<String, ObjectSchema>, withAdditionalAttributes: Boolean): Pair<JsonSchema, Map<String, ObjectSchema>> {
        return when (baleenType) {
            is DataDescription -> encodeDescription(baleenType, objectContext, withAdditionalAttributes)
            is AllowsNull<*> -> {
                val (subSchema, subContext) = getJsonSchema(baleenType.type, objectContext, withAdditionalAttributes)
                if (subSchema is OneOf) {
                    OneOf(listOf(NullSchema()) + subSchema.oneOf) to subContext
                } else {
                    OneOf(listOf(NullSchema(), subSchema)) to subContext
                }
            }
            is BooleanType -> BooleanSchema to objectContext
            is CoercibleType<*, *> -> getJsonSchema(baleenType.type, objectContext, withAdditionalAttributes)
            is DoubleType -> NumberSchema(
                maximum = baleenType.max.takeIf { it.isFinite() }?.toBigDecimal(),
                minimum = baleenType.min.takeIf { it.isFinite() }?.toBigDecimal()
            ) to objectContext
            is IntType -> IntegerSchema(
                maximum = baleenType.max.toBigInteger(),
                minimum = baleenType.min.toBigInteger()
            ) to objectContext
            is IntegerType -> IntegerSchema(
                maximum = baleenType.max,
                minimum = baleenType.min
            ) to objectContext
            is EnumType -> StringSchema(
                    enum = baleenType.enum
            ) to objectContext
            is MapType -> {
                if (baleenType.keyType !is StringType) {
                    throw Exception("Map keys can only be String")
                }
                val (subSchema, subContext) = getJsonSchema(baleenType.valueType, objectContext, withAdditionalAttributes)
                MapSchema(additionalProperties = subSchema) to subContext
            }
            is FloatType -> NumberSchema(
                maximum = baleenType.max.takeIf { it.isFinite() }?.toBigDecimal(),
                minimum = baleenType.min.takeIf { it.isFinite() }?.toBigDecimal()
            ) to objectContext
            is InstantType -> DateTimeSchema() to objectContext
            is LongType -> IntegerSchema(
                maximum = baleenType.max.toBigInteger(),
                minimum = baleenType.min.toBigInteger()
            ) to objectContext
            is NumericType -> NumberSchema(
                maximum = baleenType.max,
                minimum = baleenType.min
            ) to objectContext
            is OccurrencesType -> {
                val (subSchema, subContext) = getJsonSchema(baleenType.memberType, objectContext, withAdditionalAttributes)
                ArraySchema(items = subSchema) to subContext
            }
            is StringType -> StringSchema(
                    maxLength = baleenType.max,
                    minLength = baleenType.min
            ) to objectContext
            is StringConstantType -> StringSchema(
                    enum = listOf(baleenType.constant)
            ) to objectContext
            is TimestampMillisType -> DateTimeSchema() to objectContext
            is UnionType -> {
                val l = baleenType.types.map { getJsonSchema(it, objectContext, withAdditionalAttributes) }
                val subSchemas = l.map { it.first }.distinct()
                val subContext = l.map { it.second }.reduce { x, y -> x + y }
                if (subSchemas.size == 1) {
                    subSchemas.first() to subContext
                } else {
                    OneOf(subSchemas) to subContext
                }
            }
            else -> throw Exception("Unknown type: " + baleenType::class.simpleName)
        }
    }

    fun encode(dataDescription: DataDescription, withAdditionalAttributes: Boolean = false): RootJsonSchema {
        val id = if (dataDescription.nameSpace.isNotBlank()) "${dataDescription.nameSpace}.${dataDescription.name}" else dataDescription.name
        val ref = "#/definitions/record:$id"
        val schema = "http://json-schema.org/draft-04/schema"

        val results = getJsonSchema(dataDescription, emptyMap(), withAdditionalAttributes)

        return RootJsonSchema(id, results.second.toSortedMap(), ref, schema)
    }

    fun encodeAsSelfDescribing(dataDescription: DataDescription, version: String, withAdditionalAttributes: Boolean = false): RootJsonSchema {
        val selfDescribingSchema = "http://iglucentral.com/schemas/com.snowplowananalytics.self-desc/schema/jsonschema/1-0-0"

        val rootSchema = encode(dataDescription, withAdditionalAttributes)

        return RootJsonSchema(
                rootSchema.id,
                rootSchema.definitions,
                rootSchema.`$ref`,
                selfDescribingSchema,
                SelfDescribing(
                        dataDescription.nameSpace,
                        dataDescription.name,
                        version
                ))
    }

    fun RootJsonSchema.writeTo(directory: File, prettyPrint: Boolean = false): File {
        val id = this.id
        val schemaFile = if (id != null) {
            val name = id.replace(".", "/")
            File(directory, "$name.schema.json")
        } else {
            File(directory, "UNNAMED.schema.json")
        }

        schemaFile.parentFile.mkdirs()

        if (prettyPrint) {
            ObjectMapper()
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(schemaFile, this)
        } else {
            ObjectMapper()
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .writeValue(schemaFile, this)
        }
        return directory
    }

    fun RootJsonSchema.writeTo(directory: Path, prettyPrint: Boolean = false): Path {
        return this.writeTo(directory.toFile(), prettyPrint).toPath()
    }

    fun RootJsonSchema.writeTo(out: Appendable, prettyPrint: Boolean = false): Appendable {
        if (prettyPrint) {
            out.append(ObjectMapper()
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(this))
        } else {
            out.append(ObjectMapper()
                    .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                    .writeValueAsString(this))
        }
        return out
    }
}
