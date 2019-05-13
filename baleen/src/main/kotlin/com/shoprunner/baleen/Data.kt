package com.shoprunner.baleen

data class DataValue(val value: Any?, val dataTrace: DataTrace)

interface Data {
    fun containsKey(key: String): Boolean

    // returns null if value does not exist
    operator fun get(key: String): Any?

    fun attributeDataValue(key: String, dataTrace: DataTrace): DataValue = DataValue(get(key), dataTrace + "attribute \"$key\"")

    val keys: Set<String>
}
