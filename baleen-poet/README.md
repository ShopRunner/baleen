# Baleen Poet

Baleen Poet is an extension of the awesome [KotlinPoet](https://github.com/square/kotlinpoet) library that supports writing Kotlin files. This library
takes a BaleenType as input and then writes it to the file.  This is especially useful for Baleen generators where an
external format like Json schema or Avro schemas can be imported to create a Baleen schema.

## Installation

### Gradle
```kotlin
implementation "com.shoprunner:baleen:$baleen_version"
implementation "com.shoprunner:baleen-poet:$baleen_version"
```

## Examples

Simple Baleen Types write to a file with no package.

```kotlin
import com.shoprunner.baleen.poet.toFileSpec
import com.shoprunner.baleen.types.BooleanType

val booleanTest = BooleanType()

booleanTest.toFileSpec().writeTo(File("src/"))
```

Creates `src/boolean.kt`
```kotlin
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.types.BooleanType

val boolean: BaleenType = BooleanType()
```

Setting the name and package can customize where the file gets written

```kotlin
import com.shoprunner.baleen.poet.toFileSpec
import com.shoprunner.baleen.types.BooleanType

val booleanTest = BooleanType()

booleanTest.toFileSpec("com.shoprunner.baleen.test", "MyBooleanType").writeTo(File("src/"))
```

Creates `src/com/shoprunner/baleen/test/MyBooleanType.kt`
```kotlin
package com.shoprunner.baleen.test

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.types.BooleanType

val MyBooleanType: BaleenType = BooleanType()
```

A DataDescription can be written to the file based off the Description name and namespace (if present).

```kotlin
import com.shoprunner.baleen.describeAs
import com.shoprunner.baleen.poet.toFileSpec
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.StringType

val type = "Dog".describeAs("com.shoprunner.baleen.test") {
    "name".type(StringType())
    "numLegs".type(AllowsNull(IntType()))
}

type.toFileSpec().writeTo(File("src/"))
```
Creates `src/com/shoprunner/baleen/test/Dog.kt`
```kotlin
package com.shoprunner.baleen.poet.test

import com.shoprunner.baleen.Baleen.describe
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.StringType

val Dog: DataDescription = describe("Dog", "com.shoprunner.baleen.poet.test", "") {
      it.attr(
        name = "name",
        type = StringType(min = 0, max = Int.MAX_VALUE)
      )
      it.attr(
        name = "numLegs",
        type = AllowsNull(IntType(min = Int.MIN_VALUE, max = Int.MAX_VALUE))
      )

    }
``` 

Nested Baleen descriptions are written to separate source files. Use `generateAllFileSpecs` to generate files for all
nested descriptions.

```kotlin
import com.shoprunner.baleen.describeAs
import com.shoprunner.baleen.poet.generateAllFileSpecs
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.StringType

val dog = "Dog".describeAs(nameSpace = "com.shoprunner.dog") {
    "name".type(StringType())
    "numLegs".type(AllowsNull(IntType()))
}

val pack = "Pack".describeAs(nameSpace = "com.shoprunner.pack") {
    "dogs".type(OccurrencesType(dog))
}

pack.generateAllFileSpecs().forEach { it.writeTo(File("src/")) }
```
Generates 2 source files

`src/com/shoprunner/dog/Dog.kt`
```kotlin
package com.shoprunner.dog

import com.shoprunner.baleen.Baleen.describe
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.StringType

val Dog: DataDescription = describe("NestedDog", "com.shoprunner.dog", "") {
      it.attr(
        name = "name",
        type = StringType(min = 0, max = Int.MAX_VALUE)
      )
      it.attr(
        name = "numLegs",
        type = AllowsNull(IntType(min = Int.MIN_VALUE, max = Int.MAX_VALUE))
      )

    }
```

`src/com/shoprunner/pack/Pack.kt`
```kotlin
package com.shoprunner.pack

import com.shoprunner.baleen.Baleen.describe
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.dog.Dog

val Pack: DataDescription = describe("Pack", "com.shoprunner.pack", "") {
      it.attr(
        name = "dogs",
        type = OccurrencesType(Dog)
      )

    }
```

Types can be overridden using a TypeMapper function. It takes the CodeBlock.Builder and BaleenType and allows for customized
code for BaleenTypes that need to be overriden or do not have mapping in the default type mapper.

```kotlin
typealias TypeMapper = (CodeBlock.Builder, BaleenType) -> CodeBlock.Builder

fun customOverride(builder: CodeBlock.Builder, baleenType: BaleenType): CodeBlock.Builder =
    when {
        baleenType is StringType -> builder.add("%T()", IntType::class)
        baleenType is DataDescription && baleenType.name == "Dog" -> builder.add("%T()", StringType::class) 
        else -> defaultTypeMapper(builder, baleenType, ::customOverride) // Recursive call here
    }

val spec = type.toFileSpec(typeMapper = ::customOverride)
spec.writeTo(File("src/"))

```
