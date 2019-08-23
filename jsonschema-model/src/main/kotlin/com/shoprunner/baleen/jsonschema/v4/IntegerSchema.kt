package com.shoprunner.baleen.jsonschema.v4

import java.math.BigInteger

data class IntegerSchema(
    val maximum: BigInteger? = null,
    val minimum: BigInteger? = null
) : JsonSchema() {
    val type = JsonType.integer
}
