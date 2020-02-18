# Baleen Base Schema Generators

This is the base for any generators that read Baleen types and convert them into another schema format.

Implementations:
* [XSD](../baleen-xsd-generator)
* [JsonSchema](../baleen-jsonschema-generator)

To Do:
* [Avro](../baleen-avro-generator)
* [Kotlin Data Classes](../baleen-kotlin/baleen-kotlin-generator)

## Gradle Setup

```groovy
dependencies {
    api project(':baleen')
    implementation project(':baleen-base-schema-generator')
}
```

## New Generator

Create a [new `Options` class](src/test/kotlin/com/shoprunner/baleen/generator/StringOptions.kt)
```kotlin
data class StringOptions(
    override val coercibleHandlerOption: CoercibleHandlerOption = CoercibleHandlerOption.FROM
) : Options
```

In your [new generator](src/test/kotlin/com/shoprunner/baleen/generator/StringGenerator.kt) implement `BaseGenerator` 
and override `defaultTypeMapper` with default mappings from Baleen types to new output. It needs to recursively call
`typeMapper` so that overrides can be implemented. In this example, we are transforming the Baleen type to String.

```kotlin
object StringGenerator : BaseGenerator<String, StringOptions> {
    override fun defaultTypeMapper(
        typeMapper: TypeMapper<String, StringOptions>,
        baleenType: BaleenType,
        options: StringOptions
    ): String =
        when (baleenType) {
            is AllowsNull<*> -> "AllowsNull(${typeMapper(baleenType.type, options)})"
            is CoercibleType<*, *> -> {
                val subType = baleenType.toSubType(options.coercibleHandlerOption)
                val mapped = typeMapper(subType, options)
                when (options.coercibleHandlerOption) {
                    CoercibleHandlerOption.FROM -> "CoercibleFrom($mapped)"
                    CoercibleHandlerOption.TO -> "CoercibleTo($mapped)"
                }
            }
            is OccurrencesType -> "Occurrences(${typeMapper(baleenType.memberType, options)})"
            is MapType -> {
                val key = typeMapper(baleenType.valueType, options)
                val value = typeMapper(baleenType.valueType, options)
                "Map($key, $value)"
            }
            is UnionType -> {
                baleenType.types
                    .map { typeMapper(it, options) }
                    .joinToString(", ", "Union(", ")")
            }
            is DataDescription -> {
                val name =
                    if (baleenType.nameSpace.isNotBlank()) "${baleenType.nameSpace}.${baleenType.name()}"
                    else baleenType.name
                val attrs = baleenType.attrs
                    .map { "${it.name}=${typeMapper(it.type, options)}" }
                    .joinToString(", ")
                "$name($attrs)"
            }
            else -> baleenType.name()
        }
}

```

## Supporting Overrides

Overrides can be supported with functions that take `BaleenType` and `Options` and returns the new output. To fallback
on the default mapping, call the `recursiveTypeMapper` function and pass the new mapper to it.

For example, we can override any `StringType` with a custom string.

```kotlin
fun customMapper(baleenType: BaleenType, options: StringOptions): String =
    when (baleenType) {
        is StringType -> "CustomOverride"
        else -> StringGenerator.recursiveTypeMapper(::customMapper, baleenType, options)
}
    
val output = customMapper(dataDescription, StringOptions())
```
