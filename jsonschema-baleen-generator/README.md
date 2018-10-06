# Baleen Json Schema Generator

Given a Json Schema, generate a Baleen description.

```json
{
  "id" : "com.shoprunner.data.dogs.Dog",
  "definitions" : {
    "record:com.shoprunner.data.dogs.Dog" : {
      "description" : "It's a dog. Ruff Ruff!",
      "type" : "object",
      "required" : [ "name" ],
      "additionalProperties" : false,
      "properties" : {
        "name" : {
          "description" : "The name of the dog",
          "type" : "string",
          "maxLength" : 2147483647,
          "minLength" : 0
        },
        "legs" : {
          "description" : "The number of legs",
          "default" : null,
          "oneOf" : [ {
            "type" : "null"
          }, {
            "type" : "integer",
            "maximum" : 2147483647,
            "minimum" : -2147483648
          } ]
        }
      }
    }
  },
  "$ref" : "#/definitions/record:com.shoprunner.data.dogs.Dog",
  "$schema" : "http://json-schema.org/draft-04/schema"
}
```

```kotlin
// Generate from a JSON string
BaleenGenerator.encode(jsonSchemaStr.parseJsonSchema()).writeTo(File("outDir"))

// Generate from a JSON file
BaleenGenerator.encode(File("Dog.schema.json").parseJsonSchema()).writeTo(File("outDir"))

// Generate from a URL
BaleenGenerator.encode(URL("http://example.com/jsonschema/Dog.schema.json").parseJsonSchema()).writeTo(File("outDir"))
```

Will output to file `outDir/com/shoprunner/data/dogs/Dog.kt` with the following description

```kotlin
package com.shoprunner.data.dogs

import com.shoprunner.baleen.Baleen.describe
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.StringType

val Dog: DataDescription = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
    it.attr(
        name = "name",
        type = StringType(),
        markdownDescription = "The name of the dog",
        required = true
    )
    it.attr(
        name = "legs",
        type = AllowsNull(IntType()),
        markdownDescription = "The number of legs",
        required = false,
        default = null
    )
}
```

## Adding mapping overrides

If the default Json Schema to Baleen mapping does not meet needs, a function override can be used.

```kotlin
fun mapIntegerSchemaToStringCoercibleToBoolean(j: IntegerSchema): CodeBlock {
    return CodeBlock.of("%T(%T())", StringCoercibleToLong::class, LongType::class)
}

val longTypeOverride = (::mapIntegerSchemaToStringCoercibleToBoolean).asBaleenOverride()

BaleenGenerator.encode(jsonSchemaStr.parseJsonSchema(), listOf(longTypeOverride)).writeTo(File("outDir"))
```

Will output the following description:

```kotlin
package com.shoprunner.data.dogs

import com.shoprunner.baleen.Baleen.describe
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.StringCoercibleToLong
import com.shoprunner.baleen.types.StringType

val Dog: DataDescription = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
    it.attr(
        name = "name",
        type = StringType(),
        markdownDescription = "The name of the dog",
        required = true
    )
    it.attr(
        name = "legs",
        type = AllowsNull(StringCoercibleToLong(LongType()),
        markdownDescription = "The number of legs",
        required = false,
        default = null
    )
}
```

## Notes

Not Supported (PR's welcome!):
* `allOf`
* `not`
* `patternProperties`
* All string `format`s except for `date-time`
* When `additionalProperties` are not boolean, the `object` becomes a map even though `additionalProperties` can also be on `object`.
* `dependencies`