# Baleen with Kotlin Data Classes

Given a Kotlin Data class, generate at compile time, a baleen data description using Kotlin annotation processing (kapt).
Two Baleen annotations are given along with base extension methods.

## Setup

Kapt is used to generate the required files. Using the gradle plugin is mandatory. The baleen-kotlin-api is separated
from the kapt implementation to keep dependencies as lightweight as possible.

```groovy
plugins {
    id "org.jetbrains.kotlin.kapt" version "1.3.41
}

dependencies {
    implementation 'com.shoprunner:baleen-kotlin-api:x.y.z'
    kapt 'com.shoprunner:baleen-kotlin-kapt:x.y.z'
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

## @DataTest

Annotation on a top-level method to add additional tests.

```kotlin
package com.shoprunner.baleen.kotlin.example

import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.annotation.DataTest
import com.shoprunner.baleen.dataTrace

@DataTest
fun assert3orMoreLegs(dog: Dog, dataTrace: DataTrace = dataTrace()): Sequence<ValidationResult> {
    val numLegs = dog.numLegs
    if(numLegs == null) {
        return emptySequence()
    } else if (numLegs >= 3) {
        return sequenceOf(ValidationInfo(dataTrace, "Num Legs greater or equal to 3", numLegs))
    } else {
        return sequenceOf(ValidationError(dataTrace, "Num Legs less than 3", numLegs))
    }
}
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
                """Unable to cast data map to a 'com.shoprunner.baleen.kotlin.example.Dog': '${npe.message}'""",
                data))
          }
          catch(cce: ClassCastException) {
            sequenceOf(ValidationError(dataTrace,
                """Unable to cast data map to a 'com.shoprunner.baleen.kotlin.example.Dog': '${cce.message}'""",
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

## TODO

* Missing Data Types (enums, BigDecimal, BigInteger, LocalDate, etc.)
* Support for annotations from popular libraries
  * Java's validation annotations - javax.validation.annotations
  * Kotlinx Serialization
  * Jackson JSON annotations
  * Java's XML annotations
* Support for Java classes (should be straightforward to add)