package com.shoprunner.baleen.jsonschema.v4

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.ObjectMapper
import com.shoprunner.baleen.DataDescription
import java.io.File
import java.nio.file.Path

object JsonSchemaGenerator {

    fun encode(dataDescription: DataDescription, withAdditionalAttributes: Boolean = false, baleenMapper: BaleenJsonSchemaMapper = DefaultBaleenJsonSchemaMapper): RootJsonSchema {
        val id = if (dataDescription.nameSpace.isNotBlank()) "${dataDescription.nameSpace}.${dataDescription.name}" else dataDescription.name
        val ref = "#/definitions/record:$id"
        val schema = "http://json-schema.org/draft-04/schema"

        val results = baleenMapper.getJsonSchema(dataDescription, emptyMap(), withAdditionalAttributes)

        return RootJsonSchema(id, results.second.toSortedMap(), ref, schema)
    }

    fun encodeAsSelfDescribing(dataDescription: DataDescription, version: String, namespace: String = dataDescription.nameSpace, withAdditionalAttributes: Boolean = false, baleenMapper: BaleenJsonSchemaMapper = DefaultBaleenJsonSchemaMapper): RootJsonSchema {
        val selfDescribingSchema = "http://iglucentral.com/schemas/com.snowplowananalytics.self-desc/schema/jsonschema/1-0-0"

        val rootSchema = encode(dataDescription, withAdditionalAttributes, baleenMapper)

        return RootJsonSchema(
                rootSchema.id,
                rootSchema.definitions,
                rootSchema.`$ref`,
                selfDescribingSchema,
                SelfDescribing(
                        namespace,
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
