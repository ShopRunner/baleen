package com.shoprunner.baleen

/**
 * A wrapper around the data value that contains extra context information as needed.
 */
data class DataValue<T>(
    val value: T,
    val lineNumber: Int? = null,
    val columnNumber: Int? = null
)