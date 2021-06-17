# Baleen-Script

Using Baleen-script is a low code, easy to get started using Baleen to validate your data. 
It is built on top of Kotlin scripting, so all you need is Kotlin installed and then you are good to go

## Quick Start

Install Kotlin on your machine or use an IDE like IntelliJ that supports executing kotlin scripts.

```bash
brew install kotlin
```

Alternatively use a docker image like [zenika/kotlin](https://hub.docker.com/r/zenika/kotlin).

For example, validating json

```json
{
  "id": 1,
  "firstName": "Jon",
  "lastName": "Smith"
}
```

First create a file with a suffix `main.kts` and place these boilerplate lines at the top.

```kotlin
#!/usr/bin/env kotlin

@file:DependsOn("com.shoprunner:baleen-script:1.14.1")

import com.shoprunner.baleen.*
import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.printer.*
import com.shoprunner.baleen.script.*
import com.shoprunner.baleen.types.*
```

Next define the "shape" of the data you would like to validate.

```kotlin
val description = "Person".describeAs {
    "id".type(IntegerType(), required = true)
    "firstName".type(StringType(0, 32), required = true)
    "middleName".type(AllowsNull(StringType(0, 32)))
    "lastName".type(StringType(0, 32), required = true)
}
```

Within the `describeAs` function, also include any data level tests.

```kotlin
val description = "Person".describeAs {
    "id".type(IntegerType(), required = true)
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
```

Finally, call the validate function.

```kotlin
validate(
    description = description,
    data = csv("./example.json")
)
```

It can run it in IntelliJ or via command-line Kotlin or Docker

```bash
kotlin example-json.main.kts

docker container run -it --rm zenika/kotlin kotlin example-json.main.kts
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

[./src/examples/example-json.main.kts](./src/examples/example-json.main.kts)
```kotlin
val description = "Person".describeAs {
    "id".type(IntegerType(), required = true)
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

validate(
    description = description,
    data = json("./src/examples/example.json")
)
```

### XML Validation

[./src/examples/example.xml](./src/examples/example.xml)
```xml
<person>
    <id>1</id>
    <firstName>Jon</firstName>
    <lastName>Smith</lastName>
</person>
```

[./src/examples/example-xml.main.kts](./src/examples/example-xml.main.kts)
```kotlin
val description = "person".describeAs {
    "id".type(IntegerType(), required = true)
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

validate(
    description = description,
    data = xml("./src/examples/example.xml")
)
```

### CSV Validation

[./src/examples/example.csv](./src/examples/example.csv)
```csv
id,firstName,lastName
0,Jon,Smith
1,Jane,Doe
2,Billy,Idol
```

[./src/examples/example-csv.main.kts](./src/examples/example-csv.main.kts)
```kotlin
val description = "Person".describeAs {
    "id".type(IntegerType(), required = true)
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

validate(
    description = description,
    data = csv("./src/examples/example.csv")
)
```

### Database validation

In order to do database validation, the JDBC dependency must be added as a `@DependsOn`.

```kotlin
@file:DependsOn("net.snowflake:snowflake-jdbc:3.13.1")
```

* Snowflake Database Example: [snowflake.main.kts](src/examples/example-db-snowflake.main.kts)

#### Validate against all rows of a table
```kotlin
val description = "Person".describeAs {
    "id".type(IntegerType(), required = true)
    "first_name".type(StringType(0, 32), required = true)
    "last_name".type(StringType(0, 32), required = true)

    test("first name is not same as last name") { data ->
        assertNotEquals(
            "first != last",
            data.getAsStringOrNull("first_name"),
            data.getAsStringOrNull("last_name")
        )
    }
}.tags("ID" to withAttributeValue("ID"))

val connection = getConnection(
    url = "jdbc:postgresql://localhost:5432/names",
    user = "user",
    password = "password"
)

validate(
    description = description,
    data = table(
        table = "engineer.example",
        connection = connection
    )
)
```

#### Validate against a random sampling of rows of a table
```kotlin
validate(
    description = description,
    data = sample(
        table = "engineer.example",
        connection = connection,
    )
)
```

#### Validate against query
```kotlin
validate(
    description = description,
    data = query(
        queryName = "query example",
        query = "SELECT id, first_name, last_name FROM engineer.example WHERE last_name = 'Smith'",
        connection = connection,
    )
)
```

### Validating data from service

We provide a function called `http` that takes a Method (GET, POST, PUT, or DELETE).
Then within the http function, tell it how to validate the data by passing `json`, `xml`, or `csv`.

[./src/examples/example-http.main.kts](./src/examples/example-http.main.kts)
```kotlin
val description = "Person".describeAs {
    "data".type {
        "id".type(IntegerType(), required = true)
        "firstName".type(StringType(0, 32), required = true)
        "lastName".type(StringType(0, 32), required = true)

        test("first name is not same as last name") { data ->
            assertNotEquals(
                "first != last",
                data.getAsStringOrNull("first_name"),
                data.getAsStringOrNull("last_name")
            )
        }
    }
}

validate(
    description = description,
    data = http(
        url = "https://reqres.in/api/users/2",
        method = Method.GET,
        contentType = "application/json",
        data = json()
    )
)
```

## Changing output location

By default, the baleen test output is printed to console. Other output formats are supported.
More than one can be passed in.

* CsvPrinter(outputDir) -> Output as multiple csv files
* HtmlPrinter(OutputStreamWriter) -> Output as single html file
* TextPrinter(OutputStreamWriter) -> Output as single text flle
* ConsolePrinter -> Print output to screen.
* LogPrinter() -> Print to log

```kotlin
File("summary.html").writer().use {
    validate(
        description = description,
        data = json("./example.json"),
        printers = arrayOf(ConsolePrinter, HtmlPrinter(it)),
    )
}
```
