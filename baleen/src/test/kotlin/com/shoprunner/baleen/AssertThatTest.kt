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

//
//    @Test
//    fun `test assertContains`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertContains("test 1 in [1,2,3]", listOf(1,2,3), 1)
//        assertions.assertContains("test 1 in []", emptyList<Int>(), 1)
//        assertions.assertContains("test null in [1,2,3]", listOf(1,2,3), null)
//        assertions.assertContains("test 1 in null", null, 1)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 in [1,2,3]"), "Pass: test 1 in [1,2,3]", "1 in [1, 2, 3]"),
//            ValidationError(dataTrace().tag("assertion" to "test 1 in []"), "Fail: test 1 in []", "1 in []"),
//            ValidationError(dataTrace().tag("assertion" to "test null in [1,2,3]"), "Fail: test null in [1,2,3]", "null in [1, 2, 3]"),
//            ValidationError(dataTrace().tag("assertion" to "test 1 in null"), "Fail: test 1 in null", "1 in null"),
//        )
//    }
//
//    @Test
//    fun `test assertNotContains`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertNotContains("test 1 !in [1,2,3]", listOf(1,2,3), 1)
//        assertions.assertNotContains("test 1 !in []", emptyList<Int>(), 1)
//        assertions.assertNotContains("test null !in [1,2,3]", listOf(1,2,3), null)
//        assertions.assertNotContains("test 1 !in null", null, 1)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 1 !in [1,2,3]"), "Fail: test 1 !in [1,2,3]", "1 not in [1, 2, 3]"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 !in []"), "Pass: test 1 !in []", "1 not in []"),
//            ValidationInfo(dataTrace().tag("assertion" to "test null !in [1,2,3]"), "Pass: test null !in [1,2,3]", "null not in [1, 2, 3]"),
//            ValidationError(dataTrace().tag("assertion" to "test 1 !in null"), "Fail: test 1 !in null", "1 not in null"),
//        )
//    }
//
//    @Test
//    fun `test assertEmpty`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertEmpty("test [1,2,3] is empty", listOf(1,2,3))
//        assertions.assertEmpty("test [] is empty", emptyList<Int>())
//        assertions.assertEmpty("test null is empty", null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test [1,2,3] is empty"), "Fail: test [1,2,3] is empty", listOf(1, 2, 3)),
//            ValidationInfo(dataTrace().tag("assertion" to "test [] is empty"), "Pass: test [] is empty", emptyList<Int>()),
//            ValidationError(dataTrace().tag("assertion" to "test null is empty"), "Fail: test null is empty", null),
//        )
//    }
//
//    @Test
//    fun `test assertNullOrEmpty`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertNullOrEmpty("test [1,2,3] is null or empty", listOf(1,2,3))
//        assertions.assertNullOrEmpty("test [] is null or empty", emptyList<Int>())
//        assertions.assertNullOrEmpty("test null is null or empty", null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test [1,2,3] is null or empty"), "Fail: test [1,2,3] is null or empty", listOf(1, 2, 3)),
//            ValidationInfo(dataTrace().tag("assertion" to "test [] is null or empty"), "Pass: test [] is null or empty", emptyList<Int>()),
//            ValidationInfo(dataTrace().tag("assertion" to "test null is null or empty"), "Pass: test null is null or empty", null),
//        )
//    }
//
//    @Test
//    fun `test assertNotEmpty`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertNotEmpty("test [1,2,3] is not empty", listOf(1,2,3))
//        assertions.assertNotEmpty("test [] is not empty", emptyList<Int>())
//        assertions.assertNotEmpty("test null is not empty", null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationInfo(dataTrace().tag("assertion" to "test [1,2,3] is not empty"), "Pass: test [1,2,3] is not empty", listOf(1, 2, 3)),
//            ValidationError(dataTrace().tag("assertion" to "test [] is not empty"), "Fail: test [] is not empty", emptyList<Int>()),
//            ValidationError(dataTrace().tag("assertion" to "test null is not empty"), "Fail: test null is not empty", null),
//        )
//    }
//
//    @Test
//    fun `test assertSizeEquals`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertSizeEquals("test [1,2,3] is length 3", listOf(1,2,3), 3)
//        assertions.assertSizeEquals("test [] is length 3", emptyList<Int>(), 3)
//        assertions.assertSizeEquals("test null is length 3", null, 3)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationInfo(dataTrace().tag("assertion" to "test [1,2,3] is length 3"), "Pass: test [1,2,3] is length 3", "3 = size([1, 2, 3])"),
//            ValidationError(dataTrace().tag("assertion" to "test [] is length 3"), "Fail: test [] is length 3", "3 = size([])"),
//            ValidationError(dataTrace().tag("assertion" to "test null is length 3"), "Fail: test null is length 3", "3 = size(null)"),
//        )
//    }
//
//    @Test
//    fun `test assertNull`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertNull("test \"hello\" is null", "hello")
//        assertions.assertNull("test null is null", null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test \"hello\" is null"), "Fail: test \"hello\" is null", "hello"),
//            ValidationInfo(dataTrace().tag("assertion" to "test null is null"), "Pass: test null is null", null),
//        )
//    }
//
//    @Test
//    fun `test assertNotNull`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertNotNull("test \"hello\" is not null", "hello")
//        assertions.assertNotNull("test null is not null", null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationInfo(dataTrace().tag("assertion" to "test \"hello\" is not null"), "Pass: test \"hello\" is not null", "hello"),
//            ValidationError(dataTrace().tag("assertion" to "test null is not null"), "Fail: test null is not null", null),
//        )
//    }
//
//    @Test
//    fun `test assertInstanceOf`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertInstanceOf<String>("test \"hello\" is a String", "hello")
//        assertions.assertInstanceOf<String>("test 1 is a String", 1)
//        assertions.assertInstanceOf<String>("test null is a String", null)
//        assertions.assertInstanceOf<String?>("test null is a nullable String", null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationInfo(dataTrace().tag("assertion" to "test \"hello\" is a String"), "Pass: test \"hello\" is a String", "hello is a kotlin.String"),
//            ValidationError(dataTrace().tag("assertion" to "test 1 is a String"), "Fail: test 1 is a String", "1 is a kotlin.Int"),
//            ValidationError(dataTrace().tag("assertion" to "test null is a String"), "Fail: test null is a String", "null is a null"),
//            ValidationInfo(dataTrace().tag("assertion" to "test null is a nullable String"), "Pass: test null is a nullable String", "null is a null"),
//        )
//    }
}
