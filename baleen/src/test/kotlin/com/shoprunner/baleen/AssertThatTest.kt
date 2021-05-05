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
    fun `test assertThat with typed value returns AssertThatValue`() {
        with(Assertions(dataTrace())) {
            val assertThatValue = assertThat("hello world").isNotNull()
            junitAssertThat(assertThatValue.actual).isEqualTo("hello world")

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "is not null"), "Pass: is not null", "hello world"),
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
    fun `test assertThat validates types`() {
        val data = dataOf(
            "value" to true,
        )
        with(Assertions(dataTrace())) {
            assertThat<Boolean>(data, "value")
            assertThat<String>(data, "value")

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "assertThat<kotlin.Boolean>(data[value])"), "Pass: assertThat<kotlin.Boolean>(data[value])", "true is a kotlin.Boolean"),
                ValidationError(dataTrace().tag("assertion" to "assertThat<kotlin.String>(data[value])"), "Fail: assertThat<kotlin.String>(data[value])", "true is a kotlin.Boolean"),
            )
        }
    }

    @Test
    fun `test assertThat validate instance of nullable type`() {
        val data = dataOf(
            "value" to null,
        )
        with(Assertions(dataTrace())) {
            assertThat<Boolean?>(data, "value")
            assertThat<Boolean>(data, "value")

            junitAssertThat(this.results.toList()).contains(
                ValidationInfo(dataTrace().tag("assertion" to "assertThat<kotlin.Boolean>(data[value])"), "Pass: assertThat<kotlin.Boolean>(data[value])", "null is a kotlin.Boolean?"),
                ValidationError(dataTrace().tag("assertion" to "assertThat<kotlin.Boolean>(data[value])"), "Fail: assertThat<kotlin.Boolean>(data[value])", "null is a kotlin.Boolean?"),
            )
        }
    }

    @Test
    fun `test assertThat Boolean isTrue`() {
        val data = dataOf(
            "value1" to true,
            "value2" to false,
            "value3" to "Hello World"
        )
        with(Assertions(dataTrace())) {
            assertThat<Boolean>(data, "value1").isTrue()
            assertThat<Boolean>(data, "value2").isTrue()
            assertThat<Boolean>(data, "value3").isTrue()
            assertThat<Boolean?>(data, "valueNull").isTrue()
            assertThat<Boolean>(data, "valueNull").isTrue()

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value1] is true"), "Pass: data[value1] is true", true),
                ValidationError(dataTrace().tag("assertion" to "data[value2] is true"), "Fail: data[value2] is true", false),
                ValidationError(dataTrace().tag("assertion" to "data[value3] is true"), "Fail: data[value3] is true", "Hello World"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is true"), "Fail: data[valueNull] is true", null),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is true"), "Fail: data[valueNull] is true", null),
            )
        }
    }

    @Test
    fun `test assertThat Boolean isFalse`() {
        val data = dataOf(
            "value1" to true,
            "value2" to false,
            "value3" to "Hello World"
        )
        with(Assertions(dataTrace())) {
            assertThat<Boolean>(data, "value1").isFalse()
            assertThat<Boolean>(data, "value2").isFalse()
            assertThat<Boolean>(data, "value3").isFalse()
            assertThat<Boolean?>(data, "valueNull").isTrue()
            assertThat<Boolean>(data, "valueNull").isTrue()

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value1] is false"), "Fail: data[value1] is false", true),
                ValidationInfo(dataTrace().tag("assertion" to "data[value2] is false"), "Pass: data[value2] is false", false),
                ValidationError(dataTrace().tag("assertion" to "data[value3] is false"), "Fail: data[value3] is false", "Hello World"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is true"), "Fail: data[valueNull] is true", null),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is true"), "Fail: data[valueNull] is true", null),
            )
        }
    }

    @Test
    fun `test assertThat isEquals`() {
        val data = dataOf(
            "value1" to true,
            "value2" to "Hello World"
        )
        with(Assertions(dataTrace())) {
            assertThat<Boolean>(data, "value1").isEqualTo(true)
            assertThat<Boolean>(data, "value1").isEqualTo(false)
            assertThat<String>(data, "value2").isEqualTo("Hello World")
            assertThat<String>(data, "value2").isEqualTo("Goodbye")
            assertThat<String?>(data, "valueNull").isEqualTo("Hi")
            assertThat<String>(data, "valueNull").isEqualTo("Hi")

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
            "value2" to "Hello World"
        )
        with(Assertions(dataTrace())) {
            assertThat<Boolean>(data, "value1").isNotEqualTo(true)
            assertThat<Boolean>(data, "value1").isNotEqualTo(false)
            assertThat<String>(data, "value2").isNotEqualTo("Hello World")
            assertThat<String>(data, "value2").isNotEqualTo("Goodbye")

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
        )
        with(Assertions(dataTrace())) {
            assertThat<Int>(data, "value").isLessThan(1)
            assertThat<Int>(data, "value").isLessThan(2)
            assertThat<Int?>(data, "valueNull").isLessThan(1)
            assertThat<Int>(data, "valueNull").isLessThan(1)

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
        )
        with(Assertions(dataTrace())) {
            assertThat<Long>(data, "value").isLessThan(1)
            assertThat<Long>(data, "value").isLessThan(2)
            assertThat<Long?>(data, "valueNull").isLessThan(1)
            assertThat<Long>(data, "valueNull").isLessThan(1)

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
        )
        with(Assertions(dataTrace())) {
            assertThat<Float>(data, "value").isLessThan(1.0f)
            assertThat<Float>(data, "value").isLessThan(2.0f)
            assertThat<Float?>(data, "valueNull").isLessThan(1.0f)
            assertThat<Float>(data, "valueNull").isLessThan(1.0f)

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
        )
        with(Assertions(dataTrace())) {
            assertThat<Double>(data, "value").isLessThan(1.0)
            assertThat<Double>(data, "value").isLessThan(2.0)
            assertThat<Double?>(data, "valueNull").isLessThan(1.0)
            assertThat<Double>(data, "valueNull").isLessThan(1.0)

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
    fun `test assertThat Int isLessThanEquals`() {
        val data = dataOf(
            "value" to 1,
        )
        with(Assertions(dataTrace())) {
            assertThat<Int>(data, "value").isLessThanEquals(0)
            assertThat<Int>(data, "value").isLessThanEquals(1)
            assertThat<Int>(data, "value").isLessThanEquals(2)
            assertThat<Int?>(data, "valueNull").isLessThanEquals(1)
            assertThat<Int>(data, "valueNull").isLessThanEquals(1)

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is less than equals 0"), "Fail: data[value] is less than equals 0", "1 <= 0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than equals 1"), "Pass: data[value] is less than equals 1", "1 <= 1"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than equals 2"), "Pass: data[value] is less than equals 2", "1 <= 2"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than equals 1"), "Fail: data[valueNull] is less than equals 1", "null <= 1"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than equals 1"), "Fail: data[valueNull] is less than equals 1", "null <= 1"),
            )
        }
    }

    @Test
    fun `test assertThat Long isLessThanEquals`() {
        val data = dataOf(
            "value" to 1L,
        )
        with(Assertions(dataTrace())) {
            assertThat<Long>(data, "value").isLessThanEquals(0)
            assertThat<Long>(data, "value").isLessThanEquals(1)
            assertThat<Long>(data, "value").isLessThanEquals(2)
            assertThat<Long?>(data, "valueNull").isLessThanEquals(1)
            assertThat<Long>(data, "valueNull").isLessThanEquals(1)

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is less than equals 0"), "Fail: data[value] is less than equals 0", "1 <= 0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than equals 1"), "Pass: data[value] is less than equals 1", "1 <= 1"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than equals 2"), "Pass: data[value] is less than equals 2", "1 <= 2"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than equals 1"), "Fail: data[valueNull] is less than equals 1", "null <= 1"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than equals 1"), "Fail: data[valueNull] is less than equals 1", "null <= 1"),
            )
        }
    }

    @Test
    fun `test assertThat Float isLessThanEquals`() {
        val data = dataOf(
            "value" to 1.0f,
        )
        with(Assertions(dataTrace())) {
            assertThat<Float>(data, "value").isLessThanEquals(0.0f)
            assertThat<Float>(data, "value").isLessThanEquals(1.0f)
            assertThat<Float>(data, "value").isLessThanEquals(2.0f)
            assertThat<Float?>(data, "valueNull").isLessThanEquals(1.0f)
            assertThat<Float>(data, "valueNull").isLessThanEquals(1.0f)

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is less than equals 0.0"), "Fail: data[value] is less than equals 0.0", "1.0 <= 0.0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than equals 1.0"), "Pass: data[value] is less than equals 1.0", "1.0 <= 1.0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than equals 2.0"), "Pass: data[value] is less than equals 2.0", "1.0 <= 2.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than equals 1.0"), "Fail: data[valueNull] is less than equals 1.0", "null <= 1.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than equals 1.0"), "Fail: data[valueNull] is less than equals 1.0", "null <= 1.0"),
            )
        }
    }

    @Test
    fun `test assertThat Double isLessThanEquals`() {
        val data = dataOf(
            "value" to 1.0,
        )
        with(Assertions(dataTrace())) {
            assertThat<Double>(data, "value").isLessThanEquals(0.0)
            assertThat<Double>(data, "value").isLessThanEquals(1.0)
            assertThat<Double>(data, "value").isLessThanEquals(2.0)
            assertThat<Double?>(data, "valueNull").isLessThanEquals(1.0)
            assertThat<Double>(data, "valueNull").isLessThanEquals(1.0)

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is less than equals 0.0"), "Fail: data[value] is less than equals 0.0", "1.0 <= 0.0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than equals 1.0"), "Pass: data[value] is less than equals 1.0", "1.0 <= 1.0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is less than equals 2.0"), "Pass: data[value] is less than equals 2.0", "1.0 <= 2.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than equals 1.0"), "Fail: data[valueNull] is less than equals 1.0", "null <= 1.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is less than equals 1.0"), "Fail: data[valueNull] is less than equals 1.0", "null <= 1.0"),
            )
        }
    }

    @Test
    fun `test assertThat Int isGreaterThan`() {
        val data = dataOf(
            "value" to 1,
        )
        with(Assertions(dataTrace())) {
            assertThat<Int>(data, "value").isGreaterThan(0)
            assertThat<Int>(data, "value").isGreaterThan(1)
            assertThat<Int>(data, "value").isGreaterThan(2)
            assertThat<Int?>(data, "valueNull").isGreaterThan(1)
            assertThat<Int>(data, "valueNull").isGreaterThan(1)

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than 0"), "Pass: data[value] is greater than 0", "1 > 0"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than 1"), "Fail: data[value] is greater than 1", "1 > 1"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than 2"), "Fail: data[value] is greater than 2", "1 > 2"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than 1"), "Fail: data[valueNull] is greater than 1", "null > 1"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than 1"), "Fail: data[valueNull] is greater than 1", "null > 1"),
            )
        }
    }

    @Test
    fun `test assertThat Long isGreaterThan`() {
        val data = dataOf(
            "value" to 1L,
        )
        with(Assertions(dataTrace())) {
            assertThat<Long>(data, "value").isGreaterThan(0)
            assertThat<Long>(data, "value").isGreaterThan(1)
            assertThat<Long>(data, "value").isGreaterThan(2)
            assertThat<Long?>(data, "valueNull").isGreaterThan(1)
            assertThat<Long>(data, "valueNull").isGreaterThan(1)

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than 0"), "Pass: data[value] is greater than 0", "1 > 0"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than 1"), "Fail: data[value] is greater than 1", "1 > 1"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than 2"), "Fail: data[value] is greater than 2", "1 > 2"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than 1"), "Fail: data[valueNull] is greater than 1", "null > 1"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than 1"), "Fail: data[valueNull] is greater than 1", "null > 1"),
            )
        }
    }

    @Test
    fun `test assertThat Float isGreaterThan`() {
        val data = dataOf(
            "value" to 1.0f,
        )
        with(Assertions(dataTrace())) {
            assertThat<Float>(data, "value").isGreaterThan(0.0f)
            assertThat<Float>(data, "value").isGreaterThan(1.0f)
            assertThat<Float>(data, "value").isGreaterThan(2.0f)
            assertThat<Float?>(data, "valueNull").isGreaterThan(1.0f)
            assertThat<Float>(data, "valueNull").isGreaterThan(1.0f)

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than 0.0"), "Pass: data[value] is greater than 0.0", "1.0 > 0.0"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than 1.0"), "Fail: data[value] is greater than 1.0", "1.0 > 1.0"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than 2.0"), "Fail: data[value] is greater than 2.0", "1.0 > 2.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than 1.0"), "Fail: data[valueNull] is greater than 1.0", "null > 1.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than 1.0"), "Fail: data[valueNull] is greater than 1.0", "null > 1.0"),
            )
        }
    }

    @Test
    fun `test assertThat Double isGreaterThan`() {
        val data = dataOf(
            "value" to 1.0,
        )
        with(Assertions(dataTrace())) {
            assertThat<Double>(data, "value").isGreaterThan(0.0)
            assertThat<Double>(data, "value").isGreaterThan(1.0)
            assertThat<Double>(data, "value").isGreaterThan(2.0)
            assertThat<Double?>(data, "valueNull").isGreaterThan(1.0)
            assertThat<Double>(data, "valueNull").isGreaterThan(1.0)

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than 0.0"), "Pass: data[value] is greater than 0.0", "1.0 > 0.0"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than 1.0"), "Fail: data[value] is greater than 1.0", "1.0 > 1.0"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than 2.0"), "Fail: data[value] is greater than 2.0", "1.0 > 2.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than 1.0"), "Fail: data[valueNull] is greater than 1.0", "null > 1.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than 1.0"), "Fail: data[valueNull] is greater than 1.0", "null > 1.0"),
            )
        }
    }

    @Test
    fun `test assertThat Int isGreaterThanEquals`() {
        val data = dataOf(
            "value" to 1,
        )
        with(Assertions(dataTrace())) {
            assertThat<Int>(data, "value").isGreaterThanEquals(0)
            assertThat<Int>(data, "value").isGreaterThanEquals(1)
            assertThat<Int>(data, "value").isGreaterThanEquals(2)
            assertThat<Int?>(data, "valueNull").isGreaterThanEquals(1)
            assertThat<Int>(data, "valueNull").isGreaterThanEquals(1)

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than equals 0"), "Pass: data[value] is greater than equals 0", "1 >= 0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than equals 1"), "Pass: data[value] is greater than equals 1", "1 >= 1"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than equals 2"), "Fail: data[value] is greater than equals 2", "1 >= 2"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than equals 1"), "Fail: data[valueNull] is greater than equals 1", "null >= 1"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than equals 1"), "Fail: data[valueNull] is greater than equals 1", "null >= 1"),
            )
        }
    }

    @Test
    fun `test assertThat Long isGreaterThanEquals`() {
        val data = dataOf(
            "value" to 1L,
        )
        with(Assertions(dataTrace())) {
            assertThat<Long>(data, "value").isGreaterThanEquals(0)
            assertThat<Long>(data, "value").isGreaterThanEquals(1)
            assertThat<Long>(data, "value").isGreaterThanEquals(2)
            assertThat<Long?>(data, "valueNull").isGreaterThanEquals(1)
            assertThat<Long>(data, "valueNull").isGreaterThanEquals(1)

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than equals 0"), "Pass: data[value] is greater than equals 0", "1 >= 0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than equals 1"), "Pass: data[value] is greater than equals 1", "1 >= 1"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than equals 2"), "Fail: data[value] is greater than equals 2", "1 >= 2"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than equals 1"), "Fail: data[valueNull] is greater than equals 1", "null >= 1"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than equals 1"), "Fail: data[valueNull] is greater than equals 1", "null >= 1"),
            )
        }
    }

    @Test
    fun `test assertThat Float isGreaterThanEquals`() {
        val data = dataOf(
            "value" to 1.0f,
        )
        with(Assertions(dataTrace())) {
            assertThat<Float>(data, "value").isGreaterThanEquals(0.0f)
            assertThat<Float>(data, "value").isGreaterThanEquals(1.0f)
            assertThat<Float>(data, "value").isGreaterThanEquals(2.0f)
            assertThat<Float?>(data, "valueNull").isGreaterThanEquals(1.0f)
            assertThat<Float>(data, "valueNull").isGreaterThanEquals(1.0f)

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than equals 0.0"), "Pass: data[value] is greater than equals 0.0", "1.0 >= 0.0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than equals 1.0"), "Pass: data[value] is greater than equals 1.0", "1.0 >= 1.0"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than equals 2.0"), "Fail: data[value] is greater than equals 2.0", "1.0 >= 2.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than equals 1.0"), "Fail: data[valueNull] is greater than equals 1.0", "null >= 1.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than equals 1.0"), "Fail: data[valueNull] is greater than equals 1.0", "null >= 1.0"),
            )
        }
    }

    @Test
    fun `test assertThat Double isGreaterThanEquals`() {
        val data = dataOf(
            "value" to 1.0,
        )
        with(Assertions(dataTrace())) {
            assertThat<Double>(data, "value").isGreaterThanEquals(0.0)
            assertThat<Double>(data, "value").isGreaterThanEquals(1.0)
            assertThat<Double>(data, "value").isGreaterThanEquals(2.0)
            assertThat<Double?>(data, "valueNull").isGreaterThanEquals(1.0)
            assertThat<Double>(data, "valueNull").isGreaterThanEquals(1.0)

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than equals 0.0"), "Pass: data[value] is greater than equals 0.0", "1.0 >= 0.0"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is greater than equals 1.0"), "Pass: data[value] is greater than equals 1.0", "1.0 >= 1.0"),
                ValidationError(dataTrace().tag("assertion" to "data[value] is greater than equals 2.0"), "Fail: data[value] is greater than equals 2.0", "1.0 >= 2.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than equals 1.0"), "Fail: data[valueNull] is greater than equals 1.0", "null >= 1.0"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is greater than equals 1.0"), "Fail: data[valueNull] is greater than equals 1.0", "null >= 1.0"),
            )
        }
    }

    @Test
    fun `test assertThat Collection contains`() {
        val data = dataOf(
            "value" to listOf(1, 2, 3),
            "valueEmpty" to emptyList<Int>(),
        )
        with(Assertions(dataTrace())) {
            assertThat<List<Int>>(data, "value").contains(1)
            assertThat<List<Int>>(data, "valueEmpty").contains(1)
            assertThat<List<Int>>(data, "value").contains(null)
            assertThat<List<Int>?>(data, "valueNull").contains(1)
            assertThat<List<Int>>(data, "valueNull").contains(1)

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] contains 1"), "Pass: data[value] contains 1", "1 in [1, 2, 3]"),
                ValidationError(dataTrace().tag("assertion" to "data[valueEmpty] contains 1"), "Fail: data[valueEmpty] contains 1", "1 in []"),
                ValidationError(dataTrace().tag("assertion" to "data[value] contains null"), "Fail: data[value] contains null", "null in [1, 2, 3]"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] contains 1"), "Fail: data[valueNull] contains 1", "1 in null"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] contains 1"), "Fail: data[valueNull] contains 1", "1 in null"),
            )
        }
    }

    @Test
    fun `test assertThat Collection notContains`() {
        val data = dataOf(
            "value" to listOf(1, 2, 3),
            "valueEmpty" to emptyList<Int>(),
        )
        with(Assertions(dataTrace())) {
            assertThat<List<Int>>(data, "value").notContains(1)
            assertThat<List<Int>>(data, "valueEmpty").notContains(1)
            assertThat<List<Int>>(data, "value").notContains(null)
            assertThat<List<Int>?>(data, "valueNull").notContains(1)
            assertThat<List<Int>>(data, "valueNull").notContains(1)

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] not contains 1"), "Fail: data[value] not contains 1", "1 not in [1, 2, 3]"),
                ValidationInfo(dataTrace().tag("assertion" to "data[valueEmpty] not contains 1"), "Pass: data[valueEmpty] not contains 1", "1 not in []"),
                ValidationInfo(dataTrace().tag("assertion" to "data[value] not contains null"), "Pass: data[value] not contains null", "null not in [1, 2, 3]"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] not contains 1"), "Fail: data[valueNull] not contains 1", "1 not in null"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] not contains 1"), "Fail: data[valueNull] not contains 1", "1 not in null"),
            )
        }
    }

    @Test
    fun `test assertThat Collection isEmpty`() {
        val data = dataOf(
            "value" to listOf(1, 2, 3),
            "valueEmpty" to emptyList<Int>(),
        )
        with(Assertions(dataTrace())) {
            assertThat<List<Int>>(data, "value").isEmpty()
            assertThat<List<Int>>(data, "valueEmpty").isEmpty()
            assertThat<List<Int>?>(data, "valueNull").isEmpty()
            assertThat<List<Int>>(data, "valueNull").isEmpty()

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is empty"), "Fail: data[value] is empty", listOf(1, 2, 3)),
                ValidationInfo(dataTrace().tag("assertion" to "data[valueEmpty] is empty"), "Pass: data[valueEmpty] is empty", emptyList<Int>()),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is empty"), "Fail: data[valueNull] is empty", null),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is empty"), "Fail: data[valueNull] is empty", null),
            )
        }
    }

    @Test
    fun `test assertThat Collection isNullOrEmpty`() {
        val data = dataOf(
            "value" to listOf(1, 2, 3),
            "valueEmpty" to emptyList<Int>(),
        )
        with(Assertions(dataTrace())) {
            assertThat<List<Int>>(data, "value").isNullOrEmpty()
            assertThat<List<Int>>(data, "valueEmpty").isNullOrEmpty()
            assertThat<List<Int>?>(data, "valueNull").isNullOrEmpty()
            assertThat<List<Int>>(data, "valueNull").isNullOrEmpty()

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is null or empty"), "Fail: data[value] is null or empty", listOf(1, 2, 3)),
                ValidationInfo(dataTrace().tag("assertion" to "data[valueEmpty] is null or empty"), "Pass: data[valueEmpty] is null or empty", emptyList<Int>()),
                ValidationInfo(dataTrace().tag("assertion" to "data[valueNull] is null or empty"), "Pass: data[valueNull] is null or empty", null),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is null or empty"), "Fail: data[valueNull] is null or empty", null),
            )
        }
    }

    @Test
    fun `test assertThat Collection isNotEmpty`() {
        val data = dataOf(
            "value" to listOf(1, 2, 3),
            "valueEmpty" to emptyList<Int>(),
        )
        with(Assertions(dataTrace())) {
            assertThat<List<Int>>(data, "value").isNotEmpty()
            assertThat<List<Int>>(data, "valueEmpty").isNotEmpty()
            assertThat<List<Int>?>(data, "valueNull").isNotEmpty()
            assertThat<List<Int>>(data, "valueNull").isNotEmpty()

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
        )
        with(Assertions(dataTrace())) {
            assertThat<List<Int>>(data, "value").isSizeEquals(3)
            assertThat<List<Int>>(data, "valueEmpty").isSizeEquals(3)
            assertThat<List<Int>?>(data, "valueNull").isSizeEquals(3)
            assertThat<List<Int>>(data, "valueNull").isSizeEquals(3)

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
        )
        with(Assertions(dataTrace())) {
            assertThat<String>(data, "value").isNull()
            assertThat<String?>(data, "valueNull").isNull()
            assertThat<String>(data, "valueNull").isNull()

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value] is null"), "Fail: data[value] is null", "Hello World"),
                ValidationInfo(dataTrace().tag("assertion" to "data[valueNull] is null"), "Pass: data[valueNull] is null", null),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is null"), "Fail: data[valueNull] is null", null),
            )
        }
    }

    @Test
    fun `test assertThat isNotNull`() {
        val data = dataOf(
            "value" to "Hello World",
        )
        with(Assertions(dataTrace())) {
            assertThat<String>(data, "value").isNotNull()
            assertThat<String?>(data, "valueNull").isNotNull()
            assertThat<String>(data, "valueNull").isNotNull()

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value] is not null"), "Pass: data[value] is not null", "Hello World"),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is not null"), "Fail: data[valueNull] is not null", null),
                ValidationError(dataTrace().tag("assertion" to "data[valueNull] is not null"), "Fail: data[valueNull] is not null", null),
            )
        }
    }
}
