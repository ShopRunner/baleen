[![Maven Central](https://img.shields.io/maven-central/v/com.shoprunner/baleen.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.shoprunner%22%20AND%20%22baleen%22)

# Baleen

Baleen is fluent Kotlin DSL for validating data (JSON, XML, CSV, Avro)

## Features

- [Validating JSON](./baleen-json-jackson)
- [Validating CSV](./baleen-csv)
- [Validating XML](./baleen-xml)
- [Generate JSON Schema from Baleen data description](./baleen-jsonschema-generator)
- [Generate Avro Schema from Baleen data description](./baleen-avro-generator)
- [Generate XSD Schema from Baleen data description](./baleen-xsd-generator)
- [Generate Kotlin data classes from Baleen schema](./baleen-poet)
- [Generate Baleen data description from Kotlin data class](./baleen-kotlin)
- [Generate Baleen data description from JSON Schema](./jsonschema-baleen-generator)
- [Generate Baleen data description from AVRO Schema](./baleen-avro-generator)

## Example Baleen Data Description

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
         .describeAs {
             test("department is correct value") { data -> 
                 assertThat(departments).contains(data.getAsString("department").getOrNull())
             }
         }
}

// Get your data
val data: Data = // get from file or database or whatever 

// Get Validation Results
val validation: Validation = dataDesc.validate(data)

// Each call on `isValid` and `results` will iterate over dataset again. 
// Warning: that for large datasets this will eat memory
val cachedValidation: CachedValidation = validation.cache()

// Check if any errors. True if no errors, false otherwise. 
// val isValid: Boolean = validation.isValid()
val isValid: Boolean = cachedValidation.isValid() 

// Iterate over results. Each iteration over results will execute entire flow again.
// validation.results.forEach { }
cachedValidation.results.forEach { }

// Summarize into Validation object with list of ValidationSummary with examples of errors included    
// val validationSummary: Validation= validation.createSummary()
val validationSummary: CachedValidation = cachedValidation.createSummary()
validationSummary.results.forEach { }

```

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

  Type safe code is great but if the data hasn't been sanitized then it isn't really typed.  

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
    "department".type(StringType(min = 0, max = 100)).describeAs {
        test("department is correct value") { data ->
            assertThat(departments).contains(data.getAsString("department").getOrNull())
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

Tagging is also done at the data evaluation level.  When writing tests, additional tags can be passed in using the Tagger function.
```kotlin
    "department".type(StringType(min = 0, max = 100)).describeAs {
        test("department is correct value", "sku" to withAttributeValue("sku")) { data ->
            assertThat(departments).contains(data.getAsString("department").getOrNull())
        }
    }
```

Some Baleen Validation libraries, such as the XML or JSON validators, use tags to add line and column numbers as it 
parses the original raw data. This will help identify errors in the raw data much more quickly.    

## Gotchas

- Baleen does not assume that an attribute is not set and an attribute that is set with the value of null are the same thing.

## Similar Projects

- [Clojure Spec](https://clojure.org/guides/spec)
