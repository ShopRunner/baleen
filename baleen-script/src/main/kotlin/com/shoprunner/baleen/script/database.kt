package com.shoprunner.baleen.script

import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.jdbc.DatabaseUtil
import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties

fun getConnection(
    url: String,
    user: String,
    password: String? = null,
    dataSourceProperties: Map<String, String> = emptyMap()
): Connection {
    val connectionProps = Properties()
    connectionProps.put("user", user)
    if (password != null) {
        connectionProps.put("password", password)
    }
    connectionProps.putAll(dataSourceProperties)
    return DriverManager.getConnection(url, connectionProps)
}

fun table(
    connection: Connection,
    table: String,
    vararg tags: Pair<String, String>
): DataAccess = { dataDescription ->
    DatabaseUtil.validateTable(
        table = table,
        connection = connection,
        baleenType = dataDescription,
        dataTrace = dataTrace().tag(
            "format" to "sql",
            "queryType" to "full",
            "table" to table,
            *tags
        ),
    ).results
}

private fun sampleSql(url: String, table: String, samplePercent: Double): String =
    when {
        url.startsWith("jdbc:mysql", ignoreCase = true) ->
            "SELECT * FROM $table WHERE rand() < $samplePercent"
        url.startsWith("jdbc:h2:mem") ->
            "SELECT * FROM $table WHERE rand() < $samplePercent"
        else ->
            "SELECT * FROM $table TABLESAMPLE BERNOULLI(${(samplePercent * 100).toInt()})"
    }

fun sample(
    connection: Connection,
    table: String,
    samplePercent: Double,
    queryName: String = table,
    vararg tags: Pair<String, String>
): DataAccess = { dataDescription ->
    DatabaseUtil.validateQuery(
        queryStr = sampleSql(connection.metaData.url, table, samplePercent),
        connection = connection,
        baleenType = dataDescription,
        dataTrace = dataTrace().tag(
            "format" to "sql",
            "queryType" to "sample",
            "table" to table,
            "queryName" to queryName,
            *tags
        )
    ).results
}

fun query(
    connection: Connection,
    queryName: String,
    queryStr: String,
    vararg tags: Pair<String, String>
): DataAccess = { dataDescription ->
    DatabaseUtil.validateQuery(
        queryStr = queryStr,
        connection = connection,
        baleenType = dataDescription,
        dataTrace = dataTrace().tag(
            "format" to "sql",
            "queryType" to "query",
            "queryName" to queryName,
            *tags
        )
    ).results
}
