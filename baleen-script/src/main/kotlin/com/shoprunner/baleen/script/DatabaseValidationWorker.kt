package com.shoprunner.baleen.script

import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.createSummary
import com.shoprunner.baleen.groupByTag
import com.shoprunner.baleen.jdbc.DatabaseUtil
import com.shoprunner.baleen.types.Tagger
import com.shoprunner.baleen.types.withConstantValue
import java.io.Closeable
import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties

class DatabaseValidationWorker : Closeable {
    val results: MutableList<ValidationResult> = mutableListOf()

    private var credentials: Credentials? = null

    private val connection: Connection by lazy {
        val creds = credentials
        if (creds == null) {
            throw Exception("Database credentials not set. Please use credentials { } block")
        } else {
            val connectionProps = Properties()
            connectionProps.put("user", creds.user)
            if (creds.password != null) {
                connectionProps.put("password", creds.password)
            }
            connectionProps.putAll(creds.dataSourceProperties)
            DriverManager.getConnection(creds.url, connectionProps)
        }
    }

    fun credentials(body: CredentialsBuilder.() -> Unit) {
        credentials = CredentialsBuilder().apply(body).build()
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
            tags = tags + ("table" to withConstantValue(table)),
            description = description,
        ).results.createSummary(groupBy = groupByTag("table"))
    }

    private fun sampleSql(table: String, samplePercent: Double): String =
        credentials.let {
            when {
                it == null ->
                    throw Exception("Database credentials not set. Please use credentials { } block")
                it.url.startsWith("jdbc:mysql", ignoreCase = true) ->
                    "SELECT * FROM $table rand() < $samplePercent"
                else ->
                    "SELECT * FROM $table TABLESAMPLE BERNOULLI(${samplePercent * 100})"
            }
        }

    fun sample(
        table: String,
        samplePercent: Double,
        queryName: String = table,
        tags: Map<String, Tagger> = emptyMap(),
        description: DataDescription.() -> Unit
    ) {
        results += DatabaseUtil.validateQuery(
            queryName = queryName,
            queryStr = sampleSql(table, samplePercent),
            connection = connection,
            tags = tags + listOf(
                "table" to withConstantValue(table),
                "queryName" to withConstantValue(queryName)
            ),
            description = description,
        ).results.createSummary(groupBy = groupByTag("table", "queryName"))
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
            tags = tags + ("queryName" to withConstantValue(queryName)),
            description = description,
        ).results.createSummary(groupBy = groupByTag("queryName"))
    }

    override fun close() {
        connection.close()
    }
}
