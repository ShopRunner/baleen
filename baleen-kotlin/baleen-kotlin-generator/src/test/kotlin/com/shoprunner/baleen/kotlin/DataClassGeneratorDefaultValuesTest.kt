package com.shoprunner.baleen.kotlin

import com.shoprunner.baleen.NoDefault
import com.shoprunner.baleen.kotlin.DataClassGenerator.addDefaultValue
import com.shoprunner.baleen.types.BooleanType
import com.shoprunner.baleen.types.EnumType
import com.shoprunner.baleen.types.InstantType
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.NumericType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringType
import com.shoprunner.baleen.types.TimestampMillisType
import com.squareup.kotlinpoet.CodeBlock
import java.time.Instant
import java.time.LocalDateTime
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataClassGeneratorDefaultValuesTest {

    @Test
    fun `test addDefaultValue with Nodefault return empty`() {
        val code = CodeBlock.builder().addDefaultValue(StringType(), NoDefault).build()

        Assertions.assertThat(code.toString()).isEqualTo("")
    }

    @Test
    fun `test addDefaultValue with null return null`() {
        val code = CodeBlock.builder().addDefaultValue(StringType(), null).build()

        Assertions.assertThat(code.toString()).isEqualTo("null")
    }

    @Test
    fun `test addDefaultValue with IntegerType return BigInteger`() {
        val code = CodeBlock.builder().addDefaultValue(IntegerType(), "100".toBigInteger()).build()

        Assertions.assertThat(code.toString()).isEqualTo("\"100\".toBigInteger()")
    }

    @Test
    fun `test addDefaultValue with NumericType return BigDecimal`() {
        val code = CodeBlock.builder().addDefaultValue(NumericType(), "100.0".toBigDecimal()).build()

        Assertions.assertThat(code.toString()).isEqualTo("\"100.0\".toBigDecimal()")
    }

    @Test
    fun `test addDefaultValue with OccurrencesType and empty list return emptyList`() {
        val code = CodeBlock.builder().addDefaultValue(OccurrencesType(StringType()), emptyList<String>()).build()

        Assertions.assertThat(code.toString()).isEqualTo("emptyList()")
    }

    @Test
    fun `test addDefaultValue with OccurrencesType and empty set return emptyList`() {
        val code = CodeBlock.builder().addDefaultValue(OccurrencesType(StringType()), emptySet<String>()).build()

        Assertions.assertThat(code.toString()).isEqualTo("emptyList()")
    }

    @Test
    fun `test addDefaultValue with OccurrencesType and list of Strings return list of Strings`() {
        val code = CodeBlock.builder().addDefaultValue(OccurrencesType(StringType()), listOf("hello", "world")).build()

        Assertions.assertThat(code.toString()).isEqualTo("listOf(\"hello\", \"world\")")
    }

    @Test
    fun `test addDefaultValue with OccurrencesType and set of strings return list of Strings`() {
        val code = CodeBlock.builder().addDefaultValue(OccurrencesType(StringType()), setOf("hello", "world")).build()

        Assertions.assertThat(code.toString()).isEqualTo("listOf(\"hello\", \"world\")")
    }

    @Test
    fun `test addDefaultValue when default value is a string`() {
        val code = CodeBlock.builder().addDefaultValue(StringType(), "hello world").build()

        Assertions.assertThat(code.toString()).isEqualTo("\"hello world\"")
    }

    @Test
    fun `test addDefaultValue when default value is a boolean`() {
        val code = CodeBlock.builder().addDefaultValue(BooleanType(), false).build()

        Assertions.assertThat(code.toString()).isEqualTo("false")
    }

    @Test
    fun `test addDefaultValue when default value is a int`() {
        val code = CodeBlock.builder().addDefaultValue(IntType(), false).build()

        Assertions.assertThat(code.toString()).isEqualTo("false")
    }

    @Test
    fun `test addDefaultValue when default value is a long`() {
        val code = CodeBlock.builder().addDefaultValue(LongType(), 100L).build()

        Assertions.assertThat(code.toString()).isEqualTo("100L")
    }

    @Test
    fun `test addDefaultValue when default value is a float`() {
        val code = CodeBlock.builder().addDefaultValue(LongType(), 1.1f).build()

        Assertions.assertThat(code.toString()).isEqualTo("1.1f")
    }

    @Test
    fun `test addDefaultValue when default value is a double`() {
        val code = CodeBlock.builder().addDefaultValue(LongType(), 1.1).build()

        Assertions.assertThat(code.toString()).isEqualTo("1.1")
    }

    enum class TestEnum {
        One, Two, Three
    }

    @Test
    fun `test addDefaultValue when default value is a Enum`() {
        val code = CodeBlock.builder().addDefaultValue(EnumType("Test Enum", TestEnum.values()), TestEnum.One).build()

        Assertions.assertThat(code.toString()).isEqualTo("\"One\"")
    }

    @Test
    fun `test addDefaultValue when default value is a Instant`() {
        val code = CodeBlock.builder().addDefaultValue(InstantType(), Instant.parse("2019-02-03T03:22:00Z")).build()

        Assertions.assertThat(code.toString()).isEqualTo("java.time.Instant.parse(\"2019-02-03T03:22:00Z\")")
    }

    @Test
    fun `test addDefaultValue when default value is a LocalDateTime`() {
        val code = CodeBlock.builder().addDefaultValue(TimestampMillisType(), LocalDateTime.parse("2019-02-03T03:22")).build()

        Assertions.assertThat(code.toString()).isEqualTo("java.time.LocalDateTime.parse(\"2019-02-03T03:22\")")
    }
}
