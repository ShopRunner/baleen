package com.shoprunner.baleen.jsonschema.v4

import com.squareup.kotlinpoet.CodeBlock

interface BaleenOverride : (JsonSchema) -> CodeBlock {
    fun matches(j: JsonSchema): Boolean
}

inline fun <reified J : JsonSchema> ((J) -> CodeBlock).asBaleenOverride(): BaleenOverride {
    val receiver = this
    return object : BaleenOverride {
        override fun matches(j: JsonSchema): Boolean {
            return j is J
        }

        override fun invoke(j: JsonSchema): CodeBlock {
            return receiver(j as J)
        }
    }
}
