# Baleen with Kotlin Data Classes

Given a Kotlin Data class, generate at compile time, a baleen data description using Kotlin annotation processing (kapt).
Two Baleen annotations are given along with base extension methods.

Data classes can also be used as source of truth for schemas, using Baleen as the bridge to other data formats such as 
Avro, XSD, and json-schema. This can allow for modeling data in code and then using code generation tools to build schemas
across multiple formats.  To support this effort, annotations are added for aliases and Kotlin default values are supported. 

## Setup

Kapt is used to generate the required files. Using the gradle plugin is mandatory. The baleen-kotlin-api is separated
from the kapt implementation to keep dependencies as lightweight as possible.

```groovy
plugins {
    id "org.jetbrains.kotlin.kapt" version "1.3.41"
}

dependencies {
    implementation("com.shoprunner:baleen-kotlin-api:$baleen_version")
    kapt("com.shoprunner:baleen-kotlin-kapt:$baleen_version")
}
```

## @DataDescription

Annotation on the data class itself.

```kotlin
package com.shoprunner.baleen.kotlin.example

import com.shoprunner.baleen.annotation.DataDescription

/**
 * The dog data class
 */
@DataDescription
data class Dog(
    /** A non-nullable name field **/
    var name: String,
    
    /** A nullable (optional) number of legs field */
    var numLegs: Int?
)
```

### Supported types

* String
* Boolean
* Float
* Double
* Int
* Long
* Byte
* Short
* BigDecimal
* BigInteger
* Instant
* Array
* Iterable: List & Set
* Map

## @DataTest

Annotation on a top-level method to add additional tests.

```kotlin
package com.shoprunner.baleen.kotlin.example

import com.shoprunner.baleen.Assertions
import com.shoprunner.baleen.annotation.DataTest

@DataTest
fun test3orMoreLegs(assertions: Assertions, dog: Dog) = with(assertions) {
    assertThat(dog.numLegs).isGreaterThanEquals(3)
}
```

## @Name

Overrides the attribute name in the data. Without the annotation by default the field's name is used.  Different than @Alias since the original
attribute name is no longer used.

```kotlin
@DataDescription
data class ModelWithDifferentFieldNames(
    @Name("field_name")
    var fieldName: String
)
```

## @Alias

Specifies additional attribute names that the data responds to. Useful for backwards compatibility when field names
change.

```kotlin
@DataDescription
data class ModelWithAliases(
    @Alias("field_name")
    var fieldName: String,

    @Alias("another_name1", "another_name2")
    var anotherName: String
)
```

## Default Value

Unfortunately, kotlin annotation processing does not support reading the default value specified in the parameter declaration.
The annotation processor generates a dummy instance so that it can use the default values generated by the class in the
schema.  The major caveat with this approach is that defaults that are computational or rely on another property will
be calculated against the dummy instance. This may not be what is intended.

```kotlin
package com.shoprunner.baleen.kotlin.example

import com.shoprunner.baleen.annotation.DataDescription
import java.time.Instant

/**
 * The dog data class
 */
@DataDescription
data class Dog(
    // Default value set to "Name"
    var name: String = "Name",
    
    // Default value will be null
    var numLegs: Int? = null,

    // Default value will be always null
    var numArms: Int? = numLegs,
    
    // Default value will always be the timestamp when instance class is generated. This is likely not what intended.
    var birthday: Instant = Instant.now()
)
```

## Generated files

Gradle task `kotlinKapt` is called before `kotlinCompile`. When called, Each data class adds two files, 
the `Type` and the `Extension`.  The `Type` is the Baleen Data Description.  The `Extension` file implements
extension methods on the data class for use in code.

