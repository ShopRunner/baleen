#!/usr/bin/env kotlin

// Uncomment to use mavenLocal. Run `./gradlew publishToMavenLocal -Pskip.signing=true`
//@file:Repository("file:///Users/username/.m2/repository/")
@file:DependsOn("com.shoprunner:baleen-script:1.14.1")

import com.shoprunner.baleen.*
import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.printer.*
import com.shoprunner.baleen.script.*
import com.shoprunner.baleen.types.*
import java.io.File

val description = "person".describeAs {
    "id".type(StringCoercibleToLong(LongType()), required = true)
    "firstName".type(StringType(0, 32), required = true)
    "middleName".type(AllowsNull(StringType(0, 32)))
    "lastName".type(StringType(0, 32), required = true)

    test("first name is not same as last name") { data ->
        assertNotEquals(
            "first != last",
            data.getAsStringOrNull("firstName"),
            data.getAsStringOrNull("lastName")
        )
    }
}

File("summary/example-xml.html").writer().use {
    validate(
        description = description,
        data = xml("./example.xml"),
        groupBy = groupByTag("file"),
        printers = arrayOf(ConsolePrinter, HtmlPrinter(it)),
    )
}