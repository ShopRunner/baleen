# Baleen Json Schema Generator

Given a Baleen description, generate a Json Schema.

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
  "$schema" : "http://iglucentral.com/schemas/com.snowplowananalytics.self-desc/schema/jsonschema/1-0-0",
  "self" : {
    "vendor" : "com.shoprunner.data.dogs",
    "name" : "Dog",
    "version" : "1-1-1",
    "format" : "jsonschema"
  }
}
``` 