package com.shoprunner.baleen.xml

import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.DataValue

internal sealed class XmlData( val line: Int?, val column: Int?)

internal class XmlDataLeaf(val value: Any?, line: Int?, column: Int?) : XmlData(line, column) {
    override fun toString(): String {
        return "XmlDataLeaf(value=$value, line=$line, column=$column)"
    }
}

internal class XmlDataNode(line: Int? = null, column: Int? = null) : Data, XmlData(line, column) {
    internal val hash = mutableMapOf<String, XmlData>()

    override fun containsKey(key: String): Boolean = hash.contains(key)

    override fun get(key: String): Any? =
        when (val child = hash[key]) {
            is XmlDataLeaf -> child.value
            is XmlDataNode -> child
            null -> null
        }

    override val keys: Set<String> = hash.keys
    override fun attributeDataValue(key: String, dataTrace: DataTrace) : DataValue {
        return DataValue(
            value = get(key),
            dataTrace = attributeDataTrace(key, dataTrace)
        )
    }

    private fun attributeDataTrace(key: String, dataTrace: DataTrace) : DataTrace {
        val line = hash[key]?.line
        val column = hash[key]?.column
        return (dataTrace + "attribute \"$key\"")
            .let { if (line != null) it.tag("line", line.toString()) else it }
            .let { if (column != null) it.tag("column", column.toString()) else it }
    }

    override fun toString(): String {
        return "XmlDataNode{ $hash, line = $line, column = $column }"
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