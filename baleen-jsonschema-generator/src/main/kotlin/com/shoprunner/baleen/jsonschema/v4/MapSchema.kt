package com.shoprunner.baleen.jsonschema.v4

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class MapSchema(
    @JsonDeserialize(using = JsonSchemaDeserializer::class)
    val additionalProperties: JsonSchema
) : JsonSchema() {
    val type = JsonType.`object`
}