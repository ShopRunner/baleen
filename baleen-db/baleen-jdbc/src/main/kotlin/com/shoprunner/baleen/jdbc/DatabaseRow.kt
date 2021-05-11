package com.shoprunner.baleen.jdbc

import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.DataValue
import java.sql.ResultSet

class DatabaseRow(
    private val rs: ResultSet,
    private val rowNumber: Int
) : Data {

    override val keys: Set<String> = (1..rs.metaData.columnCount).map { i ->
        rs.metaData.getColumnName(i).toUpperCase()
    }.toSet()

    override fun containsKey(key: String): Boolean = keys.contains(key.toUpperCase())

    override fun get(key: String): Any? {
        return rs.getObject(key.toUpperCase())
    }

    override fun attributeDataValue(key: String, dataTrace: DataTrace): DataValue {
        val tags = mapOf("row" to rowNumber.toString()) +
                if(containsKey(key)) mapOf("column" to key.toUpperCase())
                else emptyMap()
        return super.attributeDataValue(key, dataTrace.tag(tags))
    }

    override fun toString(): String {
        val mapString = keys.map { key -> key to get(key) }.joinToString()
        return "DatabaseRow(rowNumber=$rowNumber, $mapString)"
    }
}
