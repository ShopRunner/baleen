package com.shoprunner.baleen.jsonschema.v4

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

/**
 * Write Null as null in Json
 */
class NullJsonSerializer : JsonSerializer<Null>() {
    override fun serialize(value: Null?, gen: JsonGenerator?, serializers: SerializerProvider?) {
        gen?.writeNull()
    }
}