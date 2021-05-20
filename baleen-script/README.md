# Baleen Script

## Installation

### Gradle
```kotlin
implementation("com.shoprunner:baleen-script:$baleen_version")
```

Use [Baleen](https://github.com/ShopRunner/baleen) in a script to test against a database.

## Running Locally

Make sure you set your Snowlake account's url, user, and password if necessary.

```bash
# Snowflake 
DATABASE_USER=<your user> DATABASE_URL="jdbc:snowflake://<snowflake-url>/?warehouse=BALEEN&db=DEFAULT" \
  ./gradlew :baleen-script:run --args=baleen-script/src/examples/snowflake.main.kts 

```


## Examples

* Snowflake Example: [snowflake.baleen.kts](src/examples/snowflake.main.kts)

### Validate against all rows of a table
```kotlin
database(credentials) {
    table("example.employees", tags = mapOf("ID" to withAttributeValue("ID"))) {
        // Your tests here
    }
}
```

### Validate against a random sampling of rows of a table
```kotlin
database(credentials) {
    sample("example.employees", 1_000, tags = mapOf("ID" to withAttributeValue("ID"))) {
        // Your tests here
    }
}
```

### Validate against query
```kotlin
database(credentials) {
    query("smith_employees", "SELECT id, first_name, last_name FROM engineer.example WHERE last_name = 'Smith'",
        tags = mapOf("ID" to withAttributeValue("ID"))) {
        // Your tests here
    }
}
```

### Defining columns

Each column can be listed out and related to a [BaleenType](https://github.com/ShopRunner/baleen/tree/master/baleen/src/main/kotlin/com/shoprunner/baleen/types).

```kotlin
database(credentials) {
    table("example.employees") {
        "ID".type(IntegerType())
        "FIRST_NAME".type(StringType(0, 32))
        "LAST_NAME".type(StringType(0, 32))
    }
}
```

### Defining column tests

Test can be written at the column level using the `test` function. There are assertion function that can be used.
There can be multiple tests.

```kotlin
database(credentials) {
    table("example.employees") {
        "LAST_NAME".type(StringType(0, 32)).describe {
            test("is not all caps") { data ->
                val lastName = data.getAsString("LAST_NAME") 
                assertNotEquals("LAST_NAME is not all capitalized", lastName, lastName?.toUpperCase())
            }
        }
    }
}
```

### Defining row level tests

Each row can be tested and can have multiple tests. There are assertion function that can be used.

```kotlin
database(credentials) {
    table("example.employees") {
        test("ID is odd") { data ->
            assertTrue("ID is Odd", data.getAsLong("ID")!! % 2 != 0L, data.getAsLong("ID"))
        }

        test("FIRST_NAME != LAST_NAME") { data ->
            assertNotEquals("first name != last name", data.getAsString("FIRST_NAME"), data.getAsString("LAST_NAME"))
        }

        test("names should not be all caps") { data ->
            val firstName = data.getAsString("FIRST_NAME")
            val lastName = data.getAsString("LAST_NAME")
            assertNotEquals("first name is not all caps", firstName?.toUpperCase(), firstName)
            assertNotEquals("last name is not all caps", lastName?.toUpperCase(), lastName)
        }
    }
}
```

## Tagging the tests

On the `table`, `sample`, and `query` function, there is an optional tags map to tag useful row level data points
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
