package com.shoprunner.baleen.jsonschema.v4

import com.shoprunner.baleen.BaleenType

interface BaleenOverride : (BaleenType) -> JsonSchema {
    fun matches(b: BaleenType): Boolean
}

inline fun <reified B : BaleenType> ((B) -> JsonSchema).asBaleenOverride(): BaleenOverride {
    val receiver = this
    return object : BaleenOverride {
        override fun matches(b: BaleenType): Boolean {
            return b is B
        }

        override fun invoke(b: BaleenType): JsonSchema {
            return receiver(b as B)
        }
    }
}