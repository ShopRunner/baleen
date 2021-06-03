# Baleen HTML Printer

Writes the Sequence of ValidationResults to a formatted HTML file. 

## Install

```kotlin
implementation("com.shoprunner:baleen:$baleen_version")
implementation("com.shoprunner:baleen-printer-html:$baleen_version")

```

## Use

```kotlin
// Do validation, get a `Validation` or `CachedValidation` result
val results: Iterable<ValidationResult> = validation.results

val outputFile = File("index.html")
val printer = HtmlPrinter(outputFile)
    
printer.print(results)
```

The resulting html file contains all results grouped by summary, or each result in tables.