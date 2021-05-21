# Baleen-Script

Using Baleen-script is a low code, easy to get started using Baleen to validate your data. 
It is built on top of Kotlin scripting, so all you need is Kotlin installed and then you are good to go

## Quick Start

Install Kotlin on your machine or use an IDE like IntelliJ that supports executing kotlin scripts.

```bash
brew install kotlin
```

Alternatively use a docker image like [zenika/kotlin](https://hub.docker.com/r/zenika/kotlin).

Then create a file with a suffix `main.kts` and place these boilerplate lines at the top

```kotlin
#!/usr/bin/env kotlin

@file:DependsOn("com.shoprunner:baleen-script:1.14.0")

import com.shoprunner.baleen.script.*
import com.shoprunner.baleen.types.*
```

Finally add your test code within the `baleen` code block. For example, validating json

```json
{
  "id": 1,
  "firstName": "Jon",
  "lastName": "Smith"
}
```

then add this test code.

```kotlin
baleen {
    json("./example.json") {
        "id".type(IntegerType(), required = true)
        "firstName".type(StringType(0, 32), required = true)
        "lastName".type(StringType(0, 32), required = true)
    }
}
```

Then run it in IntelliJ or via command-line Kotlin or Docker

```bash
kotlin example.main.kts

docker container run -it --rm zenika/kotlin kotlin example.main.kts
```

## Examples

### JSON Validation

[./src/examples/example.json](./src/examples/example.json)
```json
{
  "id": 1,
  "firstName": "Jon",
  "lastName": "Smith"
}
```

```kotlin
baleen {
    json("./src/examples/example.json") {
        "id".type(IntegerType(), required = true)
        "firstName".type(StringType(0, 1), required = true)
        "lastName".type(StringType(0, 32), required = true)
    }
}
```

### XML Validation

[./src/examples/example.xml](./src/examples/example.xml)
```xml
<example>
    <id>1</id>
    <firstName>Jon</firstName>
    <lastName>Smith</lastName>
</example>
```

```kotlin
baleen {
    xml("./src/examples/example.xml") {
        "example".type("example".describeAs {
            "id".type(StringCoercibleToLong(LongType()), required = true)
            "firstName".type(StringType(0, 1), required = true)
            "lastName".type(StringType(0, 32), required = true)
        }, required = true)
    }
}
```

### CSV Validation

**Warning**: This is broken and will hang. Help needed to fix it!

[./src/examples/example.csv](./src/examples/example.csv)
```csv
id,firstName,lastName
0,Jon,Smith
1,Jane,Doe
2,Billy,Idol
```

```kotlin
baleen {
    csv("./src/examples/example.csv") {
        "id".type(StringCoercibleToLong(LongType()), required = true)
        "firstName".type(StringType(0, 1), required = true)
        "lastName".type(StringType(0, 32), required = true)
    }
}
```

### Database validation

In order to do database validation, the JDBC dependency must be added as a `@DependsOn`.

```kotlin
@file:DependsOn("net.snowflake:snowflake-jdbc:3.13.1")
```

* Snowflake Database Example: [snowflake.main.kts](src/examples/snowflake.main.kts)

#### Validate against all rows of a table
```kotlin
baleen {
    database {
        credentials {
            url = "jdbc:postgresql://localhost:5432/names"
            user = "user"
            password = "password"
        }

        // Does validation on all rows of a table.
        table(
            table = "engineer.example",
            tags = mapOf("ID" to withAttributeValue("ID"))
        ) {

            "ID".type(IntegerType())
            "FIRST_NAME".type(StringType(0, 32))
            "LAST_NAME".type(StringType(0, 32))
        }
    }
}
```

#### Validate against a random sampling of rows of a table
```kotlin
baleen {
    database {
        credentials {
            url = "jdbc:postgresql://localhost:5432/names"
            user = "user"
            password = "password"
        }

        sample(
            table = "engineer.example",
            samplePercent = 0.10,
            tags = mapOf("ID" to withAttributeValue("ID"))
        ) {

            "ID".type(IntegerType(), required = true)
            "FIRST_NAME".type(StringType(0, 1), required = true)
            "LAST_NAME".type(StringType(0, 32), required = true)
        }
    }
}
```

#### Validate against query
```kotlin
baleen {
    database {
        credentials {
            url = "jdbc:postgresql://localhost:5432/names"
            user = "user"
            password = "password"
        }

        query(
            queryName = "query example",
            query = "SELECT id, first_name, last_name FROM engineer.example WHERE last_name = 'Smith'",
            tags = mapOf("ID" to withAttributeValue("ID"))
        ) {

            "ID".type(IntegerType(), required = true)
            "FIRST_NAME".type(StringType(0, 1), required = true)
            "LAST_NAME".type(StringType(0, 32), required = true)
        }
    }
}
```

### Validating data from service

We provide a function called `http` that nests `get`, `post`, `put` or `delete` functions.
Then call the other validation functions with the body's inputStream.

```kotlin
baleen {
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
```

## Changing output location

By default, the baleen test output is printed to console. Other output formats are supported.
More than one can be passed in.

* Output.csv -> Output as multiple csv files
* Output.html -> Output as single html file
* Output.text -> Output as single text flle
* Output.console -> Print output to screen.

```kotlin
baleen("summaryOutDir", Output.console, Output.text, Output.html, Output.csv) {
    
}
```

## Tagging the tests

On each function, there is an optional tags map to tag useful row level data points
like a primary key.

With a column value:
```kotlin
tags = mapOf("ID" to withAttributeValue("ID"))
```

With a constant:
```kotlin
tags = mapOf("KEY" to withConstantValue("HARDCODED"))
```

With a function that takes the data row and outputs a string:
```kotlin
tags = mapOf("NAME" to { d ->
    when {
        d is Data -> "${d["FIRST_NAME"]}_${d["LAST_NAME"]}"
        else -> "error"
    }    
})
```
