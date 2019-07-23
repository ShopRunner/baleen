package com.shoprunner.baleen.jsonschema.v4

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class ArraySchema(
    @JsonDeserialize(using = JsonSchemaDeserializer::class)
    val items: JsonSchema
) : JsonSchema() {
    var type = JsonType.array
}
