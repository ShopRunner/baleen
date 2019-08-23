package com.shoprunner.baleen.jsonschema.v4

import java.math.BigDecimal

data class NumberSchema(
    val maximum: BigDecimal? = null,
    val minimum: BigDecimal? = null
) : JsonSchema() {
    val type = JsonType.number
}
