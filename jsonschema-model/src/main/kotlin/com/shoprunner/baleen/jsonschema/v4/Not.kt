package com.shoprunner.baleen.jsonschema.v4

import com.fasterxml.jackson.databind.annotation.JsonDeserialize

data class Not(
    @JsonDeserialize(using = JsonSchemaDeserializer::class)
    val not: JsonSchema
) : JsonSchema()