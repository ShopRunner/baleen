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

### Tagging

A feature of Baleen is to add tags to tests, so that you can more easily identify, annotate, and filter your results.
There are a couple use-cases tagging becomes useful. For example, you have an identifier, like a sku, that you want each
test to have so that you can group together failed tests by that identifier. Another use-case is that you have different
priority levels for your tests that you can set so you can highlight the most important errors.

```kotlin
val productDescription = "Product".describeAs {

    // The tag() method is on StringType and dynamic tag pulls the value.
    "sku".type(StringType().tag("priority", "critical").tag("sku", withValue()))

    // The tag() method is on the attribute and the dynamic tag pulls an attribute value from sku.
    "brand_manufacturer".type(StringType(), required = true)
        .tag("priority", "low")
        .tag("sku", withAttributeValue("sku"))
 
    // The tag() method is on the attribute, and a custom tag function is used that returns a String
    "department".type(StringType(min = 0, max = 100))
        .tag("priority", "high")
        .tag("sku", withAttributeValue("sku"))
        .tag("gender") { d ->
            when {
                d is Data && d.containsKey("gender") -> 
                    when(d["gender"]) {
                        "male" -> "male"
                        "mens" -> "male"
                        "female" -> "female"
                        "womens" -> "femle"
                        else -> "other"
                    }
                else -> "none"
            }
        }
}
// Tag is on data description and the dynamic tag pulls attribute value from sku field  from the data
.tag("sku", withAttributeValue("sku"))
``` 

Tagging is also done at the data evaluation level.  When writing tests, DataTrace can be updated with tags
```kotlin
    "department".type(StringType(min = 0, max = 100)).describe { attr ->
        attr.test { datatrace, value ->
            val department = value["department"]
            if (department != null && !departments.contains(department)) {

                // datatrace has the sku tag added
                sequenceOf(ValidationError(
                    dataTrace.tag("sku", value["sku"] ?: "null"), 
                    "Department ($department) is not a valid value.",
                     value
                ))

            } else {
                sequenceOf()
            }
        }
    }
``` 

Some Baleen Validation libraries, such as the XML or JSON validators, use tags to add line and column numbers as it 
parses the original raw data. This will help identify errors in the raw data much more quickly.    

## Gotchas

- Baleen does not assume that an attribute is not set and an attribute that is set with the value of null are the same thing.

## Similar Projects

- [Clojure Spec](https://clojure.org/guides/spec)
