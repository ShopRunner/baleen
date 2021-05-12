package com.shoprunner.baleen.script

import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.jdbc.DatabaseUtil
import com.shoprunner.baleen.types.Tagger
import java.io.Closeable
import java.sql.Connection
import java.sql.DriverManager
import java.util.*

class DatabaseValidationWorker(val credentials: Credentials) : Closeable {
    val results: MutableList<ValidationResult> = mutableListOf()

    private val connection: Connection by lazy {
        val connectionProps = Properties()
        connectionProps.put("user", credentials.user)
        if (credentials.password != null) {
            connectionProps.put("password", credentials.password)
        } else if (credentials.url.startsWith("jdbc:snowflake")) {
            connectionProps.put("authenticator", "externalBrowser")
        }

        connectionProps.putAll(credentials.dataSourceProperties)
        DriverManager.getConnection(credentials.url, connectionProps)
    }

    fun table(
        table: String,
        dataDescriptionName: String = table,
        tags: Map<String, Tagger> = emptyMap(),
        description: DataDescription.() -> Unit
    ) {
        results += DatabaseUtil.validateTable(
            table = table,
            connection = connection,
            dataDescriptionName = dataDescriptionName,
            tags = tags,
            description = description,
        ).results
    }

    fun sample(
        table: String,
        numRows: Int,
        queryName: String = table,
        tags: Map<String, Tagger> = emptyMap(),
        description: DataDescription.() -> Unit
    ) {
        results += DatabaseUtil.validateQuery(
            queryName = queryName,
            queryStr = "SELECT * FROM ${table} SAMPLE ($numRows ROWS)",
            connection = connection,
            tags = tags,
            description = description,
        ).results
    }

    fun query(
        queryName: String,
        queryStr: String,
        tags: Map<String, Tagger> = emptyMap(),
        description: DataDescription.() -> Unit
    ) {
        results += DatabaseUtil.validateQuery(
            queryName = queryName,
            queryStr = queryStr,
            connection = connection,
            tags = tags,
            description = description,
        ).results
    }

    override fun close() {
        connection.close()
    }
}