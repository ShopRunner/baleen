package com.shoprunner.baleen.poet

import com.shoprunner.baleen.NoDefault
import com.shoprunner.baleen.types.EnumType
import com.shoprunner.baleen.types.InstantType
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.NumericType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.TimestampMillisType
import com.squareup.kotlinpoet.CodeBlock
import java.time.Instant
import java.time.LocalDateTime
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DefaultValueTest {
    @Test
    fun `test default value with NoDefault does nothing`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(StringType(), NoDefault)
            .build()

        assertThat(codeBlock.toString()).isEqualTo("")
    }

    @Test
    fun `test default value with null writes null`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(StringType(), null)
            .build()

        assertThat(codeBlock.toString()).isEqualTo("null")
    }

    @Test
    fun `test default value for IntegerType is a BigInteger`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(IntegerType(), "100".toBigInteger())
            .build()

        assertThat(codeBlock.toString()).isEqualTo("\"100\".toBigInteger()")
    }

    @Test
    fun `test default value for IntegerType is a BigInteger if int passed in`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(IntegerType(), 100)
            .build()

        assertThat(codeBlock.toString()).isEqualTo("\"100\".toBigInteger()")
    }

    @Test
    fun `test default value for NumericType is a BigDecimal`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(NumericType(), "100.0".toBigDecimal())
            .build()

        assertThat(codeBlock.toString()).isEqualTo("\"100.0\".toBigDecimal()")
    }

    @Test
    fun `test default value for NumericType is a BigDecimal if int passed in`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(NumericType(), 100)
            .build()

        assertThat(codeBlock.toString()).isEqualTo("\"100\".toBigDecimal()")
    }

    @Test
    fun `test default value for OccurrencesType is a list`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(OccurrencesType(StringType()), listOf("hello", "world"))
            .build()

        assertThat(codeBlock.toString()).isEqualTo("listOf(\"hello\", \"world\")")
    }

    @Test
    fun `test default value for OccurrencesType is a emptyList`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(OccurrencesType(StringType()), emptyList<Any>())
            .build()

        assertThat(codeBlock.toString()).isEqualTo("emptyList<Any?>()")
    }

    @Test
    fun `test default value for OccurrencesType of NumericType is a list of BigDecimals`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(OccurrencesType(NumericType()), listOf(1, 2))
            .build()

        assertThat(codeBlock.toString()).isEqualTo("listOf(\"1\".toBigDecimal(), \"2\".toBigDecimal())")
    }

    @Test
    fun `test default value for MapType is a map`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(MapType(StringType(), IntType()), mapOf("dog" to 1, "cat" to 0))
            .build()

        assertThat(codeBlock.toString()).isEqualTo("mapOf(\"dog\" to 1, \"cat\" to 0)")
    }

    @Test
    fun `test default value for MapType is a emptyMap`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(MapType(StringType(), IntType()), emptyMap<String, Int>())
            .build()

        assertThat(codeBlock.toString()).isEqualTo("emptyMap<Any?, Any?>()")
    }

    @Test
    fun `test default value is a string writes a string`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(StringType(), "Hello World")
            .build()

        assertThat(codeBlock.toString()).isEqualTo("\"Hello World\"")
    }

    @Test
    fun `test default value is an int writes an int`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(IntType(), 1)
            .build()

        assertThat(codeBlock.toString()).isEqualTo("1")
    }

    @Test
    fun `test default value is a long writes a long`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(IntType(), 1L)
            .build()

        assertThat(codeBlock.toString()).isEqualTo("1L")
    }

    @Test
    fun `test default value is a float writes a float`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(IntType(), 1.0f)
            .build()

        assertThat(codeBlock.toString()).isEqualTo("1.0f")
    }

    @Test
    fun `test default value is a double writes a double`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(IntType(), 1.0)
            .build()

        assertThat(codeBlock.toString()).isEqualTo("1.0")
    }

    @Test
    fun `test default value is a enum writes a string`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(EnumType("TestEnum", TestEnum.values()), TestEnum.One)
            .build()

        assertThat(codeBlock.toString()).isEqualTo("com.shoprunner.baleen.poet.TestEnum.One")
    }

    @Test
    fun `test default value is an Instant writes an Instant`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(InstantType(), Instant.parse("2020-01-01T01:01:01Z"))
            .build()

        assertThat(codeBlock.toString()).isEqualTo("java.time.Instant.parse(\"2020-01-01T01:01:01Z\")")
    }

    @Test
    fun `test default value is an LocalDateTime writes an LocalDateTime`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(TimestampMillisType(), LocalDateTime.parse("2020-01-01T01:01:01"))
            .build()

        assertThat(codeBlock.toString()).isEqualTo("java.time.LocalDateTime.parse(\"2020-01-01T01:01:01\")")
    }

    @Test
    fun `test default value is an class writes a class with empty constructor`() {
        val codeBlock = CodeBlock.builder()
            .addDefaultValue(StringType(), TestDefault())
            .build()

        assertThat(codeBlock.toString()).isEqualTo("com.shoprunner.baleen.poet.TestDefault()")
    }
}

data class TestDefault(val member: String = "Default")

enum class TestEnum {
    One
}
