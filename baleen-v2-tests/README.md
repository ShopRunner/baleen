# Preparation for version 2

Do test driven development for version 2

## Use Case 1 - Given we have the data, we define the schema manually and validate the data

[UseCase1Test.kt](./src/test/kotlin/com/shoprunner/baleen/version2/UseCase1Test.kt)

## Use Case 2 - Given we have the data, we learn the schema and validate the data

[UseCase2Test.kt](./src/test/kotlin/com/shoprunner/baleen/version2/UseCase2Test.kt)

## Use Case 3 - Given a external schema (Avro, XSD, Json-Schema, Kotlin data class), build Baleen schema and validate the data

[UseCase3Test.kt](./src/test/kotlin/com/shoprunner/baleen/version2/UseCase3Test.kt)

## Use Case 4 - Given a Baleen schema, create external schema (Avro, XSD, Json-Schema, Kotlin data class) and validate the data with the external schema

[UseCase4Test.kt](./src/test/kotlin/com/shoprunner/baleen/version2/UseCase4Test.kt)