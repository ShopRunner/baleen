package com.shoprunner.baleen.json

import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.DataValue

internal sealed class JsonData(val line: Int?, val column: Int?)

internal class JsonDataLeaf(val value: Any?, line: Int?, column: Int?) : JsonData(line, column) {
    override fun toString(): String {
        return "JsonDataLeaf(value=$value, line=$line, column=$column)"
    }
}

internal class JsonDataNode(line: Int? = null, column: Int? = null) : Data, JsonData(line, column) {
    internal val hash = mutableMapOf<String, JsonData>()

    override fun containsKey(key: String): Boolean = hash.contains(key)

    override fun get(key: String): Any? =
        when (val child = hash[key]) {
            is JsonDataLeaf -> child.value
            is JsonDataNode -> child
            null -> null
        }

    override val keys: Set<String> = hash.keys
    override fun attributeDataValue(key: String, dataTrace: DataTrace): DataValue {
        return DataValue(
            value = get(key),
            dataTrace = attributeDataTrace(key, dataTrace)
        )
    }

    private fun attributeDataTrace(key: String, dataTrace: DataTrace): DataTrace {
        val line = hash[key]?.line
        val column = hash[key]?.column
        return (dataTrace + "attribute \"$key\"")
            .let { if (line != null) it.tag("line", line.toString()) else it }
            .let { if (column != null) it.tag("column", column.toString()) else it }
    }

    override fun toString(): String {
        return "JsonDataNode{ $hash, line = $line, column = $column }"
    }

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other !is Data) return false

        return (keys == other.keys) && keys.all { get(it) == other.get(it) }
    }

    override fun hashCode(): Int {
        return hash.hashCode()
    }
}
