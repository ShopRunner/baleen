# Baleen Text Printer

Writes the Sequence of ValidationResults to output directory with csv files.

## Install

```kotlin
implementation("com.shoprunner:baleen:$baleen_version")
implementation("com.shoprunner:baleen-printer-text:$baleen_version")

```

## Use

```kotlin
// Do validation, get a `Validation` or `CachedValidation` result
val results: Iterable<ValidationResult> = validation.results

val outputFile = File("output.txt")
val printer = TextPrinter(outputFile, prettyPrint = false)
    
printer.print(results)
```