`DogType.kt`
```kotlin
@file:JvmName("DogType")

package com.shoprunner.baleen.kotlin.example

import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.StringType
import kotlin.jvm.JvmName

val DogType: DataDescription = Baleen.describe("Dog", "com.shoprunner.baleen.kotlin.example",
    markdownDescription = "The dog data class") {
      /* @org.jetbrains.annotations.NotNull name: java.lang.String */
      it.attr(
        name = "name",
        type = StringType(),
        markdownDescription = "A non-nullable name field",
        required = true
      )
        
      /* @org.jetbrains.annotations.Nullable numLegs: java.lang.Integer */
      it.attr(
        name = "numLegs",
        type = AllowsNull(IntType()),
        markdownDescription = "A nullable (optional) number of legs field",
        required = true
      )
        
      /* 
         @org.jetbrains.annotations.NotNull,@com.shoprunner.baleen.annotation.DataTest
         assert3orMoreLegs(com.shoprunner.baleen.kotlin.example.Dog,com.shoprunner.baleen.DataTrace)
      */
      it.test {
        dataTrace, data ->
          try {
            val obj = asDog(data)
            assert3orMoreLegs(obj, dataTrace).asSequence()
          }
          catch(npe: NullPointerException) {
            sequenceOf(ValidationError(dataTrace,
                """Unable to run test `com.shoprunner.baleen.kotlin.example.assert3orMoreLegs`: '${npe.message}'""",
                data))
          }
          catch(cce: ClassCastException) {
            sequenceOf(ValidationError(dataTrace,
                """Unable to run test `com.shoprunner.baleen.kotlin.example.assert3orMoreLegs`: '${cce.message}'""",
                data))
          }
      }
    }
```

`DogExtension.kt`
```kotlin
@file:JvmName("DogExtension")

package com.shoprunner.baleen.kotlin

import com.shoprunner.baleen.Context
import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.Validation
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.datawrappers.HashData
import com.shoprunner.baleen.kotlin.example.Dog
import com.shoprunner.baleen.kotlin.example.DogType
import kotlin.Int
import kotlin.String
import kotlin.jvm.JvmName

fun Dog.dataDescription(): DataDescription = DogType

fun Dog.validate(dataDescription: DataDescription = this.dataDescription(), dataTrace: DataTrace =
    dataTrace()): Validation = dataDescription.validate(Context(this.asHashData(), dataTrace))

internal fun Dog.asHashData(): HashData = HashData(mapOf(
  "name" to name,
  "numLegs" to numLegs
))

internal fun asDog(data: Data): Dog = Dog(
  name = data["name"]?.let { it as String }!!,
  numLegs = data["numLegs"]?.let { it as Int }
)

```

## Using the generated files

```kotlin
val dog = Dog("Fido", 4)

// Get the data description
val dogType = dog.dataDescription()

// Validate the data against the description and tests
dog.validate()

// Or pass the data description if generated externally
val externalDogType = generatedElseWhere()
dog.validate(externalDogType, dataTrace("External Source"))
```

## Generating Data Classes from Existing Baleen DataDescriptions

If DataDescriptions already exist, generate data classes and use annotation processing
to manage Baleen schemas.

In the gradle project
```
dependencies {
    implementation 'com.shoprunner:baleen-kotlin-generator:x.y.z'
}
```

Then where the generation happens:

```kotlin
val dataDescription: DataDescription = // Generated elsewhere

val dir = File("build/baleen-gen-test")
val sourceDir = File(dir, "src/main/kotlin")

// Generate Data Class Files
dataDescription.writeDataClassesTo(sourceDir)

// Or with some optional overrides
dataDescription.writeDataClassesTo(
    dir = sourceDir,
    options = Options(
        // Set how to interpret coercible types
        coercibleHandler = CoercibleHandlerOption.FROM,

        // Set overrides
        typeOverrides = listOf(
            TypeOverride(
                isOverridable = { t -> t is StringType },
                override = { Long::class }
            )
        )
    )
)
```

## TODO

* Missing Data Types (enums, LocalDate, etc.)
* Support for annotations from popular libraries
  * Java's validation annotations - javax.validation.annotations
  * Kotlinx Serialization
  * Jackson JSON annotations
  * Java's XML annotations
* Support for Java classes (should be straightforward to add)
