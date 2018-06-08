package com.shoprunner.baleen.xsd

import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import java.io.ByteArrayOutputStream
import java.io.PrintStream

class XsdGeneratorTest {

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
        XsdGenerator.encode(dogDescription, PrintStream(outputStream))

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
        XsdGenerator.encode(dogDescription, PrintStream(outputStream))

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
        XsdGenerator.encode(packOptionalAlpha, PrintStream(outputStream))

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