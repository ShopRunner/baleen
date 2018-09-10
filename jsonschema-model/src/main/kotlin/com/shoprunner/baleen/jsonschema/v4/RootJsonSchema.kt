package com.shoprunner.baleen.jsonschema.v4

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

class RootJsonSchema(
    val id: String?,
    val definitions: Map<String, ObjectSchema>?,
    val `$ref`: String?,
    val `$schema`: String,
    val self: SelfDescribing? = null,

    // Also is a regular schema
    val required: List<String>? = null,
    val additionalProperties: Boolean? = null,
    @JsonDeserialize(contentUsing = JsonSchemaDeserializer::class)
    val properties: Map<String, JsonSchema>? = null,
    val type: JsonType? = null
) : JsonSchema()