# Baleen CSV Validation

## Installation

### Gradle
```kotlin
implementation("com.shoprunner:baleen-csv:$baleen_version")
```

## Example

```kotlin
import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.csv.FlowableUtil
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.types.StringCoercibleToLong
import io.reactivex.rxkotlin.subscribeBy
import io.reactivex.rxkotlin.toFlowable

val productDescription = "Product".describeAs {
    "id".type(StringCoercibleToLong(LongType(min = 1, max = 6500)), required = true)
    "name".type(StringType(min = 1, max = 500), required = true)
}

FlowableUtil.fromCsvWithHeader(
    dataTrace = dataTrace("example.csv"),
    readerSupplier = {
        """
        id,name
        0,
        """.trimIndent().byteInputStream().bufferedReader()
    })
    .flatMap { productDescription.validate(it).results.toFlowable() }
    .publish()
    .also {
        it.filter { it is ValidationError }.map { it as ValidationError }
            .subscribeBy(
                onNext = {
                    println(
                        "${it.dataTrace.toList().joinToString("/")} ${it.message} ${it.dataTrace.tags}"
                    )
                }
            )
    }
    .connect()
```
produces
```
example.csv/line 2/attribute "id" is less than 1 {row=0, line=2, column=0}
example.csv/line 2/attribute "name" is not at least 1 characters {row=0, line=2, column=1}
```

[Longer CSV example](src/test/kotlin/com/shoprunner/baleen/csv/Example.kt)
