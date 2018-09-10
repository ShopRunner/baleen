package com.shoprunner.baleen.jsonschema.v4

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class AllOf(
    @JsonDeserialize(contentUsing = JsonSchemaDeserializer::class)
    val allOf: List<JsonSchema>
) : JsonSchema()