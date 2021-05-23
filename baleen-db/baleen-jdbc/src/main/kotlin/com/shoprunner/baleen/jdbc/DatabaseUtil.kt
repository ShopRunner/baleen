package com.shoprunner.baleen.jdbc

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.Context
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.Validation
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.datawrappers.HashData
import com.shoprunner.baleen.types.Tagger
import com.shoprunner.baleen.types.tag
import java.sql.Connection

object DatabaseUtil {

    @JvmStatic
    fun validateTable(
        table: String,
        connection: Connection,
        baleenType: BaleenType,
        dataTrace: DataTrace = dataTrace()
    ): Validation {
        val queryStr = "SELECT * FROM $table"
        return validateQuery(queryStr, connection, baleenType, dataTrace)
    }

    @JvmStatic
    fun validateTable(
        table: String,
        connection: Connection,
        dataDescriptionName: String = table,
        tags: Map<String, Tagger> = emptyMap(),
        dataTrace: DataTrace = dataTrace(),
        description: DataDescription.() -> Unit
    ): Validation {
        val queryStr = "SELECT * FROM $table"
        return validateQuery(dataDescriptionName, queryStr, connection, tags, dataTrace, description)
    }

    @JvmStatic
    fun validateQuery(
        queryStr: String,
        connection: Connection,
        baleenType: BaleenType,
        dataTrace: DataTrace = dataTrace()
    ): Validation {
        val results = doValidation(
            query = queryStr,
            connection = connection,
            baleenType = baleenType,
            dataTrace = dataTrace
        )
        return Validation(Context(HashData(mapOf("query" to queryStr)), dataTrace), results.asIterable())
    }

    @JvmStatic
    fun validateQuery(
        queryName: String,
        queryStr: String,
        connection: Connection,
        tags: Map<String, Tagger> = emptyMap(),
        dataTrace: DataTrace = dataTrace(),
        description: DataDescription.() -> Unit
    ): Validation {
        val results = doValidation(
            query = queryStr,
            connection = connection,
            baleenType = queryName.describeAs(description = description).tag(tags),
            dataTrace = dataTrace
        )
        return Validation(Context(HashData(mapOf("query" to queryStr)), dataTrace), results.asIterable())
    }

    internal fun doValidation(
        query: String,
        connection: Connection,
        baleenType: BaleenType,
        dataTrace: DataTrace
    ): Sequence<ValidationResult> {
        return sequence {
            connection.prepareStatement(query).use { stmt ->
                stmt.executeQuery().use { rs ->
                    while (rs.next()) {
                        val rowNumber = rs.row
                        val data = DatabaseRow(rs, rowNumber)
                        val results = baleenType.validate(
                            dataTrace.tag("row" to rowNumber.toString()),
                            data
                        )
                        yield(results.toList())
                    }
                }
            }
        }
            .flatten()
    }

    // We compile to Java 6, which doesn't have this `use` feature for some reason.
    private inline fun <T : AutoCloseable, R> T.use(body: (T) -> R): R =
        try {
            body(this)
        } finally {
            this.close()
        }
}
