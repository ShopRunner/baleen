package com.shoprunner.baleen.jsonschema.v4

data class ArraySchema(
        val items: JsonSchema
) : JsonSchema() {
    val type = JsonType.array
}