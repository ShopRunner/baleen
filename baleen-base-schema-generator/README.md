# Baleen Base Schema Generators

Given a Baleen data description generate a XML Schema Definition (XSD) that can be used by 3rd party tools for code generation or validation.
The generated XSD is not guarnteed to be as strict as the Baleen data description.

### The following types are not supported
Pull requests welcome.
 
 - `MapType`
 - `UnionType`

### Example of how to generate XSD

```kotlin
import com.shoprunner.baleen.xsd.XsdGenerator.encode
import java.io.FileOutputStream
import java.io.PrintStream

PrintStream(FileOutputStream("dog.xsd")).use { printStream ->
  dogDescription.encode(printStream)
}
```

### To Generate a XSD with Custom Types

```kotlin
import com.shoprunner.baleen.xsd.XsdGenerator.encode
import java.io.FileOutputStream
import java.io.PrintStream    
 
fun customDogMapper(baleenType: BaleenType): TypeDetails =
    when(baleenType) {
        is DogRatingType -> TypeDetails(simpleType = SimpleType(
            Restriction(
                minInclusive = MinInclusive(BigDecimal.TEN)
        )))
        else -> XsdGenerator.defaultTypeMapper(baleenType)
    }
    
PrintStream(FileOutputStream("dog.xsd")).use { printStream ->
  dogDescription.encode(printStream, ::customDogMapper)
}
```