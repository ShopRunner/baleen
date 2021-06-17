# Change Log

All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

## [1.14.1] - 2021-06-17

Bug Fixes
- AssertThat<Any?>.isA should compile on non-null receivers

Other improvements
- Switch back to targeting jvm 1.8

## [1.14.0] - 2021-06-04

New Features 
- [Baleen-Poet](./baleen-poet/README.md) - Code generation from Baleen to Kotlin code.
- [baleen-jdbc](./baleen-db/baleen-jdbc/README.md) - Baleen validator for JDBC queries
- [Baleen Script](./baleen-script/README.md) - Run Baleen from Kotlin Scripts
- Generate ValidationSummary of all ValidationResults
- JUnit-style assertions and tests within Baleen Data Descriptions
- Anonymous Nested DataDescriptions
- Print ValidationResult to various formats: Console, Log, HTML, CSV, Text

Bug Fixes
- baleen-jsonschema-generator module needs dependencies exposed as api

Other improvements
- Switch Gradle scripts from Groovy to Kotlin
- Update to Gradle 7
- Update documentation for  XML
- Use Github native dependabot

## [1.13.0] - 2020-12-29
- Switch build to java 11 (LTS)

Dependency Updates
- avro 1.10.1
- assertj-core 3.18.1
- jackson 2.12.0
- jaxb 3.0
- junit 5.7.0
- kotlin 1.4.21
- kotlinpoet 1.7.2
- opencsv 5.2
- autoservice 1.0-rc7
- rxjava 2.2.20
- kotlinter 3.3.0
- dokka

## [1.12.0] - 2020-03-20
- Support ability to override errors into warnings
- Support adding tags to baleen types. Tags can be static strings or dynamic.
- Support overriding types during Json-Schema generations from Baleen.
- Created Base Baleen to X Generator to make Baleen to X generation easier. Used by baleen-jsonschema-generator.

Dependency Updates
- assertj-core 3.15.0
- avro 1.9.2
- dokka 0.10.1
- gradle 6.2.2
- junit 5.6
- kotlin 1.3.70
- kotlinter 2.3.1
- opencsv 5.1
- rxjava 2.2.19

## [1.11.2] - 2020-01-27
- Republished to fix module file java version.

## [1.11.1] - 2020-01-15
- Fix parsing XML with empty tags
- Update rxjava

## [1.11.0] - 2020-01-08

- Support generating Data Description for Kotlin Data Class
- NumericType and IntegerType

Dependency Updates
- rxjava 2.2.15
- rxkotlin 2.4.0
- kotlinter 2.2.0
- dokka 0.10.0
- kotlinpoet 1.4.4
- assertj 3.14.0
- avro 1.9.1
- opencsv 5.0
- kotlin 1.3.61
- Gradle 6.0.1


## [1.10.4] - 2019-07-17

Fix issed where occurences of XML elements containing text weren't being validated (https://github.com/ShopRunner/baleen/pull/51)

## [1.10.3] - 2019-07-16

Fix line aware handler for xml of occurrences (https://github.com/ShopRunner/baleen/pull/50)

## [1.10.2] - 2019-06-13

Add JCenter to the gradle repositories to fix Dokka dependency.

## [1.10.1] - 2019-06-13

* Upgrade avro to 1.9.0
* Upgrade Dokka to 0.9.18

## [1.10.0] - 2019-05-31

* Dependencies bump
* Fixed bug xsd generation so types can be overridden that are nested.

## [1.9.0] - 2019-05-28

Dependencies bump

## [1.8.0] - 2019-05-21

* Add support for adding tags to data traces.  This allows the capture of line number, column number or custom attributes in the data trace.

## [1.7.0] - 2019-01-21

* Upgrade build to gradle 5

## [1.6.0] - 2019-01-02

* Upgrade Kotlin to 1.3

## [1.5.0] - 2018-08-17

* Introduced generating JSON Schema's from Baleen Data Descriptions and vice-versa.
* Added support for building baleen.xml with versions of Java 9 and above.

## [1.4.1] - 2018-07-16

* Fix avro generation to use AllowsNull when the default value is null.

## [1.4.0] - 2018-07-11

* Introduced the concept of default values.  This makes the attribute optional and is used in code generation.

## [1.3.0] - 2018-07-02

* XSD generation now supports the map type.

## [1.2] - 2018-06-21

* Added method to datatrace to convert to list of strings.

## [1.1] - 2018-06-20

* Added support for converting Baleen Data Descriptions to and from Avro Schema (https://github.com/ShopRunner/baleen/pull/6)
* Added support for generating XML Schema Definitions (XSD) from Baleen Data Descriptions (https://github.com/ShopRunner/baleen/pull/5)

## [1.0] - 2018-06-12

* Initial Release
