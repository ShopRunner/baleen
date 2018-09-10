package com.shoprunner.baleen.jsonschema.v4

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class ObjectSchema(
    val required: List<String>?,
    val additionalProperties: Boolean?,
    @JsonDeserialize(contentUsing = JsonSchemaDeserializer::class)
    val properties: Map<String, JsonSchema>

) : JsonSchema() {
    val type = JsonType.`object`
}