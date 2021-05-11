package com.shoprunner.baleen

import com.shoprunner.baleen.Data.Companion.getAs
import com.shoprunner.baleen.TestHelper.dataOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.contracts.ExperimentalContracts
import org.assertj.core.api.Assertions.assertThat as junitAssertThat

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExperimentalContracts
internal class AssertThatTest {

    @Test
    fun `test assertThat Data has attribute`() {
        val data = dataOf(
            "value" to 1,
        )
        with(Assertions(dataTrace())) {
            assertThat(data)
                .hasAttribute("value") { attr ->
                    attr.isA<Int?>()
                        .isNotNull()
                }
                .hasAttribute("noValue") { attr ->
                    attr.isA<Int?>()
                        .isNotNull()
                }
            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] exists"), "Pass: data[value] exists", data),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is a kotlin.Int"), "Pass: data[value] is a kotlin.Int", 1),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is not null"), "Pass: data[value] is not null", 1),
                ValidationError(dataTrace().tag("assertion" to "data[noValue] exists"), "Fail: data[noValue] exists", data),
                ValidationError(dataTrace().tag("assertion" to "data[noValue] is a kotlin.Int"), "Fail: data[noValue] is a kotlin.Int", null),
                ValidationError(dataTrace().tag("assertion" to "data[noValue] is not null"), "Fail: data[noValue] is not null", null),
            )
        }
    }

    @Test
    fun `test assertThat with typed value returns AssertThatValue`() {
        with(Assertions(dataTrace())) {
            val assertThatValue = assertThat("hello world").isEqualTo("hello world")
            junitAssertThat(assertThatValue.actual).isEqualTo("hello world")

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "is equal to hello world"), "Pass: is equal to hello world", "hello world == hello world"),
            )
        }
    }

    @Test
    fun `test assertThat with getAs validates types`() {
        val data = dataOf(
            "value" to "true",
        )
        with(Assertions(dataTrace())) {
            assertThat { data.getAs<String>("value") }
            assertThat { data.getAs<Boolean>("value") }
            assertThat { data.getAs<String>("value").tryMap { it.toBoolean() } }
            assertThat { data.getAs<String>("value").tryMap { it.toInt() } }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "assertThat<kotlin.String>()"), "Pass: assertThat<kotlin.String>()", "true is a kotlin.String"),
                ValidationError(dataTrace().tag("assertion" to "assertThat<kotlin.Boolean>()"), "Fail: assertThat<kotlin.Boolean>()", "true is a kotlin.String"),
                ValidationInfo(dataTrace().tag("assertion" to "assertThat<kotlin.Boolean>()"), "Pass: assertThat<kotlin.Boolean>()", "true is a kotlin.Boolean"),
                ValidationError(dataTrace().tag("assertion" to "assertThat<kotlin.Int>()"), "Fail: assertThat<kotlin.Int>()", "true is a kotlin.String"),
            )
        }
    }

    @Test
    fun `test assertThat Boolean isTrue`() {
        val data = dataOf(
            "value1" to true,
            "value2" to false,
            "value3" to "Hello World",
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value1") { it.isA<Boolean>().isTrue() }
            assertThat(data).hasAttribute("value2") { it.isA<Boolean>().isTrue() }
            assertThat(data).hasAttribute("value3") { it.isA<Boolean>().isTrue() }
            assertThat(data).hasAttribute("valueNull") { it.isA<Boolean>().isTrue() }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value1] is true"), "Pass: data[value1] is true", true),
                ValidationError(dataTrace().tag("assertion" to "data[value2] is true"), "Fail: data[value2] is true", false),
                ValidationError(dataTrace().tag("assertion" to "data[value3] is true"), "Fail: data[value3] is true", "Hello World"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is true"), "Fail: data[valueNull] is true", null),
            )
        }
    }

    @Test
    fun `test assertThat Boolean isFalse`() {
        val data = dataOf(
            "value1" to true,
            "value2" to false,
            "value3" to "Hello World",
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value1") { it.isA<Boolean>().isFalse() }
            assertThat(data).hasAttribute("value2") { it.isA<Boolean>().isFalse() }
            assertThat(data).hasAttribute("value3") { it.isA<Boolean>().isFalse() }
            assertThat(data).hasAttribute("valueNull") { it.isA<Boolean>().isTrue() }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value1] is false"), "Fail: data[value1] is false", true),
                ValidationInfo(dataTrace().tag("assertion" to "data[value2] is false"), "Pass: data[value2] is false", false),
                ValidationError(dataTrace().tag("assertion" to "data[value3] is false"), "Fail: data[value3] is false", "Hello World"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is true"), "Fail: data[valueNull] is true", null),
            )
        }
    }

    @Test
    fun `test assertThat isEquals`() {
        val data = dataOf(
            "value1" to true,
            "value2" to "Hello World",
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value1") { it.isA<Boolean>().isEqualTo(true) }
            assertThat(data).hasAttribute("value1") { it.isA<Boolean>().isEqualTo(false) }
            assertThat(data).hasAttribute("value2") { it.isA<String>().isEqualTo("Hello World") }
            assertThat(data).hasAttribute("value2") { it.isA<String>().isEqualTo("Goodbye") }
            assertThat(data).hasAttribute("valueNull") { it.isA<String?>().isEqualTo("Hi") }
            assertThat(data).hasAttribute("valueNull") { it.isA<String>().isEqualTo("Hi") }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value1] is equal to true"), "Pass: data[value1] is equal to true", "true == true"),
                ValidationError(dataTrace().tag("assertion" to "data[value1] is equal to false"), "Fail: data[value1] is equal to false", "true == false"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value2] is equal to Hello World"), "Pass: data[value2] is equal to Hello World", "Hello World == Hello World"),
                ValidationError(dataTrace().tag("assertion" to "data[value2] is equal to Goodbye"), "Fail: data[value2] is equal to Goodbye", "Hello World == Goodbye"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is equal to Hi"), "Fail: data[valueNull] is equal to Hi", "null == Hi"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is equal to Hi"), "Fail: data[valueNull] is equal to Hi", "null == Hi"),
            )
        }
    }

    @Test
    fun `test assertThat isNotEquals`() {
        val data = dataOf(
            "value1" to true,
            "value2" to "Hello World",
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value1") { it.isA<Boolean>().isNotEqualTo(true) }
            assertThat(data).hasAttribute("value1") { it.isA<Boolean>().isNotEqualTo(false) }
            assertThat(data).hasAttribute("value2") { it.isA<String>().isNotEqualTo("Hello World") }
            assertThat(data).hasAttribute("value2") { it.isA<String>().isNotEqualTo("Goodbye") }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value1] is not equal to true"), "Fail: data[value1] is not equal to true", "true != true"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value1] is not equal to false"), "Pass: data[value1] is not equal to false", "true != false"),
                ValidationError(dataTrace().tag("assertion" to "data[value2] is not equal to Hello World"), "Fail: data[value2] is not equal to Hello World", "Hello World != Hello World"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value2] is not equal to Goodbye"), "Pass: data[value2] is not equal to Goodbye", "Hello World != Goodbye"),
            )
        }
    }

    @Test
    fun `test assertThat Int isLessThan`() {
        val data = dataOf(
            "value" to 1,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<Int>().isLessThan(1) }
            assertThat(data).hasAttribute("value") { it.isA<Int>().isLessThan(2) }
            assertThat(data).hasAttribute("valueNull") { it.isA<Int>().isLessThan(1) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is less than 1"), "Fail: data[value] is less than 1", "1 < 1"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than 2"), "Pass: data[value] is less than 2", "1 < 2"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than 1"), "Fail: data[valueNull] is less than 1", "null < 1"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than 1"), "Fail: data[valueNull] is less than 1", "null < 1"),
            )
        }
    }

    @Test
    fun `test assertThat Long isLessThan`() {
        val data = dataOf(
            "value" to 1L,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<Long>().isLessThan(1) }
            assertThat(data).hasAttribute("value") { it.isA<Long>().isLessThan(2) }
            assertThat(data).hasAttribute("valueNull") { it.isA<Long>().isLessThan(1) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is less than 1"), "Fail: data[value] is less than 1", "1 < 1"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than 2"), "Pass: data[value] is less than 2", "1 < 2"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than 1"), "Fail: data[valueNull] is less than 1", "null < 1"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than 1"), "Fail: data[valueNull] is less than 1", "null < 1"),
            )
        }
    }

    @Test
    fun `test assertThat Float isLessThan`() {
        val data = dataOf(
            "value" to 1.0f,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<Float>().isLessThan(1.0f) }
            assertThat(data).hasAttribute("value") { it.isA<Float>().isLessThan(2.0f) }
            assertThat(data).hasAttribute("valueNull") { it.isA<Float>().isLessThan(1.0f) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is less than 1.0"), "Fail: data[value] is less than 1.0", "1.0 < 1.0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than 2.0"), "Pass: data[value] is less than 2.0", "1.0 < 2.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than 1.0"), "Fail: data[valueNull] is less than 1.0", "null < 1.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than 1.0"), "Fail: data[valueNull] is less than 1.0", "null < 1.0"),
            )
        }
    }

    @Test
    fun `test assertThat Double isLessThan`() {
        val data = dataOf(
            "value" to 1.0,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<Double>().isLessThan(1.0) }
            assertThat(data).hasAttribute("value") { it.isA<Double>().isLessThan(2.0) }
            assertThat(data).hasAttribute("valueNull") { it.isA<Double>().isLessThan(1.0) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is less than 1.0"), "Fail: data[value] is less than 1.0", "1.0 < 1.0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than 2.0"), "Pass: data[value] is less than 2.0", "1.0 < 2.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than 1.0"), "Fail: data[valueNull] is less than 1.0", "null < 1.0"),
            )
        }
    }

    @Test
    fun `test assertThat Int isLessThanEquals`() {
        val data = dataOf(
            "value" to 1,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<Int>().isLessThanEquals(0) }
            assertThat(data).hasAttribute("value") { it.isA<Int>().isLessThanEquals(1) }
            assertThat(data).hasAttribute("value") { it.isA<Int>().isLessThanEquals(2) }
            assertThat(data).hasAttribute("valueNull") { it.isA<Int>().isLessThanEquals(1) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is less than equals 0"), "Fail: data[value] is less than equals 0", "1 <= 0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than equals 1"), "Pass: data[value] is less than equals 1", "1 <= 1"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than equals 2"), "Pass: data[value] is less than equals 2", "1 <= 2"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than equals 1"), "Fail: data[valueNull] is less than equals 1", "null <= 1"),
            )
        }
    }

    @Test
    fun `test assertThat Long isLessThanEquals`() {
        val data = dataOf(
            "value" to 1L,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<Long>().isLessThanEquals(0) }
            assertThat(data).hasAttribute("value") { it.isA<Long>().isLessThanEquals(1) }
            assertThat(data).hasAttribute("value") { it.isA<Long>().isLessThanEquals(2) }
            assertThat(data).hasAttribute("valueNull") { it.isA<Long>().isLessThanEquals(1) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is less than equals 0"), "Fail: data[value] is less than equals 0", "1 <= 0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than equals 1"), "Pass: data[value] is less than equals 1", "1 <= 1"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than equals 2"), "Pass: data[value] is less than equals 2", "1 <= 2"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than equals 1"), "Fail: data[valueNull] is less than equals 1", "null <= 1"),
            )
        }
    }

    @Test
    fun `test assertThat Float isLessThanEquals`() {
        val data = dataOf(
            "value" to 1.0f,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<Float>().isLessThanEquals(0.0f) }
            assertThat(data).hasAttribute("value") { it.isA<Float>().isLessThanEquals(1.0f) }
            assertThat(data).hasAttribute("value") { it.isA<Float>().isLessThanEquals(2.0f) }
            assertThat(data).hasAttribute("valueNull") { it.isA<Float>().isLessThanEquals(1.0f) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is less than equals 0.0"), "Fail: data[value] is less than equals 0.0", "1.0 <= 0.0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than equals 1.0"), "Pass: data[value] is less than equals 1.0", "1.0 <= 1.0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than equals 2.0"), "Pass: data[value] is less than equals 2.0", "1.0 <= 2.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than equals 1.0"), "Fail: data[valueNull] is less than equals 1.0", "null <= 1.0"),
            )
        }
    }

    @Test
    fun `test assertThat Double isLessThanEquals`() {
        val data = dataOf(
            "value" to 1.0,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<Double>().isLessThanEquals(0.0) }
            assertThat(data).hasAttribute("value") { it.isA<Double>().isLessThanEquals(1.0) }
            assertThat(data).hasAttribute("value") { it.isA<Double>().isLessThanEquals(2.0) }
            assertThat(data).hasAttribute("valueNull") { it.isA<Double>().isLessThanEquals(1.0) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is less than equals 0.0"), "Fail: data[value] is less than equals 0.0", "1.0 <= 0.0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than equals 1.0"), "Pass: data[value] is less than equals 1.0", "1.0 <= 1.0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than equals 2.0"), "Pass: data[value] is less than equals 2.0", "1.0 <= 2.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than equals 1.0"), "Fail: data[valueNull] is less than equals 1.0", "null <= 1.0"),
            )
        }
    }

    @Test
    fun `test assertThat Int isGreaterThan`() {
        val data = dataOf(
            "value" to 1,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<Int>().isGreaterThan(0) }
            assertThat(data).hasAttribute("value") { it.isA<Int>().isGreaterThan(1) }
            assertThat(data).hasAttribute("value") { it.isA<Int>().isGreaterThan(2) }
            assertThat(data).hasAttribute("valueNull") { it.isA<Int>().isGreaterThan(1) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than 0"), "Pass: data[value] is greater than 0", "1 > 0"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than 1"), "Fail: data[value] is greater than 1", "1 > 1"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than 2"), "Fail: data[value] is greater than 2", "1 > 2"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than 1"), "Fail: data[valueNull] is greater than 1", "null > 1"),
            )
        }
    }

    @Test
    fun `test assertThat Long isGreaterThan`() {
        val data = dataOf(
            "value" to 1L,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<Long>().isGreaterThan(0) }
            assertThat(data).hasAttribute("value") { it.isA<Long>().isGreaterThan(1) }
            assertThat(data).hasAttribute("value") { it.isA<Long>().isGreaterThan(2) }
            assertThat(data).hasAttribute("valueNull") { it.isA<Long>().isGreaterThan(1) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than 0"), "Pass: data[value] is greater than 0", "1 > 0"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than 1"), "Fail: data[value] is greater than 1", "1 > 1"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than 2"), "Fail: data[value] is greater than 2", "1 > 2"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than 1"), "Fail: data[valueNull] is greater than 1", "null > 1"),
            )
        }
    }

    @Test
    fun `test assertThat Float isGreaterThan`() {
        val data = dataOf(
            "value" to 1.0f,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<Float>().isGreaterThan(0.0f) }
            assertThat(data).hasAttribute("value") { it.isA<Float>().isGreaterThan(1.0f) }
            assertThat(data).hasAttribute("value") { it.isA<Float>().isGreaterThan(2.0f) }
            assertThat(data).hasAttribute("valueNull") { it.isA<Float>().isGreaterThan(1.0f) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than 0.0"), "Pass: data[value] is greater than 0.0", "1.0 > 0.0"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than 1.0"), "Fail: data[value] is greater than 1.0", "1.0 > 1.0"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than 2.0"), "Fail: data[value] is greater than 2.0", "1.0 > 2.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than 1.0"), "Fail: data[valueNull] is greater than 1.0", "null > 1.0"),
            )
        }
    }

    @Test
    fun `test assertThat Double isGreaterThan`() {
        val data = dataOf(
            "value" to 1.0,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<Double>().isGreaterThan(0.0) }
            assertThat(data).hasAttribute("value") { it.isA<Double>().isGreaterThan(1.0) }
            assertThat(data).hasAttribute("value") { it.isA<Double>().isGreaterThan(2.0) }
            assertThat(data).hasAttribute("valueNull") { it.isA<Double>().isGreaterThan(1.0) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than 0.0"), "Pass: data[value] is greater than 0.0", "1.0 > 0.0"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than 1.0"), "Fail: data[value] is greater than 1.0", "1.0 > 1.0"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than 2.0"), "Fail: data[value] is greater than 2.0", "1.0 > 2.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than 1.0"), "Fail: data[valueNull] is greater than 1.0", "null > 1.0"),
            )
        }
    }

    @Test
    fun `test assertThat Int isGreaterThanEquals`() {
        val data = dataOf(
            "value" to 1,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<Int>().isGreaterThanEquals(0) }
            assertThat(data).hasAttribute("value") { it.isA<Int>().isGreaterThanEquals(1) }
            assertThat(data).hasAttribute("value") { it.isA<Int>().isGreaterThanEquals(2) }
            assertThat(data).hasAttribute("valueNull") { it.isA<Int>().isGreaterThanEquals(1) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than equals 0"), "Pass: data[value] is greater than equals 0", "1 >= 0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than equals 1"), "Pass: data[value] is greater than equals 1", "1 >= 1"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than equals 2"), "Fail: data[value] is greater than equals 2", "1 >= 2"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than equals 1"), "Fail: data[valueNull] is greater than equals 1", "null >= 1"),
            )
        }
    }

    @Test
    fun `test assertThat Long isGreaterThanEquals`() {
        val data = dataOf(
            "value" to 1L,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<Long>().isGreaterThanEquals(0) }
            assertThat(data).hasAttribute("value") { it.isA<Long>().isGreaterThanEquals(1) }
            assertThat(data).hasAttribute("value") { it.isA<Long>().isGreaterThanEquals(2) }
            assertThat(data).hasAttribute("valueNull") { it.isA<Long>().isGreaterThanEquals(1) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than equals 0"), "Pass: data[value] is greater than equals 0", "1 >= 0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than equals 1"), "Pass: data[value] is greater than equals 1", "1 >= 1"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than equals 2"), "Fail: data[value] is greater than equals 2", "1 >= 2"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than equals 1"), "Fail: data[valueNull] is greater than equals 1", "null >= 1"),
            )
        }
    }

    @Test
    fun `test assertThat Float isGreaterThanEquals`() {
        val data = dataOf(
            "value" to 1.0f,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<Float>().isGreaterThanEquals(0.0f) }
            assertThat(data).hasAttribute("value") { it.isA<Float>().isGreaterThanEquals(1.0f) }
            assertThat(data).hasAttribute("value") { it.isA<Float>().isGreaterThanEquals(2.0f) }
            assertThat(data).hasAttribute("valueNull") { it.isA<Float>().isGreaterThanEquals(1.0f) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than equals 0.0"), "Pass: data[value] is greater than equals 0.0", "1.0 >= 0.0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than equals 1.0"), "Pass: data[value] is greater than equals 1.0", "1.0 >= 1.0"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than equals 2.0"), "Fail: data[value] is greater than equals 2.0", "1.0 >= 2.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than equals 1.0"), "Fail: data[valueNull] is greater than equals 1.0", "null >= 1.0"),
            )
        }
    }

    @Test
    fun `test assertThat Double isGreaterThanEquals`() {
        val data = dataOf(
            "value" to 1.0,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<Double>().isGreaterThanEquals(0.0) }
            assertThat(data).hasAttribute("value") { it.isA<Double>().isGreaterThanEquals(1.0) }
            assertThat(data).hasAttribute("value") { it.isA<Double>().isGreaterThanEquals(2.0) }
            assertThat(data).hasAttribute("valueNull") { it.isA<Double>().isGreaterThanEquals(1.0) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than equals 0.0"), "Pass: data[value] is greater than equals 0.0", "1.0 >= 0.0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than equals 1.0"), "Pass: data[value] is greater than equals 1.0", "1.0 >= 1.0"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than equals 2.0"), "Fail: data[value] is greater than equals 2.0", "1.0 >= 2.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than equals 1.0"), "Fail: data[valueNull] is greater than equals 1.0", "null >= 1.0"),
            )
        }
    }

    @Test
    fun `test assertThat Collection contains`() {
        val data = dataOf(
            "value" to listOf(1, 2, 3),
            "valueEmpty" to emptyList<Int>(),
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<List<Int>>().contains(1) }
            assertThat(data).hasAttribute("valueEmpty") { it.isA<List<Int>>().contains(1) }
            assertThat(data).hasAttribute("value") { it.isA<List<Int>>().contains(null) }
            assertThat(data).hasAttribute("valueNull") { it.isA<List<Int>>().contains(1) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] contains 1"), "Pass: data[value] contains 1", "1 in [1, 2, 3]"),
                ValidationError(dataTrace().tag("assertion" to "data[valueEmpty] contains 1"), "Fail: data[valueEmpty] contains 1", "1 in []"),
                ValidationError(dataTrace().tag("assertion" to "data[value] contains null"), "Fail: data[value] contains null", "null in [1, 2, 3]"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] contains 1"), "Fail: data[valueNull] contains 1", "1 in null"),
            )
        }
    }

    @Test
    fun `test assertThat isOneOf Collection`() {
        val collection = listOf(1, 2, 3)
        val emptyCollection = emptyList<Int>()
        val collectionWithNull = listOf<Int?>(null)
        val data = dataOf(
            "value" to 1,
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isOneOf(collection) }
            assertThat(data).hasAttribute("value") { it.isOneOf(emptyCollection) }
            assertThat(data).hasAttribute("valueNull") { it.isOneOf(collection) }
            assertThat(data).hasAttribute("valueNull") { it.isOneOf(collectionWithNull) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is one of [1, 2, 3]"), "Pass: data[value] is one of [1, 2, 3]", "1 in [1, 2, 3]"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is one of []"), "Fail: data[value] is one of []", "1 in []"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is one of [1, 2, 3]"), "Fail: data[valueNull] is one of [1, 2, 3]", "null in [1, 2, 3]"),
                ValidationInfo(dataTrace().tag("assertion" to "data[valueNull] is one of [null]"), "Pass: data[valueNull] is one of [null]", "null in [null]"),
            )
        }
    }

    @Test
    fun `test assertThat Collection notContains`() {
        val data = dataOf(
            "value" to listOf(1, 2, 3),
            "valueEmpty" to emptyList<Int>(),
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<List<Int>>().notContains(1) }
            assertThat(data).hasAttribute("valueEmpty") { it.isA<List<Int>>().notContains(1) }
            assertThat(data).hasAttribute("value") { it.isA<List<Int>>().notContains(null) }
            assertThat(data).hasAttribute("valueNull") { it.isA<List<Int>>().notContains(1) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] not contains 1"), "Fail: data[value] not contains 1", "1 not in [1, 2, 3]"),
                ValidationInfo(dataTrace().tag("assertion" to "data[valueEmpty] not contains 1"), "Pass: data[valueEmpty] not contains 1", "1 not in []"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] not contains null"), "Pass: data[value] not contains null", "null not in [1, 2, 3]"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] not contains 1"), "Fail: data[valueNull] not contains 1", "1 not in null"),
            )
        }
    }

    @Test
    fun `test assertThat Collection isEmpty`() {
        val data = dataOf(
            "value" to listOf(1, 2, 3),
            "valueEmpty" to emptyList<Int>(),
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<List<Int>>().isEmpty() }
            assertThat(data).hasAttribute("valueEmpty") { it.isA<List<Int>>().isEmpty() }
            assertThat(data).hasAttribute("valueNull") { it.isA<List<Int>>().isEmpty() }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is empty"), "Fail: data[value] is empty", listOf(1, 2, 3)),
                ValidationInfo(dataTrace().tag("assertion" to "data[valueEmpty] is empty"), "Pass: data[valueEmpty] is empty", emptyList<Int>()),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is empty"), "Fail: data[valueNull] is empty", null),
            )
        }
    }

    @Test
    fun `test assertThat Collection isNullOrEmpty`() {
        val data = dataOf(
            "value" to listOf(1, 2, 3),
            "valueEmpty" to emptyList<Int>(),
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<List<Int>?>().isNullOrEmpty() }
            assertThat(data).hasAttribute("valueEmpty") { it.isA<List<Int>?>().isNullOrEmpty() }
            assertThat(data).hasAttribute("valueNull") { it.isA<List<Int>?>().isNullOrEmpty() }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is null or empty"), "Fail: data[value] is null or empty", listOf(1, 2, 3)),
                ValidationInfo(dataTrace().tag("assertion" to "data[valueEmpty] is null or empty"), "Pass: data[valueEmpty] is null or empty", emptyList<Int>()),
                ValidationInfo(dataTrace().tag("assertion" to "data[valueNull] is null or empty"), "Pass: data[valueNull] is null or empty", null),
            )
        }
    }

    @Test
    fun `test assertThat Collection isNotEmpty`() {
        val data = dataOf(
            "value" to listOf(1, 2, 3),
            "valueEmpty" to emptyList<Int>(),
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<List<Int>>().isNotEmpty() }
            assertThat(data).hasAttribute("valueEmpty") { it.isA<List<Int>>().isNotEmpty() }
            assertThat(data).hasAttribute("valueNull") { it.isA<List<Int>?>().isNotEmpty() }
            assertThat(data).hasAttribute("valueNull") { it.isA<List<Int>>().isNotEmpty() }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is not empty"), "Pass: data[value] is not empty", listOf(1, 2, 3)),
                ValidationError(dataTrace().tag("assertion" to "data[valueEmpty] is not empty"), "Fail: data[valueEmpty] is not empty", emptyList<Int>()),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is not empty"), "Fail: data[valueNull] is not empty", null),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is not empty"), "Fail: data[valueNull] is not empty", null),
            )
        }
    }

    @Test
    fun `test assertThat Collection isSizeEquals`() {
        val data = dataOf(
            "value" to listOf(1, 2, 3),
            "valueEmpty" to emptyList<Int>(),
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isA<List<Int>>().isSizeEquals(3) }
            assertThat(data).hasAttribute("valueEmpty") { it.isA<List<Int>>().isSizeEquals(3) }
            assertThat(data).hasAttribute("valueNull") { it.isA<List<Int>?>().isSizeEquals(3) }
            assertThat(data).hasAttribute("valueNull") { it.isA<List<Int>>().isSizeEquals(3) }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is size equal to 3"), "Pass: data[value] is size equal to 3", "3 = size([1, 2, 3])"),
                ValidationError(dataTrace().tag("assertion" to "data[valueEmpty] is size equal to 3"), "Fail: data[valueEmpty] is size equal to 3", "3 = size([])"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is size equal to 3"), "Fail: data[valueNull] is size equal to 3", "3 = size(null)"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is size equal to 3"), "Fail: data[valueNull] is size equal to 3", "3 = size(null)"),
            )
        }
    }

    @Test
    fun `test assertThat isNull`() {
        val data = dataOf(
            "value" to "Hello World",
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isNull() }
            assertThat(data).hasAttribute("valueNull") { it.isNull() }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is null"), "Fail: data[value] is null", "Hello World"),
                ValidationInfo(dataTrace().tag("assertion" to "data[valueNull] is null"), "Pass: data[valueNull] is null", null),
            )
        }
    }

    @Test
    fun `test assertThat isNullOr`() {
        val data = dataOf(
            "value" to "Hello World",
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") {
                it.isNullOr {
                    it.isEqualTo("Hello World")
                }
            }
            assertThat(data).hasAttribute("valueNull") {
                it.isNullOr {
                    it.isEqualTo("Hello World")
                }
            }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is equal to Hello World"), "Pass: data[value] is equal to Hello World", "Hello World == Hello World"),
                ValidationInfo(dataTrace().tag("assertion" to "data[valueNull] is null"), "Pass: data[valueNull] is null", null),
            )
        }
    }

    @Test
    fun `test assertThat isNotNull`() {
        val data = dataOf(
            "value" to "Hello World",
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { it.isNotNull() }
            assertThat(data).hasAttribute("valueNull") { it.isNotNull() }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is not null"), "Pass: data[value] is not null", "Hello World"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is not null"), "Fail: data[valueNull] is not null", null),
            )
        }
    }

    @Test
    fun `test assertThat isA`() {
        val data = dataOf(
            "valueStr" to "Hello World",
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("valueStr") { it.isA<String>() }
            assertThat(data).hasAttribute("valueStr") { it.isA<Int>() }
            assertThat(data).hasAttribute("valueNull") { it.isA<String>() }
            assertThat(data).hasAttribute("valueNull") { it.isA<String?>() }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[valueStr] is a kotlin.String"), "Pass: data[valueStr] is a kotlin.String", "Hello World"),
                ValidationError(dataTrace().tag("assertion" to "data[valueStr] is a kotlin.Int"), "Fail: data[valueStr] is a kotlin.Int", "Hello World"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is a kotlin.String"), "Fail: data[valueNull] is a kotlin.String", null),
                ValidationInfo(dataTrace().tag("assertion" to "data[valueNull] is a kotlin.String"), "Pass: data[valueNull] is a kotlin.String", null),
            )
        }
    }

    @Test
    fun `test assertThat or`() {
        val data = dataOf(
            "value" to "Hello World",
            "valueNull" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat(data).hasAttribute("value") { attr ->
                attr.or(
                    { it.isNull() },
                    { it.isEqualTo("Hello World") }
                )
            }
            assertThat(data).hasAttribute("valueNull") { attr ->
                attr.or(
                    { it.isNull() },
                    { it.isEqualTo("Hello World") }
                )
            }
            assertThat(data).hasAttribute("value") { attr ->
                attr.or(
                    { it.isNull() },
                    { it.isEqualTo("Good Bye") }
                )
            }

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is equal to Hello World"), "Pass: data[value] is equal to Hello World", "Hello World == Hello World"),
                ValidationInfo(dataTrace().tag("assertion" to "data[valueNull] is null"), "Pass: data[valueNull] is null", null),
                ValidationError(dataTrace().tag("assertion" to "data[value] is null"), "Fail: data[value] is null", "Hello World"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is equal to Good Bye"), "Fail: data[value] is equal to Good Bye", "Hello World == Good Bye"),
            )
        }
    }
}
