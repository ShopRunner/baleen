package com.shoprunner.baleen.jsonschema.v4

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class OneOf(
    @JsonDeserialize(contentUsing = JsonSchemaDeserializer::class)
    val oneOf: List<JsonSchema>
) : JsonSchema()
