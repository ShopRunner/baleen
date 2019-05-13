package com.shoprunner.baleen

interface Data {
    fun containsKey(key: String): Boolean

    // returns null if value does not exist
    operator fun get(key: String): Any?

    fun getDataValue(key: String): DataValue<*>?

    val keys: Set<String>
}
