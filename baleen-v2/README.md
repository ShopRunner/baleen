# Preparation for version 2

Do test driven development for version 2

```bash
./gradlew build -Pversion2
./gradlew :baleen-v2:build -Pversion2

```

## Use Case 1 - Given we have the data, we define the schema manually and validate the data

[UseCase1Test.kt](baleen-v2-tests/src/test/kotlin/com/shoprunner/baleen/version2/UseCase1Test.kt)

## Use Case 2 - Given we have the data, we learn the schema and validate the data

[UseCase2Test.kt](baleen-v2-tests/src/test/kotlin/com/shoprunner/baleen/version2/UseCase2Test.kt)

## Use Case 3 - Given a external schema (Avro, XSD, Json-Schema, Kotlin data class), build Baleen schema and validate the data

[UseCase3Test.kt](baleen-v2-tests/src/test/kotlin/com/shoprunner/baleen/version2/UseCase3Test.kt)

## Use Case 4 - Given a Baleen schema, create external schema (Avro, XSD, Json-Schema, Kotlin data class) and validate the data with the external schema

[UseCase4Test.kt](baleen-v2-tests/src/test/kotlin/com/shoprunner/baleen/version2/UseCase4Test.kt)

## Use Case 5 - Baleen is multiplatform and can be run on JVM, in Javascript from node and from the browser, and from Python via Cinteropt

[baleen-v2/build.gradle](baleen-v2/build.gradle)