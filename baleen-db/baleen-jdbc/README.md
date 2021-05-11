# Baleen Database Validation

## Installation

### Gradle
```kotlin
implementation("com.shoprunner:baleen-jdbc:$baleen_version")
```

## Example

There are two functions available.

`table(tableName)` - Queries the entire table.
`query(queryStr)` - Allows custom query

Either pass the data description in as a parameter
```kotlin
val productDescription = "Product".describeAs {
    "id".type(LongType(min = 1), required = true)
    "name".type(StringType(min = 1, max = 500), required = true)
}

val validation = table("product", dbConnection, productDescription)
val validation = query("SELECT * FROM product WHERE name LIKE 'Shoes%", dbConnection, productDescription)

validation.isValid()
```

or inlined to the functions.

```kotlin
val validation = table("product", dbConnection) {
    "id".type(LongType(min = 1), required = true)
    "name".type(StringType(min = 1, max = 500), required = true)
}

val validation = query("shoes", "SELECT * FROM product WHERE name LIKE 'Shoes%", dbConnection) {
    "id".type(LongType(min = 1), required = true)
    "name".type(StringType(min = 1, max = 500), required = true)
}

validation.isValid()

```
