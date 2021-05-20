#!/usr/bin/env kotlin

// Uncomment to use mavenLocal. Run `./gradlew publishToMavenLocal -Pskip.signing=true`
//@file:Repository("file:///Users/username/.m2/repository/")
@file:DependsOn("com.shoprunner:baleen-script:1.14.0")
@file:DependsOn("net.snowflake:snowflake-jdbc:3.13.1")

import com.shoprunner.baleen.*
import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.script.*
import com.shoprunner.baleen.types.*

baleen("summary", Output.console, Output.text, Output.html, Output.csv) {

    // CSV validation is currently is broken
//    csv("./example.csv") {
//        "id".type(IntegerType(), required = true)
//        "firstName".type(StringType(0, 1), required = true)
//        "lastName".type(StringType(0, 32), required = true)
//    }

    json("./example.json") {
        "id".type(IntegerType(), required = true)
        "firstName".type(StringType(0, 1), required = true)
        "lastName".type(StringType(0, 32), required = true)
    }

    xml("./example.xml") {
        "example".type("example".describeAs {
            "id".type(StringCoercibleToLong(LongType()), required = true)
            "firstName".type(StringType(0, 1), required = true)
            "lastName".type(StringType(0, 32), required = true)
        }, required = true)
    }

    database {
        credentials {
            url = "jdbc:snowflake://xp71857.us-east-1.snowflakecomputing.com/?warehouse=SHOPRUNNER&db=SHOPRUNNER"
            user = "kdallmeyer"
            addDataSourceProperty("authenticator", "externalBrowser")
        }

        // Does validation on all rows of a table.
        table(
            "engineer.example", "table example",
            tags = mapOf("ID" to withAttributeValue("ID"))
        ) {

            "ID".type(IntegerType(), required = true)
            "FIRST_NAME".type(StringType(0, 1), required = true)
            "LAST_NAME".type(StringType(0, 32), required = true)
        }

        // Does validation on a sample of rows of a table.
        sample(
            "engineer.example", 0.10, "sampling example",
            tags = mapOf("ID" to withAttributeValue("ID"))
        ) {

            "ID".type(IntegerType(), required = true)
            "FIRST_NAME".type(StringType(0, 1), required = true)
            "LAST_NAME".type(StringType(0, 32), required = true)
        }

        // Does validation on the results of a query
        query(
            "query example", "SELECT id, first_name, last_name FROM engineer.example WHERE last_name = 'Smith'",
            tags = mapOf("ID" to withAttributeValue("ID"))
        ) {

            "ID".type(IntegerType(), required = true)
            "FIRST_NAME".type(StringType(0, 1), required = true)
            "LAST_NAME".type(StringConstantType("Smith"), required = true)
        }
    }

    http {
        get("https://reqres.in/api/users/2", "applicatin/json") { body ->
            json("http example", body!!.byteInputStream()) {
                "data".type("data".describeAs{
                    "id".type(IntegerType(), required = true)
                    "first_name".type(StringType(0, 1), required = true)
                    "last_name".type(StringType(0, 32), required = true)
                })
            }
        }
    }
}