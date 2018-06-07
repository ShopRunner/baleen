package com.shoprunner.baleen.datawrappers

import com.shoprunner.baleen.Data

class HashData(private val hash: Map<String, Any?>) : Data {
    override fun containsKey(key: String): Boolean = hash.contains(key)

    override fun get(key: String): Any? = hash[key]

    override val keys: Set<String> = hash.keys

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Data) return false

        return (keys == other.keys) && keys.all { get(it) == other.get(it) }
    }

    override fun toString(): String {
        return "HashData{ $hash }"
    }
}