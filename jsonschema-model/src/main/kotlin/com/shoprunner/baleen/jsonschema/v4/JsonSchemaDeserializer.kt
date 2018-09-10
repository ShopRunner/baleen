package com.shoprunner.baleen.jsonschema.v4

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer

class JsonSchemaDeserializer : StdDeserializer<JsonSchema>(JsonSchema::class.java) {
    override fun deserialize(p: JsonParser?, ctxt: DeserializationContext?): JsonSchema {
        if (p == null) {
            throw NullPointerException("This should not happen!")
        }
        val mapper = p.codec as ObjectMapper
        val tree = mapper.readTree<JsonNode>(p)

        val type = when {
            tree.has("type") -> tree["type"].asText()
            tree.has("allOf") -> "allOf"
            tree.has("anyOf") -> "anyOf"
            tree.has("oneOf") -> "oneOf"
            tree.has("not") -> "not"
            tree.has("${'$'}ref") -> "ref"
            else -> null
        }

        return when (type) {
            "allOf" -> mapper.treeToValue(tree, AllOf::class.java)
            "anyOf" -> mapper.treeToValue(tree, AnyOf::class.java)
            "array" -> mapper.treeToValue(tree, ArraySchema::class.java)
            "boolean" -> mapper.treeToValue(tree, BooleanSchema::class.java)
            "integer" -> mapper.treeToValue(tree, IntegerSchema::class.java)
            "not" -> mapper.treeToValue(tree, Not::class.java)
            "number" -> mapper.treeToValue(tree, NumberSchema::class.java)
            "null" -> mapper.treeToValue(tree, NullSchema::class.java)
            "object" -> {
                val additionalProperties = tree["additionalProperties"]
                if (additionalProperties == null || additionalProperties.isBoolean || additionalProperties.isNull) {
                    mapper.treeToValue(tree, ObjectSchema::class.java)
                } else {
                    mapper.treeToValue(tree, MapSchema::class.java)
                }
            }
            "oneOf" -> mapper.treeToValue(tree, OneOf::class.java)
            "string" -> mapper.treeToValue(tree, StringSchema::class.java)
            "ref" -> mapper.treeToValue(tree, ObjectReference::class.java)
            else -> throw Exception("$type type not supported")
        }.apply {
            if (tree.has("default") && tree["default"].isNull) {
                default = Null
            }
        }
    }
}