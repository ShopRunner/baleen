package com.shoprunner.baleen.jdbc

import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.DataValue
import java.sql.ResultSet

/**
 * [Data] representation of a row in a [ResultSet]. Look ups are case-insensitive because
 * SQL is case-insensitive.  Various implementations of JDBC drivers return column names in different ways, with
 * some returning all caps and some lower-case.
 */
class DatabaseRow(
    rs: ResultSet,
    private val rowNumber: Int
) : Data {

    private val columnIdxMapping = (1..rs.metaData.columnCount).map { idx ->
        rs.metaData.getColumnName(idx).toUpperCase() to idx
    }.toMap()

    private val data = columnIdxMapping.map { (key, idx) -> key to rs.getObject(idx) }.toMap()

    override val keys: Set<String> = columnIdxMapping.keys

    override fun containsKey(key: String): Boolean = keys.contains(key.toUpperCase())

    override fun get(key: String): Any? {
        return data[key.toUpperCase()]
    }

    override fun attributeDataValue(key: String, dataTrace: DataTrace): DataValue {
        val dbColumn = key.toUpperCase()
        val tags = mapOf("row" to rowNumber.toString()) +
            if (containsKey(dbColumn)) {
                mapOf("column" to dbColumn, "columnIdx" to columnIdxMapping[dbColumn].toString())
            } else {
                emptyMap()
            }
        return DataValue(get(key), (dataTrace + "column \"$dbColumn\"").tag(tags))
    }

    override fun toString(): String {
        val mapString = keys.map { key -> "$key=${get(key)}" }.joinToString()
        return "DatabaseRow(rowNumber=$rowNumber, $mapString)"
    }
}
