package com.shoprunner.baleen

interface Data {
    fun containsKey(key: String): Boolean

    // returns null if value does not exist
    operator fun get(key: String): Any?

    val keys: Set<String>
}
