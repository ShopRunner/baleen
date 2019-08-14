package com.shoprunner.baleen.xml

import com.shoprunner.baleen.Baleen
import com.shoprunner.baleen.Context
import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataValue
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationSuccess
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

    @Test
    fun `attributeDataValue handles text nodes`() {
        val inputStream = """
            <pack>
                <dog>Doug</dog>
            </pack>
            """.trimIndent().byteInputStream()

        val context = XmlUtil.fromXmlToContext(dataTrace(), inputStream)
        val data = context.data.attributeDataValue("pack", dataTrace()).value as Data
        assertThat(data.attributeDataValue("dog", dataTrace()))
            .isEqualTo(
                DataValue(
                    value = "Doug",
                    dataTrace = dataTrace("attribute \"dog\"")
                        .tag("line", "2")
                        .tag("column", "10"))
                )
    }

    @Test
    fun `attributeDataValue handles node attributes`() {
        val inputStream = """
            <pack dog="Doug">
            </pack>
            """.trimIndent().byteInputStream()

        val context = XmlUtil.fromXmlToContext(dataTrace(), inputStream)
        val data = context.data.attributeDataValue("pack", dataTrace()).value as Data
        assertThat(data.attributeDataValue("dog", dataTrace()))
            .isEqualTo(
                DataValue(
                    value = "Doug",
                    dataTrace = dataTrace("attribute \"dog\"")
                        .tag("line", "1")
                        .tag("column", "18"))
            )
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
            """.trimIndent()

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

        @Test
        fun `returned data validation returns the nested list`() {
            val inputStream = multipleOccurrences.byteInputStream()
            val context = XmlUtil.fromXmlToContext(dataTrace("example.xml"), inputStream)

            val fidoLeaf = XmlDataLeaf(value = "Fido", line = 3, column = 15)
            val fidoNameNode = XmlDataNode(2, 10).apply { hash["name"] = fidoLeaf }
            val dougLeaf = XmlDataLeaf(value = "Doug", line = 6, column = 15)
            val dougNameNode = XmlDataNode(5, 10).apply { hash["name"] = dougLeaf }
            val dogLeaf = XmlDataLeaf(value = listOf(fidoNameNode, dougNameNode), line = 2, column = 10)

            val packNode = XmlDataNode(1, 7).apply { hash["dog"] = dogLeaf }

            val rootNode = XmlDataNode().apply { hash["pack"] = packNode }

            val validation = packContainer.validate(context)
            val results = validation.results.toList()

            assertThat(validation.context).isEqualTo(context)

            assertThat(results).hasSize(5)

            assertThat(results[0]).isEqualTo(ValidationInfo(
                dataTrace = dataTrace("example.xml"),
                message = "has attribute \"pack\"",
                value = rootNode
            ))

            assertThat(results[1]).isEqualTo(ValidationInfo(
                dataTrace = dataTrace("example.xml", "attribute \"pack\"").tag("line", "1").tag("column", "7"),
                message = "has attribute \"dog\"",
                value = packNode
            ))

            assertThat(results[2]).isEqualTo(ValidationInfo(
                dataTrace = dataTrace("example.xml", "attribute \"pack\"", "attribute \"dog\"", "index 0").tag("line", "2").tag("column", "10"),
                message = "has attribute \"name\"",
                value = fidoNameNode
            ))

            assertThat(results[3]).isEqualTo(ValidationInfo(
                dataTrace = dataTrace("example.xml", "attribute \"pack\"", "attribute \"dog\"", "index 1").tag("line", "2").tag("column", "10"),
                message = "has attribute \"name\"",
                value = dougNameNode
            ))

            assertThat(results[4]).isEqualTo(ValidationSuccess(
                dataTrace = dataTrace("example.xml"),
                value = rootNode
            ))
        }

        @Test
        fun `can validate a list of strings`() {
            val pack = Baleen.describe("Pack") { p ->
                p.attr(name = "dog",
                    type = OccurrencesType(StringType(max = 3)),
                    required = true)
            }
            val packContainer = Baleen.describe("PackContainer") { p ->
                p.attr(name = "pack",
                    type = pack,
                    required = true)
            }

            val multipleOccurrences = """
            <pack>
                <dog>Dug</dog>
                <dog>Fido</dog>
                <dog>Bo</dog>
            </pack>
            """.trimIndent()

            val dogLeaf = XmlDataLeaf(value = listOf("Dug", "Fido", "Bo"), line = 2, column = 7)

            val packNode = XmlDataNode(1, 7).apply { hash["dog"] = dogLeaf }
            val rootNode = XmlDataNode().apply { hash["pack"] = packNode }

            val inputStream = multipleOccurrences.byteInputStream()
            val context = XmlUtil.fromXmlToContext(dataTrace("example.xml"), inputStream)

            val validation = packContainer.validate(context)
            val results = validation.results.toList()

            assertThat(validation.context).isEqualTo(context)

            assertThat(results).hasSize(3)

            assertThat(results[0]).isEqualTo(ValidationInfo(
                dataTrace = dataTrace("example.xml"),
                message = "has attribute \"pack\"",
                value = rootNode
            ))

            assertThat(results[1]).isEqualTo(ValidationInfo(
                dataTrace = dataTrace("example.xml", "attribute \"pack\"").tag("line", "1").tag("column", "7"),
                message = "has attribute \"dog\"",
                value = packNode
            ))

            assertThat(results[2]).isEqualTo(ValidationError(
                dataTrace = dataTrace("example.xml", "attribute \"pack\"", "attribute \"dog\"", "index 1").tag("line", "2").tag("column", "10"),
                message = "is more than 3 characters",
                value = "Fido"
            ))
        }

        @Test
        fun `data trace for root is correct`() {
            val inputStream = multipleOccurrences.byteInputStream()
            val context = XmlUtil.fromXmlToContext(dataTrace("example.xml"), inputStream)
            assertThat(context.dataTrace).isEqualTo(dataTrace("example.xml"))
        }

        @Test
        fun `data trace for child tag is correct`() {
            val inputStream = multipleOccurrences.byteInputStream()
            val context = XmlUtil.fromXmlToContext(dataTrace("example.xml"), inputStream)
            assertThat(context.data.attributeDataValue("pack", dataTrace("someFile")).dataTrace)
                .isEqualTo(
                    dataTrace("someFile", "attribute \"pack\"")
                        .tag("line", "1")
                        .tag("column", "7"))
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

        private val emptyElement = """
            <dog>
                <name xsi:nil="true" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"/>
            </dog>
            """.trimIndent()

        @Test
        fun `produces data with context for namespace nil`() {
            val inputStream = emptyElement.byteInputStream()
            val context = XmlUtil.fromXmlToContext(dataTrace("example.xml"), inputStream)
            assertThat(context.dataTrace).isEqualTo(dataTrace("example.xml"))
            val data = context.data
            assertThat(data).isEqualTo(HashData(mapOf("dog" to HashData(mapOf("name" to null)))))
        }

        private val vanillaNilElement = """
            <dog>
                <name nil="true" />
            </dog>
            """.trimIndent()

        @Test
        fun `produces data with context for nil without namespace`() {
            val inputStream = vanillaNilElement.byteInputStream()
            val context = XmlUtil.fromXmlToContext(dataTrace("example.xml"), inputStream)
            assertThat(context).isEqualTo(
                Context(
                    data = HashData(mapOf("dog" to HashData(mapOf("name" to null)))),
                    dataTrace = dataTrace("example.xml")))
        }

        private val customNilElement = """
            <dog>
                <name xsi:nil="true" xmlns:xsi="http://custom_site"/>
            </dog>
            """.trimIndent()

        @Test
        fun `doesn't produce data for custom nil`() {
            val inputStream = customNilElement.byteInputStream()
            val context = XmlUtil.fromXmlToContext(dataTrace("example.xml"), inputStream)
            assertThat(context.dataTrace).isEqualTo(dataTrace("example.xml"))
            val data = context.data
            assertThat(data).isEqualTo(HashData(mapOf("dog" to HashData(mapOf("name" to null)))))
        }
    }
}
