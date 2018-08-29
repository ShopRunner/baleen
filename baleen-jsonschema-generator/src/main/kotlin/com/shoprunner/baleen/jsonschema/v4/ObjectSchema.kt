package com.shoprunner.baleen.jsonschema.v4

data class ObjectSchema(
        val required: List<String>?,
        val additionalProperties: Boolean?,
        val properties: Map<String, JsonSchema>

) : JsonSchema() {
    val type = JsonType.`object`
}