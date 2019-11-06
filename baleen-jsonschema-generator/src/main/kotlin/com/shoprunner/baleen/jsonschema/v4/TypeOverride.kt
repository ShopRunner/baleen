package com.shoprunner.baleen.jsonschema.v4

import com.shoprunner.baleen.BaleenType

/**
 * A representation of how a BaleenType can be overridden.
 */
data class TypeOverride(
    /**
     * A function that given a BaleenType, returns true if should override that type
     */
    val isOverridable: (BaleenType) -> Boolean,

    /**
     * A function that given a BaleenType, returns the JsonSchema to override with.
     */
    val override: (BaleenType) -> JsonSchema
)
