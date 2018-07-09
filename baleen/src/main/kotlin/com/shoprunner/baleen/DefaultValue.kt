package com.shoprunner.baleen

/**
 * Container for the default value of an attribute if it exists. If it exists, then NoDefault class is used,
 * otherwise use the Default class. Null defaults are not the same as no default. Use Default(null).
 */
interface DefaultValue

/**
 * Container for default values, including null.
 */
data class Default(val value: Any?) : DefaultValue

/**
 * Container for no defaults
 */
object NoDefault : DefaultValue