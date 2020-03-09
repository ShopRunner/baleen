[![Maven Central](https://img.shields.io/maven-central/v/com.shoprunner/baleen.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.shoprunner%22%20AND%20%22baleen%22)

# Baleen

Baleen is a library for validating streams of data (XML, CSV, ...).  It can be especially useful for 
legacy data because a schema can slowly be introduced.

Since it is written in Kotlin it can be easily used in any JVM language.

## Getting Started

### Binaries
Binaries and dependency information for Maven, Ivy, Gradle and others can be found at [maven.org](https://search.maven.org/search?q=g:%22com.shoprunner%22%20AND%20%22baleen%22).

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
import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.types.StringType

val departments = listOf("Mens", "Womens", "Boys", "Girls", "Kids", "Baby & Toddler")

val productDescription = "Product".describeAs {

    "sku".type(StringType(min = 1, max = 500),
          required = true)

    "brand_manufacturer".type(StringType(min = 1, max = 500),
          required = true)

    "department".type(StringType(min = 0, max = 100))
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

### Warnings

Sometimes you will want an attribute or type to warn instead of error. The `asWarnings()` method will transform the output
from `ValidationError` to `ValidationWarning` for all nested tests run underneath that attribute/type.

```kotlin
import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.asWarnings


val productDescription = "Product".describeAs {

    // The asWarnings() method is on StringType. Min/max are warnings, but required is still an error.
    "sku".type(StringType(min = 1, max = 500).asWarnings(), required = true) 

    // The asWarnings() method is on the attribute. Min/max and required are all warnings.
    "brand_manufacturer".type(StringType(min = 1, max = 500), required = true).asWarnings()

    // The asWarnings() method is on the attribute. The attribute's custom test will also be turned into a warning.
    "department".type(StringType(min = 0, max = 100)).describe { attr ->

        attr.test { datatrace, value ->
            val department = value["department"]
            if (department != null && !departments.contains(department)) {
                sequenceOf(ValidationError(dataTrace, "Department ($department) is not a valid value.", value))
            } else {
                sequenceOf()
            }
        }
    }.asWarnings()
}
```

## Gotchas

- Baleen does not assume that an attribute is not set and an attribute that is set with the value of null are the same thing.

## Similar Projects

- [Clojure Spec](https://clojure.org/guides/spec)
