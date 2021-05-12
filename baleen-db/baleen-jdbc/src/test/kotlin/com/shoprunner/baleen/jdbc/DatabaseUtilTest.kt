package com.shoprunner.baleen.jdbc

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.ValidationSuccess
import com.shoprunner.baleen.ValidationWarning
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.types.NumericType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.withAttributeValue
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.sql.Connection
import java.sql.DriverManager
import java.util.Properties

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DatabaseUtilTest {
    private val connection: Connection by lazy {
        val connectionProps = Properties()
        connectionProps["user"] = "test"
        DriverManager.getConnection("jdbc:h2:mem:test", connectionProps)
    }

    private val productDescription = "Product".describeAs {
        "id".type(NumericType())
        "name".type(StringType(max = 50))
        "type".type(StringType(max = 50))
    }

    @BeforeAll
    fun setupDatabase() {
        connection.prepareStatement(
            """
            CREATE TABLE product (
                id NUMBER PRIMARY KEY,
                name VARCHAR(50),
                type VARCHAR(50)
            )
            """.trimIndent()
        ).use { it.execute() }

        connection.prepareStatement(
            """
            INSERT INTO product (id, name, type)
            VALUES 
                (1, 'jeans', 'pants'),
                (2, 'loafers', 'shoes'),
                (3, 'button-down', 'shirt'),
                (4, 'ballcap', 'hat')
            """.trimIndent()
        ).use { it.execute() }
    }

    @AfterAll
    fun closeConnection() {
        connection.close()
    }

    @Test
    fun `test table validates against description`() {
        val validation = DatabaseUtil.validateTable("product", connection, productDescription)

        assertThat(validation.isValid()).isTrue
        assertThat(validation.results.toList()).allSatisfy { allSatisfyTags(it, "row") }
    }

    @Test
    fun `test table validates against inline description`() {
        val validation = DatabaseUtil.validateTable("product", connection) {
            "id".type(NumericType())
            "name".type(StringType(max = 50))
            "type".type(StringType(max = 50))
        }

        assertThat(validation.isValid()).isTrue
        assertThat(validation.results.toList()).allSatisfy { allSatisfyTags(it, "row") }
    }

    @Test
    fun `test table with inline description adds custom tags`() {
        val validation = DatabaseUtil.validateTable(
            "product", connection,
            tags = mapOf("id" to withAttributeValue("id"))
        ) {
            "id".type(NumericType())
            "name".type(StringType(max = 50))
            "type".type(StringType(max = 50))
        }

        assertThat(validation.isValid()).isTrue
        assertThat(validation.results.toList()).allSatisfy { allSatisfyTags(it, "row", "id") }
    }

    @Test
    fun `test query validates against description`() {
        val validation = DatabaseUtil.validateQuery("SELECT * FROM product WHERE type = 'shoes'", connection, productDescription)

        assertThat(validation.isValid())
        assertThat(validation.results.toList()).allSatisfy { allSatisfyTags(it, "row") }
    }

    @Test
    fun `test query validates against inline description`() {
        val validation = DatabaseUtil.validateQuery("shoes", "SELECT * FROM product WHERE type = 'shoes'", connection) {
            "id".type(NumericType())
            "name".type(StringType(max = 50))
            "type".type(StringType(max = 50))
        }

        assertThat(validation.isValid())
        assertThat(validation.results.toList()).allSatisfy { allSatisfyTags(it, "row") }
    }

    @Test
    fun `test query with inline description add custom tags`() {
        val validation = DatabaseUtil.validateQuery(
            "shoes", "SELECT * FROM product WHERE type = 'shoes'", connection,
            tags = mapOf("id" to withAttributeValue("id"))
        ) {
            "id".type(NumericType())
            "name".type(StringType(max = 50))
            "type".type(StringType(max = 50))
        }

        assertThat(validation.isValid())
        assertThat(validation.results.toList()).allSatisfy { allSatisfyTags(it, "row", "id") }
    }

    private fun allSatisfyTags(validationResult: ValidationResult, vararg tags: String) {
        val dt = when (validationResult) {
            is ValidationSuccess -> validationResult.dataTrace
            is ValidationInfo -> validationResult.dataTrace
            is ValidationError -> validationResult.dataTrace
            is ValidationWarning -> validationResult.dataTrace
            else -> dataTrace()
        }
        assertThat(dt.tags).containsKeys(*tags)
    }

    // We compile to Java 6, which doesn't have this `use` feature for some reason.
    private inline fun <T : AutoCloseable, R> T.use(body: (T) -> R): R =
        try {
            body(this)
        } finally {
            this.close()
        }
}
