#!/usr/bin/env kotlin

// Uncomment to use mavenLocal. Run `./gradlew publishToMavenLocal -Pskip.signing=true`
//@file:Repository("file:///Users/username/.m2/repository/")
@file:DependsOn("com.shoprunner:baleen-script:1.14.0")

import com.shoprunner.baleen.*
import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.printer.*
import com.shoprunner.baleen.script.*
import com.shoprunner.baleen.types.*
import java.io.File

val description = "person".describeAs {
    "data".type {
        "id".type(IntegerType(), required = true)
        "first_name".type(StringType(0, 32), required = true)
        "middle_name".type(AllowsNull(StringType(0, 32)))
        "last_name".type(StringType(0, 32), required = true)

        test("first name is not same as last name") { data ->
            assertNotEquals(
                "first != last",
                data.getAsStringOrNull("first_name"),
                data.getAsStringOrNull("last_name")
            )
        }
    }

}

File("summary/example-http.html").writer().use {
    validate(
        description = description,
        data = http(
            url = "https://reqres.in/api/users/2",
            method = Method.GET,
            contentType = "application/json",
            data = json()
        ),
        printers = arrayOf(ConsolePrinter, HtmlPrinter(it)),
    )
}