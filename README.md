# Baleen

Baleen is a library for validating streams of data (XML, CSV, ...).  It can be especially useful for 
legacy data because a schema can slowly be introduced.

Since it is written in Kotlin it can be easily used in any JVM language.

## Getting Started

### Binaries
Binaries and dependency information for Maven, Ivy, Gradle and others can be found at [maven.org](http://search.maven.org).

You will need the `baleen` core for describing the data and one or more parsers `baleen-csv` or `baleen-xml`.

Example for Maven:

```xml
<dependency>
    <groupId>com.shoprunner</groupId>
    <artifactId>baleen</artifactId>
    <version>x.y.z</version>
</dependency>
<dependency>
     <groupId>com.shoprunner</groupId>
     <artifactId>baleen-csv</artifactId>
     <version>x.y.z</version>
</dependency>
<dependency>
     <groupId>com.shoprunner</groupId>
     <artifactId>baleen-xml</artifactId>
     <version>x.y.z</version>
</dependency>
```
and for Gradle:

```groovy
compile 'com.shoprunner:baleen:x.y.z'
compile 'com.shoprunner:baleen-csv:x.y.z'
compile 'com.shoprunner:baleen-xml:x.y.z'
```

### Example

See [CSV example](baleen-csv/src/test/kotlin/com/shoprunner/baleen/csv/Example.kt)

## Getting Help

Join the [slack channel](https://join.slack.com/t/baleen-validation/signup)

## Core Concepts

- Tests are great

  There are a lot of great libraries for testing code.  We should use those same concepts for testing 
  data.

- Performance and streaming are important

  A data validation library should be able to handle large amounts of data quickly.

- Invalid data is also important

  Warnings and Errors need to be treated as first class objects.

- Data Traces
  
  Similar to a stack trace being used to debug a code path, a data trace can be used to debug a 
  path through data. 

- Don't map data to Types too early.

  Type safe code is great but if the data hasn't been santized then it isn't really typed.  

## Example Schema Definition

```kotlin
import com.shoprunner.baleen.Baleen.describeBy
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.types.StringType

val departments = listOf("Mens", "Womens", "Boys", "Girls", "Kids", "Baby & Toddler")

val productSpec = describeBy("Product") {
    attr( name = "sku",
          type = StringType(min = 1, max = 500),
          required = true)

    attr( name = "brand_manufacturer",
          type = StringType(min = 1, max = 500),
          required = true)

    attr( name = "department",
          type = StringType(min = 0, max = 100))
         .describe { attr ->

        attr.test { datatrace, value ->
            val department = value["department"]
            if (department != null && !departments.contains(department)) {
                sequenceOf(ValidationError(dataTrace, "Department ($department) is not a valid value.", value))
            } else {
                sequenceOf()
            }
        }
    }
}
```


## Gotchas

- Baleen does not assume that an attribute is not set and an attribute that is set with the value of null are the same thing.

## Similar Projects

- [Clojure Spec](https://clojure.org/guides/spec)
