# Baleen XML Validation

## Installation

### Gradle
```kotlin
implementation("com.shoprunner:baleen-xml:$baleen_version")
```

## Example

```kotlin
import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.xml.XmlUtil

val productDescription = "product".describeAs {
    "id".type(LongType(min = 1, max = 6500), required = true)
    "name".type(StringType(min = 1, max = 500), required = true)
}

XmlUtil.validateFromRoot(
    productDescription,
    """
        <product>
            <id>0</id>
            <name></name>
        </product>
    """.byteInputStream())
    .results.filterIsInstance<ValidationError>().forEach {
        println("${it.dataTrace.toList().joinToString("/")} ${it.message} ${it.dataTrace.tags}")
    }
```
produces
```
attribute "product"/attribute "id" is not a long {line=3, column=21}
attribute "product"/attribute "name" is not at least 1 characters {line=4, column=23}
```
