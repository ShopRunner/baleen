package com.shoprunner.baleen.jsonschema.v4

data class StringSchema(
        val maxLength: Int? = null,
        val minLength: Int? = null,
        val enum: List<String>? = null,
        val format: StringFormats? = null
): JsonSchema() {
    val type = JsonType.string
}