package com.shoprunner.baleen.jsonschema.v4

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class ObjectSchema(
    val required: List<String>?,
    val additionalProperties: Boolean?,
    @JsonDeserialize(contentUsing = JsonSchemaDeserializer::class)
    val properties: Map<String, JsonSchema>,
    // Optional field for passing the id around
    @JsonIgnore
    val id: String? = null

) : JsonSchema() {
    val type = JsonType.`object`
}
