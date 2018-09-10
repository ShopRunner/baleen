package com.shoprunner.baleen.jsonschema.v4

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class AnyOf(
    @JsonDeserialize(contentUsing = JsonSchemaDeserializer::class)
    val anyOf: List<JsonSchema>
) : JsonSchema()