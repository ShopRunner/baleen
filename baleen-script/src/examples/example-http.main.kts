#!/usr/bin/env kotlin

// Uncomment to use mavenLocal. Run `./gradlew publishToMavenLocal -Pskip.signing=true`
//@file:Repository("file:///Users/username/.m2/repository/")
@file:DependsOn("com.shoprunner:baleen-script:1.14.0")

import com.shoprunner.baleen.*
import com.shoprunner.baleen.script.*
import com.shoprunner.baleen.types.*

baleen("summary", Output.console, Output.text, Output.html, Output.csv) {
    http {
        get("https://reqres.in/api/users/2", "application/json") { body ->
            json("http example", body!!.byteInputStream()) {
                "data".type {
                    "id".type(IntegerType(), required = true)
                    "first_name".type(StringType(0, 1), required = true)
                    "last_name".type(StringType(0, 32), required = true)
                }
            }
        }
    }
}