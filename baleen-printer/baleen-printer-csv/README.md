# Baleen CSV Printer

Writes the Sequence of ValidationResults to output directory with csv files.

## Install

```kotlin
implementation("com.shoprunner:baleen:$baleen_version")
implementation("com.shoprunner:baleen-printer-csv:$baleen_version")

```

## Use

```kotlin
// Do validation, get a `Validation` or `CachedValidation` result
val results: Iterable<ValidationResult> = validation.results

val outputDir = File("output")
val printer = CsvPrinter(outputDir)
    
// ValidationSuccess, ValidationInfo, ValidationError, ValidationWarning will write to results.csv
printer.print(results)

val summaryResults = results.summary()

// ValidationSummary will write to summary.csv and errors and warning for each summary will got to a separate file
printer.print(summaryResults)
```
