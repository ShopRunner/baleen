# Baleen-Script

Using Baleen-script is a low code, easy to get started using Baleen to validate your data. 
It is built on top of Kotlin scripting, so all you need is Kotlin installed and then you are good to go

## Quick Start

Install Kotlin on your machine or use an IDE like IntelliJ that supports executing kotlin scripts.

```bash
brew install kotlin
```

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

```
baleen {
    json("./example.json") {
        "id".type(IntegerType(), required = true)
        "firstName".type(StringType(0, 32), required = true)
        "lastName".type(StringType(0, 32), required = true)
    }
}
```

## Examples

* Snowflake Database Example: [snowflake.main.kts](src/examples/snowflake.main.kts)

### Validate against all rows of a table
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

### Validate against a random sampling of rows of a table
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

### Validate against query
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
