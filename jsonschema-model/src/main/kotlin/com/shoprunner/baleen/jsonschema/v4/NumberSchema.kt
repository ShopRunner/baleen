package com.shoprunner.baleen.jsonschema.v4

data class NumberSchema(
    val maximum: Double? = null,
    val minimum: Double? = null
) : JsonSchema() {
    val type = JsonType.number
}
