# Baleen Json Schema Generator

Given a Baleen description, generate a Json Schema.


### Gradle
```kotlin
implementation("com.shoprunner:baleen-jsonschema-generator:$baleen_version")
```

## Example

```kotlin
val Dog = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
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

JsonSchemaGenerator.encode(Dog).writeTo(File("outDir"), true)

```

Will output to file `outDir/com/shoprunner/data/dogs/Dog.schema.json` with the following schema

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

Addiitonally, there is support writing self-describing json schemas.

```kotlin
JsonSchemaGenerator.encodeAsSelfDescribing(Dog, version = "1-1-1").writeTo(dir, true)
```

Will output to file `outDir/com/shoprunner/data/dogs/Dog.schema.json` with the following schema

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
  "$schema" : "http://iglucentral.com/schemas/com.snowplowanalytics.self-desc/schema/jsonschema/1-0-0",
  "self" : {
    "vendor" : "com.shoprunner.data.dogs",
    "name" : "Dog",
    "version" : "1-1-1",
    "format" : "jsonschema"
  }
}
``` 

## Adding mapping overrides

If the default Baleen to Json Schema mapping does not meet needs, you can use a custom mapping function.

Take the Dog and Pack descriptions:
```kotlin
val dogDescription = Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
    it.attr(
            name = "name",
            type = StringType(),
            markdownDescription = "The name of the dog"
    )

    it.attr(
            name = "hasSpots",
            type = StringCoercibleToBoolean(BooleanType()),
            markdownDescription = "Does the dog have spots"
    )
}

val packDescription = Baleen.describe("Pack", "com.shoprunner.data.dogs", "It's a pack of Dogs!") {
    it.attr(
            name = "name",
            type = StringType(),
            markdownDescription = "The name of the pack",
            required = true
    )

    it.attr(
            name = "dogs",
            type = OccurrencesType(dogDescription),
            markdownDescription = "The dogs in the pack",
            required = true
    )
}

```

For simple mappings, map the types

```kotlin
fun customSpotsMapper(baleenType: BaleenType, options: JsonSchemaOptions): JsonSchema =
    when (baleenType) {
        is StringCoercibleToBoolean -> StringSchema(enum = listOf("true", "false"))
        else -> JsonSchemaGenerator.recursiveTypeMapper(::customSpotsMapper, baleenType, options)
    }

val outputStream = ByteArrayOutputStream()
JsonSchemaGenerator.encode(dogDescription, typeMapper = ::customDogTypeMapper).writeTo(PrintStream(outputStream), true)
val jsonStr = outputStream.toString()
```

Will output a json string with has_spots overriden.
```json
{
  "id" : "com.shoprunner.data.dogs.Dog",
  "definitions" : {
    "record:com.shoprunner.data.dogs.Dog" : {
      "description" : "It's a dog. Ruff Ruff!",
      "type" : "object",
      "additionalProperties" : false,
      "properties" : {
        "name" : {
          "description" : "The name of the dog",
          "type" : "string",
          "maxLength" : 2147483647,
          "minLength" : 0
        },
        "hasSpots" : {
          "description" : "Does the dog have spots",
          "type" : "string",
          "enum" : [ "true", "false" ]
        }
      }
    }
  },
  "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Dog",
  "${'$'}schema" : "http://json-schema.org/draft-04/schema"
}
```

Overriding a data description will look like this:

```kotlin
fun customDogTypeMapper(baleenType: BaleenType, options: JsonSchemaOptions): JsonSchema =
    when {
        baleenType is DataDescription && baleenType.name == "Dog" && baleenType.nameSpace == "com.shoprunner.data.dogs.Dog" -> {
            val id = "com.shoprunner.data.dogs.CustomDog"
            ObjectSchema(emptyList(), false, mapOf( "my_name" to StringSchema()), id)
        }
        else -> recursiveTypeMapper(::customDogTypeMapper, baleenType, options)
    }

val outputStream = ByteArrayOutputStream()
JsonSchemaGenerator.encode(packDescription, typeMapper = ::customDogTypeMapper).writeTo(PrintStream(outputStream), true)
val jsonStr = outputStream.toString()
```

This will output a json schema with Dog replaced with "CustomDog".

```json
{
  "id" : "com.shoprunner.data.dogs.Pack",
  "definitions" : {
    "record:com.shoprunner.data.dogs.CustomDog" : {
      "type" : "object",
      "required" : [ ],
      "additionalProperties" : false,
      "properties" : {
        "my_name" : {
          "type" : "string"
        }
      }
    },
    "record:com.shoprunner.data.dogs.Pack" : {
      "description" : "It's a pack of Dogs!",
      "type" : "object",
      "required" : [ "name", "dogs" ],
      "additionalProperties" : false,
      "properties" : {
        "name" : {
          "description" : "The name of the pack",
          "type" : "string",
          "maxLength" : 2147483647,
          "minLength" : 0
        },
        "dogs" : {
          "description" : "The dogs in the pack",
          "type" : "array",
          "items" : {
            "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.CustomDog"
          }
        }
      }
    }
  },
  "${'$'}ref" : "#/definitions/record:com.shoprunner.data.dogs.Pack",
  "${'$'}schema" : "http://json-schema.org/draft-04/schema"
}
```
