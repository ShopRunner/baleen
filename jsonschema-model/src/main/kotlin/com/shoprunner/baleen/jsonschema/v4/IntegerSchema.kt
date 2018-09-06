package com.shoprunner.baleen.jsonschema.v4

data class IntegerSchema(
    val maximum: Long? = null,
    val minimum: Long? = null
) : JsonSchema() {
    val type = JsonType.integer
}