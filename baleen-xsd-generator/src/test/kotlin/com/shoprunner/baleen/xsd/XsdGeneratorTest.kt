package com.shoprunner.baleen.xsd

import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.types.EnumType
import com.shoprunner.baleen.types.FloatType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.TimestampMillisType
import com.shoprunner.baleen.xsd.XsdGenerator.encode
import com.shoprunner.baleen.xsd.xml.MinInclusive
import com.shoprunner.baleen.xsd.xml.Restriction
import com.shoprunner.baleen.xsd.xml.SimpleType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import java.math.BigDecimal

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class XsdGeneratorTest {

    @Nested
    inner class Attributes {

        @Test
        fun `simple single attribute`() {
            val dogDescription = Baleen.describe("Dog",
                markdownDescription = "Type of dog.") {

                it.attr(name = "name",
                    type = StringType(),
                    markdownDescription = "Name of the dog.",
                    required = true)
            }

            val outputStream = ByteArrayOutputStream()
            dogDescription.encode(PrintStream(outputStream))

            assertThat(outputStream.toString()).isEqualToIgnoringWhitespace("""
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                    <xs:complexType name="Dog">
                        <xs:annotation>
                            <xs:documentation>Type of dog.</xs:documentation>
                        </xs:annotation>
                        <xs:sequence>
                            <xs:element name="name">
                                <xs:annotation>
                                    <xs:documentation>Name of the dog.</xs:documentation>
                                </xs:annotation>
                                <xs:simpleType>
                                    <xs:restriction base="xs:string">
                                        <xs:maxLength value="2147483647"/>
                                        <xs:minLength value="0"/>
                                    </xs:restriction>
                                </xs:simpleType>
                            </xs:element>
                        </xs:sequence>
                    </xs:complexType>
                    <xs:element name="Dog" type="Dog">
                        <xs:annotation>
                            <xs:documentation>Type of dog.</xs:documentation>
                        </xs:annotation>
                    </xs:element>
                </xs:schema>""")
        }

        @Test
        fun `not required`() {
            val dogDescription = Baleen.describe("Dog") {
                it.attr(name = "name",
                    type = StringType())
            }

            val outputStream = ByteArrayOutputStream()
            dogDescription.encode(PrintStream(outputStream))

            assertThat(outputStream.toString()).isEqualToIgnoringWhitespace("""
                <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                    <xs:complexType name="Dog">
                        <xs:sequence>
                            <xs:element minOccurs="0" name="name">
                                <xs:simpleType>
                                    <xs:restriction base="xs:string">
                                        <xs:maxLength value="2147483647"/>
                                        <xs:minLength value="0"/>
                                    </xs:restriction>
                                </xs:simpleType>
                            </xs:element>
                        </xs:sequence>
                    </xs:complexType>
                    <xs:element name="Dog" type="Dog"/>
                </xs:schema>""")
        }

        @Test
        fun `nested`() {
            val dogDescription = Baleen.describe("Dog") {
                it.attr(name = "name",
                    type = StringType(),
                    required = true)
            }

            val packOptionalAlpha = Baleen.describe("Pack") {
                it.attr(name = "alpha",
                    type = dogDescription,
                    required = false)
            }

            val outputStream = ByteArrayOutputStream()
            packOptionalAlpha.encode(PrintStream(outputStream))

            assertThat(outputStream.toString()).isEqualTo("""
                |<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                |<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                |    <xs:complexType name="Pack">
                |        <xs:sequence>
                |            <xs:element minOccurs="0" name="alpha" type="Dog"/>
                |        </xs:sequence>
                |    </xs:complexType>
                |    <xs:complexType name="Dog">
                |        <xs:sequence>
                |            <xs:element name="name">
                |                <xs:simpleType>
                |                    <xs:restriction base="xs:string">
                |                        <xs:maxLength value="2147483647"/>
                |                        <xs:minLength value="0"/>
                |                    </xs:restriction>
                |                </xs:simpleType>
                |            </xs:element>
                |        </xs:sequence>
                |    </xs:complexType>
                |    <xs:element name="Pack" type="Pack"/>
                |</xs:schema>
                |""".trimMargin())
        }
    }

    @Nested
    inner class Types {
        @Test
        fun `enum type`() {
            val dogDescription = Baleen.describe("Dog") {
                it.attr(
                    name = "spots",
                    type = EnumType("choice", "yes", "no"),
                    required = true
                )
            }

            val outputStream = ByteArrayOutputStream()
            dogDescription.encode(PrintStream(outputStream))

            assertThat(outputStream.toString()).isEqualTo("""
                |<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                |<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                |    <xs:complexType name="Dog">
                |        <xs:sequence>
                |            <xs:element name="spots">
                |                <xs:simpleType>
                |                    <xs:restriction base="xs:string">
                |                        <xs:enumeration value="yes"/>
                |                        <xs:enumeration value="no"/>
                |                    </xs:restriction>
                |                </xs:simpleType>
                |            </xs:element>
                |        </xs:sequence>
                |    </xs:complexType>
                |    <xs:element name="Dog" type="Dog"/>
                |</xs:schema>
                |""".trimMargin())
        }


        @Test
        fun `long type`() {
            val dogDescription = Baleen.describe("Dog") {
                it.attr(
                    name = "legs",
                    type = LongType(),
                    required = true
                )
            }

            val outputStream = ByteArrayOutputStream()
            dogDescription.encode(PrintStream(outputStream))

            assertThat(outputStream.toString()).isEqualTo("""
                |<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                |<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                |    <xs:complexType name="Dog">
                |        <xs:sequence>
                |            <xs:element name="legs">
                |                <xs:simpleType>
                |                    <xs:restriction base="xs:long">
                |                        <xs:maxInclusive value="9223372036854775807"/>
                |                        <xs:minInclusive value="-9223372036854775808"/>
                |                    </xs:restriction>
                |                </xs:simpleType>
                |            </xs:element>
                |        </xs:sequence>
                |    </xs:complexType>
                |    <xs:element name="Dog" type="Dog"/>
                |</xs:schema>
                |""".trimMargin())
        }

        @Test
        fun `float type`() {
            val dogDescription = Baleen.describe("Dog") {
                it.attr(
                    name = "weight",
                    type = FloatType(),
                    required = true
                )
            }

            val outputStream = ByteArrayOutputStream()
            dogDescription.encode(PrintStream(outputStream))

            assertThat(outputStream.toString()).isEqualTo("""
                |<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                |<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                |    <xs:complexType name="Dog">
                |        <xs:sequence>
                |            <xs:element name="weight">
                |                <xs:simpleType>
                |                    <xs:restriction base="xs:float"/>
                |                </xs:simpleType>
                |            </xs:element>
                |        </xs:sequence>
                |    </xs:complexType>
                |    <xs:element name="Dog" type="Dog"/>
                |</xs:schema>
                |""".trimMargin())
        }

        @Test
        fun `timestamp type`() {
            val dogDescription = Baleen.describe("Dog") {
                it.attr(
                    name = "birthday",
                    type = TimestampMillisType(),
                    required = true
                )
            }

            val outputStream = ByteArrayOutputStream()
            dogDescription.encode(PrintStream(outputStream))

            assertThat(outputStream.toString()).isEqualTo("""
                |<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                |<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                |    <xs:complexType name="Dog">
                |        <xs:sequence>
                |            <xs:element name="birthday" type="xs:dateTime"/>
                |        </xs:sequence>
                |    </xs:complexType>
                |    <xs:element name="Dog" type="Dog"/>
                |</xs:schema>
                |""".trimMargin())
        }
    }

    @Nested
    inner class CustomTypes {
        @Test
        fun `can handle custom type mappings`() {
            class DogRatingType : BaleenType {
                override fun name() = "dog rating"

                override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
                    when {
                        value == null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                        value !is Long -> sequenceOf(ValidationError(dataTrace, "is not a long", value))
                        value < 10 -> sequenceOf(ValidationError(dataTrace, "there good dogs, Brent", value))
                        else -> sequenceOf()
                    }
            }

            val dogDescription = Baleen.describe("Dog") {
                it.attr(
                    name = "rating",
                    type = DogRatingType(),
                    required = true
                )
            }

            fun customDogMapper(baleenType: BaleenType): TypeDetails =
                when (baleenType) {
                    is DogRatingType -> TypeDetails(simpleType = SimpleType(
                        Restriction(
                            minInclusive = MinInclusive(BigDecimal.TEN)
                    )))
                    else -> XsdGenerator.defaultTypeMapper(baleenType)
                }

            val outputStream = ByteArrayOutputStream()
            dogDescription.encode(PrintStream(outputStream), ::customDogMapper)

            assertThat(outputStream.toString()).isEqualTo("""
                |<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
                |<xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                |    <xs:complexType name="Dog">
                |        <xs:sequence>
                |            <xs:element name="rating">
                |                <xs:simpleType>
                |                    <xs:restriction>
                |                        <xs:minInclusive value="10"/>
                |                    </xs:restriction>
                |                </xs:simpleType>
                |            </xs:element>
                |        </xs:sequence>
                |    </xs:complexType>
                |    <xs:element name="Dog" type="Dog"/>
                |</xs:schema>
                |""".trimMargin())
        }
    }
}