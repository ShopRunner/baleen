package com.shoprunner.baleen.json

import com.fasterxml.jackson.core.JsonParseException
import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.Validation
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.ValidationWarning
import com.shoprunner.baleen.types.DoubleType
import com.shoprunner.baleen.types.FloatType
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.NumericType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class JsonUtilTest {

    /**
     * Returns a list of only errors
     */
    private fun Validation.errors(): List<ValidationResult> {
        return this.results.filter { it is ValidationError || it is ValidationWarning }.toList()
    }

    private fun Validation.eval() {
        this.results.toList()
    }

    @Nested
    inner class SingleValues {
        @Test
        fun `validate string`() {
            val type = StringType()
            val json = "\"string\""
            val validation = JsonUtil.validate(type, DataTrace("string"), json.byteInputStream())
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate int`() {
            val type = IntType()
            val json = "-1"
            val validation = JsonUtil.validate(type, DataTrace("int"), json.byteInputStream(), Options(integerOptions = IntegerOptions.INT))
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate long`() {
            val type = LongType()
            val json = "100000"
            val validation = JsonUtil.validate(type, DataTrace("long"), json.byteInputStream(), Options(integerOptions = IntegerOptions.LONG))
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate big integer`() {
            val type = IntegerType()
            val json = "100000"
            val validation = JsonUtil.validate(type, DataTrace("biginteger"), json.byteInputStream(), Options(integerOptions = IntegerOptions.BIG_INTEGER))
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate float`() {
            val type = FloatType()
            val json = "1.1"
            val validation = JsonUtil.validate(type, DataTrace("float"), json.byteInputStream(), Options(decimalOptions = DecimalOptions.FLOAT))
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate double`() {
            val type = DoubleType()
            val json = "10.11"
            val validation = JsonUtil.validate(type, DataTrace("double"), json.byteInputStream(), Options(decimalOptions = DecimalOptions.DOUBLE))
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate big decimal`() {
            val type = NumericType()
            val json = "100000.0"
            val validation = JsonUtil.validate(type, DataTrace("bigdecimal"), json.byteInputStream(), Options(decimalOptions = DecimalOptions.BIG_DECIMAL))
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate array`() {
            val type = OccurrencesType(NumericType())
            val json = "[ 1.1, 2.2, 3.3 ]"
            val validation = JsonUtil.validate(type, DataTrace("array"), json.byteInputStream())
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate map`() {
            val type = MapType(StringType(), StringType())
            val json = """
            {
                "key1": "value1",
                "key2": "value2"
            }
            """.trimIndent()
            val validation = JsonUtil.validate(type, DataTrace("map"), json.byteInputStream())
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate simple object`() {
            val dogType = "dog".describeAs {
                attr("name", StringType())
                attr("numLegs", IntegerType())
            }

            val json = """
            {
                "name": "Fido",
                "numLegs": 4
            }""".trimIndent()

            val validation = JsonUtil.validate(dogType, DataTrace("dog"), json.byteInputStream())
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate nested object`() {
            val dogType = "dog".describeAs {
                attr("name", StringType())
                attr("numLegs", IntegerType())
            }

            val packType = "pack".describeAs {
                attr("name", StringType())
                attr("members", OccurrencesType(dogType))
            }

            val json = """
            {
                "name": "Wolfpack",
                "members": [
                    {
                        "name": "Fido",
                        "numLegs": 4
                    },
                    {
                        "name": "Dug",
                        "numLegs": 4
                    }
                ]
            }""".trimIndent()

            val validation = JsonUtil.validate(packType, DataTrace("pack"), json.byteInputStream())
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate non-json throws an JsonParseException`() {
            val json = "bad json"

            val e = assertThrows<JsonParseException> {
                JsonUtil.validate(StringType(), DataTrace("string"), json.byteInputStream())
            }
            assertThat(e.originalMessage).isEqualTo("Unrecognized token 'bad': was expecting ('true', 'false' or 'null')")
            assertThat(e.location.lineNr).isEqualTo(1)
            assertThat(e.location.columnNr).isEqualTo(5)
        }

        @Test
        fun `validate mal-formed json throws an JsonParseException`() {
            val json = """
            {
                "key": "unexpected comma",
            }
            """.trimIndent()

            val objType = "obj".describeAs {
                attr("key", StringType())
            }

            val e = assertThrows<JsonParseException> {
                JsonUtil.validate(objType, DataTrace("string"), json.byteInputStream())
            }
            assertThat(e.originalMessage).isEqualTo("Unexpected character ('}' (code 125)): was expecting double-quote to start field name")
            assertThat(e.location.lineNr).isEqualTo(3)
            assertThat(e.location.columnNr).isEqualTo(2)
        }

        @Test
        fun `validate primitive correctly fails with line and column numbers`() {
            val json = "-1"
            val validation = JsonUtil.validate(NumericType(min = 0.toBigDecimal()), DataTrace("dog"), json.byteInputStream())
            assertThat(validation.errors()).contains(
                ValidationError(
                    DataTrace("dog")
                        .tag("line", "1").tag("column", "5"),
                    "is less than 0",
                    -1.toBigInteger()
                ))
        }

        @Test
        fun `validate object correctly fails with line and column numbers`() {
            val dogType = "dog".describeAs {
                attr("name", StringType())
                attr("numLegs", IntType())
            }

            val json = """
            {
                "name": "Fido",
                "numLegs": "wrong"
            }""".trimIndent()

            val validation = JsonUtil.validate(dogType, DataTrace("dog"), json.byteInputStream())
            assertThat(validation.errors()).contains(
                ValidationError(
                    DataTrace("dog", "attribute \"numLegs\"")
                        .tag("line", "3").tag("column", "17"),
                    "is not an Int",
                    "wrong"
                ))
        }

        @Test
        fun `validate on arrays correctly fails with line and column numbers`() {
            val dogType = "dog".describeAs {
                attr("name", StringType())
                attr("numLegs", IntType())
            }

            val json = """
            [{
                "name": "Fido",
                "numLegs": "wrong"
            }]""".trimIndent()

            val validation = JsonUtil.validate(OccurrencesType(dogType), DataTrace("dogs"), json.byteInputStream())
            assertThat(validation.errors()).contains(
                ValidationError(
                    DataTrace("dogs", "index 0", "attribute \"numLegs\"")
                        .tag("line", "3").tag("column", "17"),
                    "is not an Int",
                    "wrong"
                ))
        }
    }

    @Nested
    inner class LargeArray {

        @Test
        fun `validate an array of ints`() {
            val json = """
            [
                1,
                2,
                3,
                4,
                5
            ]
            """.trimIndent()

            val validation = JsonUtil.validateRootJsonArray(NumericType(), DataTrace("array of ints"), json.byteInputStream())
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate an array of strings`() {
            val json = """
            [
                "hello 1",
                "hello 2",
                "hello 3",
                "hello 4",
                "hello 5"
            ]
            """.trimIndent()

            val validation = JsonUtil.validateRootJsonArray(StringType(), DataTrace("array ofstrings"), json.byteInputStream())
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate an array of objects`() {
            val json = """
            [
                { "key": 1 },
                { "key": 2 },
                { "key": 3 },
                { "key": 4 },
                { "key": 5 }
            ]
            """.trimIndent()

            val objType = "obj".describeAs {
                attr("key", NumericType())
            }

            val validation = JsonUtil.validateRootJsonArray(objType, DataTrace("array of objects"), json.byteInputStream())
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate a nested array`() {
            val json = """
            [
                [ 1, 2, 3 ],
                [ 4, 5, 6 ],
                [ 7, 8, 9 ],
                [ 10, 11, 12 ],
                [ 13, 14, 15 ]
            ]
            """.trimIndent()

            val validation = JsonUtil.validateRootJsonArray(OccurrencesType(NumericType()), DataTrace("array of arrays"), json.byteInputStream())
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate correctly fails with line and column numbers`() {
            val dogType = "dog".describeAs {
                attr("name", StringType())
                attr("numLegs", IntType())
            }

            val json = """
            [{
                "name": "Fido",
                "numLegs": "wrong"
            }]""".trimIndent()

            val validation = JsonUtil.validateRootJsonArray(dogType, DataTrace("dogs"), json.byteInputStream())
            assertThat(validation.errors()).contains(
                ValidationError(
                    DataTrace("dogs", "index 0", "attribute \"numLegs\"")
                        .tag("line", "3").tag("column", "17"),
                    "is not an Int",
                    "wrong"
                ))
        }

        @Test
        fun `validate correctly fails when not an array`() {
            val json = "\"not an array\""

            val validation = JsonUtil.validateRootJsonArray(StringType(), DataTrace("array"), json.byteInputStream())
            assertThat(validation.errors()).contains(
                ValidationError(
                    DataTrace("array")
                        .tag("line", "1").tag("column", "1"),
                    "is not an array",
                    null
                ))
        }

        @Test
        fun `validate non-json throws an JsonParseException`() {
            val json = "bad json"

            val e = assertThrows<JsonParseException> {
                JsonUtil.validateRootJsonArray(StringType(), DataTrace("string"), json.byteInputStream())
            }
            assertThat(e.originalMessage).isEqualTo("Unrecognized token 'bad': was expecting ('true', 'false' or 'null')")
            assertThat(e.location.lineNr).isEqualTo(1)
            assertThat(e.location.columnNr).isEqualTo(5)
        }

        @Test
        fun `validate mal-formed json throws an JsonParseException`() {
            val json = """
            [{
                "key": "unexpected comma",
            }]
            """.trimIndent()

            val objType = "obj".describeAs {
                attr("key", StringType())
            }

            val e = assertThrows<JsonParseException> {
                JsonUtil.validateRootJsonArray(objType, DataTrace("string"), json.byteInputStream()).eval()
            }
            assertThat(e.originalMessage).isEqualTo("Unexpected character ('}' (code 125)): was expecting double-quote to start field name")
            assertThat(e.location.lineNr).isEqualTo(3)
            assertThat(e.location.columnNr).isEqualTo(2)
        }
    }

    @Nested
    inner class Stream {
        @Test
        fun `validate an stream of ints`() {
            val json = """
            1
            2
            3
            4
            5
            """.trimIndent()

            val validation = JsonUtil.validateJsonStream(NumericType(), DataTrace("stream of ints"), json.byteInputStream())
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate an stream of strings`() {
            val json = """
            "hello 1"
            "hello 2"
            "hello 3"
            "hello 4"
            "hello 5"
            """.trimIndent()

            val validation = JsonUtil.validateJsonStream(StringType(), DataTrace("stream of strings"), json.byteInputStream())
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate an stream of objects`() {
            val json = """
            { "key": 1 }
            { "key": 2 }
            { "key": 3 }
            { "key": 4 }
            { "key": 5 }
            """.trimIndent()

            val objType = "obj".describeAs {
                attr("key", NumericType())
            }

            val validation = JsonUtil.validateJsonStream(objType, DataTrace("stream of objects"), json.byteInputStream())
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate a stream of array`() {
            val json = """
            [ 1, 2, 3 ]
            [ 4, 5, 6 ]
            [ 7, 8, 9 ]
            [ 10, 11, 12 ]
            [ 13, 14, 15 ]
            """.trimIndent()

            val validation = JsonUtil.validateJsonStream(OccurrencesType(NumericType()), DataTrace("stream of arrays"), json.byteInputStream())
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate empty stream`() {
            val json = ""

            val validation = JsonUtil.validateJsonStream(OccurrencesType(NumericType()), DataTrace("empty stream"), json.byteInputStream())
            assertThat(validation.results).isEmpty()
        }

        @Test
        fun `validate correctly fails with line and column numbers`() {
            val dogType = "dog".describeAs {
                attr("name", StringType())
                attr("numLegs", NumericType())
            }

            val json = """
            { "name": "Fido", "numLegs": 4 }
            { "name": "Dug", "numLegs": "wrong" }
            """.trimIndent()

            val validation = JsonUtil.validateJsonStream(dogType, DataTrace("dogs"), json.byteInputStream())
            assertThat(validation.errors()).contains(
                ValidationError(
                    DataTrace("dogs", "index 1", "attribute \"numLegs\"")
                        .tag("line", "1").tag("column", "30"),
                    "is not a number",
                    "wrong"
                ))
        }

        @Test
        fun `validate non-json throws an JsonParseException`() {
            val json = "bad json"

            val e = assertThrows<JsonParseException> {
                JsonUtil.validateJsonStream(StringType(), DataTrace("string"), json.byteInputStream()).eval()
            }
            assertThat(e.originalMessage).isEqualTo("Unrecognized token 'bad': was expecting ('true', 'false' or 'null')")
            assertThat(e.location.lineNr).isEqualTo(1)
            assertThat(e.location.columnNr).isEqualTo(5)
        }

        @Test
        fun `validate mal-formed json throws an JsonParseException`() {
            val json = """
            { "key": "unexpected comma", }
            """.trimIndent()

            val objType = "obj".describeAs {
                attr("key", StringType())
            }

            val e = assertThrows<JsonParseException> {
                JsonUtil.validateJsonStream(objType, DataTrace("string"), json.byteInputStream()).eval()
            }
            assertThat(e.originalMessage).isEqualTo("Unexpected character ('}' (code 125)): was expecting double-quote to start field name")
            assertThat(e.location.lineNr).isEqualTo(1)
            assertThat(e.location.columnNr).isEqualTo(31)
        }

        @Test
        fun `validate an stream with a different delimiter`() {
            val json = """{ "key": 1 }|{ "key": 2 }|{ "key": 3 }|{ "key": 4 }|{ "key": 5 }"""

            val objType = "obj".describeAs {
                attr("key", NumericType())
            }

            val validation = JsonUtil.validateJsonStream(objType, DataTrace("stream of pipe objects"), json.byteInputStream(), recordDelimiter = "\\|".toPattern())
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate a multiline stream with a different delimiter`() {
            val json = """
            {
                "key": 1
            }
            |
            {
                "key": 2
            }
            |
            {
                "key": 3
            }
            |
            {
                "key": 4
            }
            |
            {
                "key": 5
            }
            """.trimIndent()

            val objType = "obj".describeAs {
                attr("key", NumericType())
            }

            val validation = JsonUtil.validateJsonStream(objType, DataTrace("stream of pipe objects"), json.byteInputStream(), recordDelimiter = "\\|".toPattern())
            assertThat(validation.errors()).isEmpty()
        }

        @Test
        fun `validate fails correctly an stream with a different delimiter`() {
            val json = """{ "key": 1 }|{ "key": "2" }|{ "key": 3 }|{ "key": "4" }|{ "key": 5 }"""

            val objType = "obj".describeAs {
                attr("key", IntType())
            }

            val validation = JsonUtil.validateJsonStream(objType, DataTrace("stream of pipe objects"), json.byteInputStream(), recordDelimiter = "\\|".toPattern())
            assertThat(validation.errors()).contains(
                ValidationError(
                    DataTrace("stream of pipe objects", "index 1", "attribute \"key\"")
                        .tag("line", "1").tag("column", "11"),
                    "is not an Int",
                    "2"
                ),
                ValidationError(
                    DataTrace("stream of pipe objects", "index 3", "attribute \"key\"")
                        .tag("line", "1").tag("column", "11"),
                    "is not an Int",
                    "4"
                )
            )
        }
    }
}
