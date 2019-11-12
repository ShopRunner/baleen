package com.shoprunner.baleen.avro

/**
 * Options to encode Baleen DataDescription to Avro Schemas.
 */
data class Options(
    /**
     * When set to true, any nullable field will be set to null unless a default already exists.
     */
    val nullableFieldsAutomaticallyDefaultToNull: Boolean = false
)
