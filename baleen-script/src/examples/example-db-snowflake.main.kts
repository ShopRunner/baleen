#!/usr/bin/env kotlin

// Uncomment to use mavenLocal. Run `./gradlew publishToMavenLocal -Pskip.signing=true`
//@file:Repository("file:///Users/username/.m2/repository/")
@file:DependsOn("com.shoprunner:baleen-script:1.14.1")
@file:DependsOn("net.snowflake:snowflake-jdbc:3.13.1")

import com.shoprunner.baleen.*
import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.printer.*
import com.shoprunner.baleen.script.*
import com.shoprunner.baleen.types.*
import java.io.File

val desc = "person".describeAs {
    "id".type(IntegerType(), required = true)
    "first_name".type(StringType(0, 32), required = true)
    "last_name".type(StringType(0, 32), required = true)
}.tag("ID" to withAttributeValue("ID"))

val connection = getConnection(
    url = "jdbc:snowflake://account.us-east-1.snowflakecomputing.com/?warehouse=WAREHOUSE",
    user = System.getenv("DATABASE_USER"),
    dataSourceProperties = mapOf("authenticator" to "externalBrowser")
)

File("summary/example-db-table.html").writer().use {
    validate(
        description = desc,
        data = table(connection, "engineer.example"),
        groupBy = groupByTag("table"),
        printers = arrayOf(ConsolePrinter, HtmlPrinter(it)),
    )
}

File("summary/example-db-sample.html").writer().use {
    validate(
        description = desc,
        data = sample(connection, "engineer.example", 0.5),
        groupBy = groupByTag("table"),
        printers = arrayOf(ConsolePrinter, HtmlPrinter(it)),
    )
}

File("summary/example-db-query.html").writer().use {
    validate(
        description = desc,
        data = query(
            connection, "query example",
            "SELECT id, first_name, last_name FROM engineer.example WHERE last_name = 'Smith'"
        ),
        groupBy = groupByTag("table"),
        printers = arrayOf(ConsolePrinter, HtmlPrinter(it)),
    )
}
