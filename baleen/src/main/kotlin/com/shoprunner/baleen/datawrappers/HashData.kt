package com.shoprunner.baleen.datawrappers

import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataValue

class HashData(private val hash: Map<String, DataValue<*>?>) : Data {
    override fun containsKey(key: String): Boolean = hash.contains(key)

    override fun get(key: String): Any? = hash[key]?.value

    override fun getDataValue(key: String): DataValue<*>? = hash[key]

    override val keys: Set<String> = hash.keys

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Data) return false

        return (keys == other.keys) && keys.all { get(it) == other.get(it) }
    }

    override fun hashCode(): Int {
        return hash.map { (k, v) -> Pair(k, v?.value) }.toMap().hashCode()
    }

    override fun toString(): String {
        return "HashData{ $hash }"
    }
}