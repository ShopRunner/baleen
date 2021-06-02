# Baleen Java Logging Printer

Writes the Sequence of ValidationResults to log-file using java.util.logging.Logger.

## Install

```kotlin
implementation("com.shoprunner:baleen:$baleen_version")
implementation("com.shoprunner:baleen-printer-java-logger:$baleen_version")

```

## Use

```kotlin
// Do validation, get a `Validation` or `CachedValidation` result
val results: Iterable<ValidationResult> = validation.results

// Default log prints to INFO
val printer = LogPrinter()
printer.print(results)

// Overwrites for logger and logLevels are supported
val printer = LogPrinter(
    logger = "MyLogger",
    logLevel = Level.WARNING
)
printer.print(results)
```
