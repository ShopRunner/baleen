package com.shoprunner.baleen.jsonschema.v4

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class UnionSchema(
    @JsonDeserialize(contentUsing = JsonSchemaDeserializer::class)
    val type: List<JsonType>
) : JsonSchema()