package com.shoprunner.baleen.xml

import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.DataValue
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.datawrappers.HashData
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.xml.ValidationAssert.Companion.assertThat
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class XmlUtilTest {

    private val dogDescription = Baleen.describe("Dog") { p ->
        p.attr(name = "name",
                type = StringType(),
                required = true)
    }

    private val pack = Baleen.describe("Pack") { p ->
        p.attr(name = "dog",
                type = OccurrencesType(dogDescription),
                required = true)
    }

    private val packContainer = Baleen.describe("PackContainer") { p ->
        p.attr(name = "pack",
                type = pack,
                required = true)
    }

    @Nested
    inner class MultipleOccurences {
        private val multipleOccurrences = """
            <pack>
                <dog>
                    <name>Fido</name>
                </dog>
                <dog>
                    <name>Doug</name>
                </dog>
            </pack>
            """

        @Test
        fun `produces data with context`() {
            val inputStream = multipleOccurrences.byteInputStream()
            val context = XmlUtil.fromXmlToContext(dataTrace("example.xml"), inputStream)
            assertThat(context.dataTrace).isEqualTo(dataTrace("example.xml"))
            val data = context.data
            assertThat(data.keys).isEqualTo(setOf("pack"))
        }

        @Test
        fun `returned data validates`() {
            val inputStream = multipleOccurrences.byteInputStream()
            val context = XmlUtil.fromXmlToContext(dataTrace("example.xml"), inputStream)
            assertThat(packContainer.validate(context)).isValid()
        }
    }

    @Nested
    inner class SingleOccurrence {

        private val singleOccurrence = """
            <pack>
                <dog>
                    <name>Doug</name>
                </dog>
            </pack>
            """

        @Test
        fun `produces data with context`() {
            val inputStream = singleOccurrence.byteInputStream()
            val context = XmlUtil.fromXmlToContext(dataTrace("example.xml"), inputStream)
            assertThat(context.dataTrace).isEqualTo(dataTrace("example.xml"))
            val data = context.data
            assertThat(data.keys).isEqualTo(setOf("pack"))
        }

        @Test
        fun `returned data validates`() {
            val inputStream = singleOccurrence.byteInputStream()
            val context = XmlUtil.fromXmlToContext(dataTrace("example.xml"), inputStream)
            assertThat(packContainer.validate(context)).isValid()
        }
    }

    @Nested
    inner class NilHandling {

        fun hashData(map: Map<String, Any?>) = HashData(map.map { (k, v) -> Pair(k, DataValue(v)) }.toMap())
        fun emptyHashData() = HashData(emptyMap())

        private val emptyElement = """
            <dog>
                <name xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>
            </dog>
            """

        @Test
        fun `produces data with context for namespace nil`() {
            val inputStream = emptyElement.byteInputStream()
            val context = XmlUtil.fromXmlToContext(dataTrace("example.xml"), inputStream)
            assertThat(context.dataTrace).isEqualTo(dataTrace("example.xml"))
            val data = context.data
            assertThat(data).isEqualTo(hashData(mapOf("dog" to hashData(mapOf("name" to null)))))
        }

        private val vanillaNilElement = """
            <dog>
                <name nil="true" />
            </dog>
            """

        @Test
        fun `produces data with context for nil without namespace`() {
            val inputStream = vanillaNilElement.byteInputStream()
            val context = XmlUtil.fromXmlToContext(dataTrace("example.xml"), inputStream)
            assertThat(context.dataTrace).isEqualTo(dataTrace("example.xml"))
            val data = context.data
            assertThat(data).isEqualTo(hashData(mapOf("dog" to hashData(mapOf("name" to null)))))
        }

        private val customNilElement = """
            <dog>
                <name xsi:nil="true" xmlns:xsi="http://custom_site"/>
            </dog>
            """

        @Test
        fun `processes data with context for custom nil`() {
            val inputStream = customNilElement.byteInputStream()
            val context = XmlUtil.fromXmlToContext(dataTrace("example.xml"), inputStream)
            assertThat(context.dataTrace).isEqualTo(dataTrace("example.xml"))
            val data = context.data
            assertThat(data).isEqualTo(hashData(mapOf("dog" to hashData(mapOf("name" to null)))))
        }
    }
}