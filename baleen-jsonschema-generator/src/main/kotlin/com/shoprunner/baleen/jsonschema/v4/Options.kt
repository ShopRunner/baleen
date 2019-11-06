package com.shoprunner.baleen.jsonschema.v4

/**
 * Json Schema generating options to allow for more control
 * of the output.
 */
data class Options(
    /**
     * When set to true, additionalAttributes flag also set to true in json schema.
     */
    val additionalAttributes: Boolean = false,

    /**
     * When set to true, nullable fields are not marked as required even if
     * specified in Baleen schema.
     */
    val nullableFieldsNotRequired: Boolean = false,

    /**
     * A list of types to override
     */
    val typeOverrides: List<TypeOverride> = emptyList()
)
