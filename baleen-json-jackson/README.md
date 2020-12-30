# Baleen JSON Validation

## Installation

### Gradle
```kotlin
implementation "com.shoprunner:baleen:$baleen_version"
implementation "com.shoprunner:baleen-json-jackson:$baleen_version"
```

## Example

```kotlin
import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.json.JsonUtil
        
val productDescription = "Product".describeAs {
    "id".type(LongType(min = 1, max = 6500), required = true)
    "name".type(StringType(min = 1, max = 500), required = true)
}

JsonUtil.validate(
    productDescription, 
    DataTrace("employee object"),
    """
    {
        "id": "35",
        "name": ""
    }
    """.byteInputStream())
    .results.filterIsInstance<ValidationError>().forEach {
        println("${it.dataTrace.toList().joinToString("/")} ${it.message} ${it.dataTrace.tags}")
    }
```
produces
```
employee object/attribute "id" is not a long {line=3, column=20}
employee object/attribute "name" is not at least 1 characters {line=4, column=22}
```
