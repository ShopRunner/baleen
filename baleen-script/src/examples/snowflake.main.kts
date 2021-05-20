#!/usr/bin/env kotlin

// Uncomment to use mavenLocal. Run `./gradlew publishToMavenLocal -Pskip.signing=true`
//@file:Repository("file:///Users/username/.m2/repository/")
@file:DependsOn("com.shoprunner:baleen-script:1.14.0")
@file:DependsOn("net.snowflake:snowflake-jdbc:3.13.1")

import com.shoprunner.baleen.script.*
import com.shoprunner.baleen.types.*

baleen {
    database {
        credentials {
            url = "jdbc:snowflake://xp71857.us-east-1.snowflakecomputing.com/?warehouse=SHOPRUNNER&db=SHOPRUNNER"
            user = "kdallmeyer" //System.getenv("DATABASE_USER")
            addDataSourceProperty("authenticator", "externalBrowser")
        }

        // Does validation on all rows of a table.
        table("engineer.example", "table example", tags = mapOf("ID" to withAttributeValue("ID"))) {
            tag("rowId", withAttributeValue("ID"))

            "ID".type(IntegerType())
            "FIRST_NAME".type(StringType(0, 32))
            "LAST_NAME".type(StringType(0, 32))
        }

        // Does validation on a sample of rows of a table.
        sample("engineer.example", 1.0, "sampling example", tags = mapOf("ID" to withAttributeValue("ID"))) {
            tag("rowId", withAttributeValue("ID"))

            "ID".type(IntegerType())
            "FIRST_NAME".type(StringType(0, 32))
            "LAST_NAME".type(StringType(0, 32))
        }

        // Does validation on the results of a query
        query(
            "query example", "SELECT id, first_name, last_name FROM engineer.example WHERE last_name = 'Smith'",
            tags = mapOf("ID" to withAttributeValue("ID"))
        ) {
            "ID".type(IntegerType())
            "FIRST_NAME".type(StringType(0, 32))
            "LAST_NAME".type(StringConstantType("Smith"))
        }

        // Example of just writing tests without defining columns
        table("engineer.example", "row test only", tags = mapOf("ID" to withAttributeValue("ID"))) {
            test("first name should not be same as last name") { data ->
                assertNotEquals(
                    "first name != last name",
                    data.getAsStringOrNull("FIRST_NAME"),
                    data.getAsStringOrNull("LAST_NAME")
                )
            }

            test("ID should always be odd") { data ->
                assertTrue("ID is Odd", data.getAsLongOrNull("ID")?.mod( 2L) != 0L, data.getAsLongOrNull("ID"))
            }

            test("names should not be all caps") { data ->
                assertNotEquals(
                    "first name is not all caps",
                    data.getAsStringOrNull("FIRST_NAME")?.uppercase(),
                    data.getAsStringOrNull("FIRST_NAME")
                )
                assertNotEquals(
                    "last name is not all caps",
                    data.getAsStringOrNull("LAST_NAME")?.uppercase(),
                    data.getAsStringOrNull("LAST_NAME")
                )
            }
        }
    }
}

