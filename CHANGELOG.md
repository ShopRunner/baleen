# Change Log

All notable changes to this project will be documented in this file.
This project adheres to [Semantic Versioning](http://semver.org/).

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
