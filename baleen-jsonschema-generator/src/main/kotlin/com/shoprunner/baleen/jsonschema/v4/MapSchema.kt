package com.shoprunner.baleen.jsonschema.v4

data class MapSchema(
        val additionalProperties: JsonSchema
) : JsonSchema() {
    val type = JsonType.`object`
}