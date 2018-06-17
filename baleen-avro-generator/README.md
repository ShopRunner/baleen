# Baleen Avro Schema Generators

## Goal 1: Support Legacy Avro Schemas

Given legacy Avro schemas, create Baleen data descriptions to do validation.

Create Baleen description files from Avro schemas

```kotlin
import com.shoprunner.baleen.avro.BaleenEncoder.encodeTo

val parser = Schema.Parser()
val dogSchemaStr = """
|{
|   "type": "record",
|   "namespace": "com.shoprunner.data.dogs",
|   "name": "Dog",
|   "doc": "It's a dog. Ruff Ruff!",
|   "fields": [
|        { "name": "name", "type": "string", "doc": "The name of the dog" },
|        { "name": "legs", "type": ["null", "long", "int"], "default": null, "doc": "The number of legs" }
|   ]
|}
""".trimMargin()
val dogSchema = parser.parse(dogSchemaStr)

val sourceDir = dogSchema.encodeTo(File("src/main/kotlin"))
```

This produces working Kotlin Code

```kotlin
package com.shoprunner.data.dogs

import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.DataDescription
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.UnionType
import kotlin.jvm.JvmStatic

/**
 * It's a dog. Ruff Ruff! */
object DogType {
    @JvmStatic
    val description: DataDescription =
            Baleen.describe("Dog", "com.shoprunner.data.dogs", "It's a dog. Ruff Ruff!") {
                p ->
                    p.attr(
                        name = "name",
                        type = StringType(),
                        markdownDescription = "The name of the dog",
                        required = true
                    )
                    p.attr(
                        name = "legs",
                        type = UnionType(LongType(), IntType()),
                        markdownDescription = "The number of legs",
                        required = false
                    )

            }

}
``` 

## Goal 2: Generate Avro Schemas from Baleen descriptions

```kotlin
import com.shoprunner.baleen.avro.AvroEncoder.encodeTo

val sourceDir = File("src/main/avro")
DogType.description.encodeTo(sourceDir)
```