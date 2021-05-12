# Baleen Database Validation

## Installation

### Gradle
```kotlin
implementation("com.shoprunner:baleen-jdbc:$baleen_version")
```

Also include the jdbc driver for the database of choice. It is not included.

## Example

There are two functions available.

`validateTable(tableName)` - Queries the entire table.
`validateQuery(queryStr)` - Allows custom query

Either pass the data description in as a parameter
```kotlin
val productDescription = "Product".describeAs {
    "id".type(LongType(min = 1), required = true)
    "name".type(StringType(min = 1, max = 500), required = true)
}

val validation = validateTable("product", dbConnection, productDescription)
val validation = validateQuery("SELECT * FROM product WHERE name LIKE 'Shoes%", dbConnection, productDescription)

validation.isValid()
```

or inlined to the functions.

```kotlin
val validation = validateTable("product", dbConnection) {
    "id".type(LongType(min = 1), required = true)
    "name".type(StringType(min = 1, max = 500), required = true)
}

val validation = validateQuery("shoes", "SELECT * FROM product WHERE name LIKE 'Shoes%", dbConnection) {
    "id".type(LongType(min = 1), required = true)
    "name".type(StringType(min = 1, max = 500), required = true)
}

validation.isValid()
```

It is advisable to add tags for primary key to make it easier to find bad data later.

```kotlin
val validation = validateTable("product", dbConnection, tags=mapOf("id" to withAttributeValue("id"))) {
    "id".type(LongType(min = 1), required = true)
    "name".type(StringType(min = 1, max = 500), required = true)
}

val validation = validateQuery("shoes", "SELECT * FROM product WHERE name LIKE 'Shoes%", dbConnection,
    tags=mapOf("id" to withAttributeValue("id"))) {
    
    "id".type(LongType(min = 1), required = true)
    "name".type(StringType(min = 1, max = 500), required = true)
}

validation.isValid()
```