package com.shoprunner.baleen.script

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.groupByTag
import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.File
import java.nio.file.Files.createTempDirectory
import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DatabaseTest {
    private val connection: Connection by lazy {
        val connectionProps = Properties()
        connectionProps["user"] = "test"
        DriverManager.getConnection("jdbc:h2:mem:test;MODE=PostgreSQL", connectionProps)
    }

    val desc = "person".describeAs {
        "id".type(IntegerType(), required = true)
        "first_name".type(StringType(0, 32), required = true)
        "last_name".type(StringType(0, 32), required = true)
    }

    @BeforeAll
    fun setupDatabase() {
        connection.prepareStatement(
            """
            CREATE TABLE person (
                id INTEGER PRIMARY KEY,
                first_name VARCHAR(50),
                last_name VARCHAR(50)
            )
            """.trimIndent()
        ).use { it.execute() }

        connection.prepareStatement(
            """
            INSERT INTO person (id, first_name, last_name)
            VALUES 
                (0,'Jon','Smith'),
                (1,'Jane','Doe'),
                (2,'Billy','Idol'),
                (3,'Ace','Ventura')
            """.trimIndent()
        ).use { it.execute() }
    }

    @AfterAll
    fun closeConnection() {
        connection.close()
    }

    @Test
    fun `validate all rows in table`() {
        val outDir = createTempDirectory("table-test").toFile()

        validate(
            description = desc,
            data = table(connection, "person"),
            outputDir = outDir,
            groupBy = groupByTag("table"),
            outputs = arrayOf(Output.text),
        )

        val output = File(outDir, "summary.txt").readText()

        Assertions.assertThat(output).isEqualToIgnoringWhitespace(
            """
            ValidationSummary(
              dataTrace=DataTrace(stack=[], tags={table=person}),
              summary=Summary for table=person,
              numSuccesses=0,
              numInfos=12,
              numErrors=0,
              numWarnings=0,
              topErrorsAndWarnings=[
              ]
            )
            """.trimIndent()
        )
    }

    @Test
    fun `validate sample of a table`() {
        val outDir = createTempDirectory("sample-test").toFile()

        validate(
            description = desc,
            data = sample(connection, "person", 1.00),
            outputDir = outDir,
            groupBy = groupByTag("table", "queryName"),
            outputs = arrayOf(Output.text),
        )

        val output = File(outDir, "summary.txt").readText()

        Assertions.assertThat(output)
            .contains("ValidationSummary")
            .contains("tags={table=person, queryName=person}")
    }

    @Test
    fun `validate query against table`() {
        val outDir = createTempDirectory("query-test").toFile()

        validate(
            description = desc,
            data = query(connection, "person", "SELECT * FROM person WHERE id = 1"),
            outputDir = outDir,
            groupBy = groupByTag("table", "queryName"),
            outputs = arrayOf(Output.text),
        )

        val output = File(outDir, "summary.txt").readText()

        Assertions.assertThat(output).isEqualToIgnoringWhitespace(
            """
            ValidationSummary(
              dataTrace=DataTrace(stack=[], tags={queryName=person}),
              summary=Summary for queryName=person,
              numSuccesses=0,
              numInfos=3,
              numErrors=0,
              numWarnings=0,
              topErrorsAndWarnings=[
              ]
            )
            """.trimIndent()
        )
    }

    // We compile to Java 6, which doesn't have this `use` feature for some reason.
    private inline fun <T : AutoCloseable, R> T.use(body: (T) -> R): R =
        try {
            body(this)
        } finally {
            this.close()
        }
}
