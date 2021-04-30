package com.shoprunner.baleen

import com.shoprunner.baleen.TestHelper.dataOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import kotlin.contracts.ExperimentalContracts
import org.assertj.core.api.Assertions.assertThat as junitAssertThat

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExperimentalContracts
internal class AssertThatTest {

    @Test
    fun `test assertThat validate instance of`() {
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

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationInfo(dataTrace().tag("assertion" to "data[value1] is true"), "Pass: data[value1] is true", true),
                ValidationError(dataTrace().tag("assertion" to "data[value2] is true"), "Fail: data[value2] is true", false),
                ValidationError(dataTrace().tag("assertion" to "data[value3] is true"), "Fail: data[value3] is true", "Hello World"),
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

            val results = this.results.toList()

            junitAssertThat(results).contains(
                ValidationError(dataTrace().tag("assertion" to "data[value1] is false"), "Fail: data[value1] is false", true),
                ValidationInfo(dataTrace().tag("assertion" to "data[value2] is false"), "Pass: data[value2] is false", false),
                ValidationError(dataTrace().tag("assertion" to "data[value3] is false"), "Fail: data[value3] is false", "Hello World"),
            )
        }
    }
//
//    @Test
//    fun `test assertEquals`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertEquals("test 1 = 1", 1, 1)
//        assertions.assertEquals("test 1 = 2", 1, 2)
//        assertions.assertEquals("test \"hello\" = \"hello\"", "hello", "hello")
//        assertions.assertEquals("test \"hello\" = \"world\"", "hello", "world")
//        assertions.assertEquals("test null = null", null, null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 = 1"), "Pass: test 1 = 1", "1 == 1"),
//            ValidationError(dataTrace().tag("assertion" to "test 1 = 2"), "Fail: test 1 = 2", "1 == 2"),
//            ValidationInfo(dataTrace().tag("assertion" to "test \"hello\" = \"hello\""), "Pass: test \"hello\" = \"hello\"", "hello == hello"),
//            ValidationError(dataTrace().tag("assertion" to "test \"hello\" = \"world\""), "Fail: test \"hello\" = \"world\"", "hello == world"),
//            ValidationInfo(dataTrace().tag("assertion" to "test null = null"), "Pass: test null = null", "null == null"),
//        )
//    }
//
//    @Test
//    fun `test assertNotEquals`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertNotEquals("test 1 != 1", 1, 1)
//        assertions.assertNotEquals("test 1 != 2", 1, 2)
//        assertions.assertNotEquals("test \"hello\" != \"hello\"", "hello", "hello")
//        assertions.assertNotEquals("test \"hello\" != \"world\"", "hello", "world")
//        assertions.assertNotEquals("test null != null", null, null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 1 != 1"), "Fail: test 1 != 1", "1 != 1"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 != 2"), "Pass: test 1 != 2", "1 != 2"),
//            ValidationError(dataTrace().tag("assertion" to "test \"hello\" != \"hello\""), "Fail: test \"hello\" != \"hello\"", "hello != hello"),
//            ValidationInfo(dataTrace().tag("assertion" to "test \"hello\" != \"world\""), "Pass: test \"hello\" != \"world\"", "hello != world"),
//            ValidationError(dataTrace().tag("assertion" to "test null != null"), "Fail: test null != null", "null != null"),
//        )
//    }
//
//    @Test
//    fun `test assertLessThan(Int)`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertLessThan("test 1 < 1", 1.toInt(), 1)
//        assertions.assertLessThan("test 1 < 2", 1.toInt(), 2)
//        assertions.assertLessThan("test null < null", null?.toInt(), null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 1 < 1"), "Fail: test 1 < 1", "1 < 1"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 < 2"), "Pass: test 1 < 2", "1 < 2"),
//            ValidationError(dataTrace().tag("assertion" to "test null < null"), "Fail: test null < null", "null < null"),
//        )
//    }
//
//    @Test
//    fun `test assertLessThan(Long)`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertLessThan("test 1 < 1", 1L, 1L)
//        assertions.assertLessThan("test 1 < 2", 1L, 2L)
//        assertions.assertLessThan("test null < null", null?.toLong(), null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 1 < 1"), "Fail: test 1 < 1", "1 < 1"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 < 2"), "Pass: test 1 < 2", "1 < 2"),
//            ValidationError(dataTrace().tag("assertion" to "test null < null"), "Fail: test null < null", "null < null"),
//        )
//    }
//
//    @Test
//    fun `test assertLessThan(Float)`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertLessThan("test 1 < 1", 1f, 1f)
//        assertions.assertLessThan("test 1 < 2", 1f, 2f)
//        assertions.assertLessThan("test null < null", null?.toFloat(), null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 1 < 1"), "Fail: test 1 < 1", "1.0 < 1.0"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 < 2"), "Pass: test 1 < 2", "1.0 < 2.0"),
//            ValidationError(dataTrace().tag("assertion" to "test null < null"), "Fail: test null < null", "null < null"),
//        )
//    }
//
//    @Test
//    fun `test assertLessThan(Double)`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertLessThan("test 1 < 1", 1.0, 1.0)
//        assertions.assertLessThan("test 1 < 2", 1.0, 2.0)
//        assertions.assertLessThan("test null < null", null?.toDouble(), null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 1 < 1"), "Fail: test 1 < 1", "1.0 < 1.0"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 < 2"), "Pass: test 1 < 2", "1.0 < 2.0"),
//            ValidationError(dataTrace().tag("assertion" to "test null < null"), "Fail: test null < null", "null < null"),
//        )
//    }
//
//    @Test
//    fun `test assertLessThanEquals(Int)`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertLessThanEquals("test 1 <= 0", 1.toInt(), 0)
//        assertions.assertLessThanEquals("test 1 <= 1", 1.toInt(), 1)
//        assertions.assertLessThanEquals("test 1 <= 2", 1.toInt(), 2)
//        assertions.assertLessThanEquals("test null <= null", null?.toInt(), null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 1 <= 0"), "Fail: test 1 <= 0", "1 <= 0"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 <= 1"), "Pass: test 1 <= 1", "1 <= 1"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 <= 2"), "Pass: test 1 <= 2", "1 <= 2"),
//            ValidationError(dataTrace().tag("assertion" to "test null <= null"), "Fail: test null <= null", "null <= null"),
//        )
//    }
//
//    @Test
//    fun `test assertLessThanEquals(Long)`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertLessThanEquals("test 1 <= 0", 1L, 0)
//        assertions.assertLessThanEquals("test 1 <= 1", 1L, 1L)
//        assertions.assertLessThanEquals("test 1 <= 2", 1L, 2L)
//        assertions.assertLessThanEquals("test null <= null", null?.toLong(), null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 1 <= 0"), "Fail: test 1 <= 0", "1 <= 0"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 <= 1"), "Pass: test 1 <= 1", "1 <= 1"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 <= 2"), "Pass: test 1 <= 2", "1 <= 2"),
//            ValidationError(dataTrace().tag("assertion" to "test null <= null"), "Fail: test null <= null", "null <= null"),
//        )
//    }
//
//    @Test
//    fun `test assertLessThanEquals(Float)`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertLessThanEquals("test 1 <= 0", 1f, 0f)
//        assertions.assertLessThanEquals("test 1 <= 1", 1f, 1f)
//        assertions.assertLessThanEquals("test 1 <= 2", 1f, 2f)
//        assertions.assertLessThanEquals("test null <= null", null?.toFloat(), null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 1 <= 0"), "Fail: test 1 <= 0", "1.0 <= 0.0"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 <= 1"), "Pass: test 1 <= 1", "1.0 <= 1.0"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 <= 2"), "Pass: test 1 <= 2", "1.0 <= 2.0"),
//            ValidationError(dataTrace().tag("assertion" to "test null <= null"), "Fail: test null <= null", "null <= null"),
//        )
//    }
//
//    @Test
//    fun `test assertLessThanEquals(Double)`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertLessThanEquals("test 1 <= 0", 1.0, 0.0)
//        assertions.assertLessThanEquals("test 1 <= 1", 1.0, 1.0)
//        assertions.assertLessThanEquals("test 1 <= 2", 1.0, 2.0)
//        assertions.assertLessThanEquals("test null <= null", null?.toDouble(), null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 1 <= 0"), "Fail: test 1 <= 0", "1.0 <= 0.0"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 <= 1"), "Pass: test 1 <= 1", "1.0 <= 1.0"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 <= 2"), "Pass: test 1 <= 2", "1.0 <= 2.0"),
//            ValidationError(dataTrace().tag("assertion" to "test null <= null"), "Fail: test null <= null", "null <= null"),
//        )
//    }
//
//    @Test
//    fun `test assertGreaterThan(Int)`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertGreaterThan("test 1 > 1", 1.toInt(), 1)
//        assertions.assertGreaterThan("test 2 > 1", 2.toInt(), 1)
//        assertions.assertGreaterThan("test null > null", null?.toInt(), null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 1 > 1"), "Fail: test 1 > 1", "1 > 1"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 2 > 1"), "Pass: test 2 > 1", "2 > 1"),
//            ValidationError(dataTrace().tag("assertion" to "test null > null"), "Fail: test null > null", "null > null"),
//        )
//    }
//
//    @Test
//    fun `test assertGreaterThan(Long)`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertGreaterThan("test 1 > 1", 1L, 1L)
//        assertions.assertGreaterThan("test 2 > 1", 2L, 1L)
//        assertions.assertGreaterThan("test null > null", null?.toLong(), null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 1 > 1"), "Fail: test 1 > 1", "1 > 1"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 2 > 1"), "Pass: test 2 > 1", "2 > 1"),
//            ValidationError(dataTrace().tag("assertion" to "test null > null"), "Fail: test null > null", "null > null"),
//        )
//    }
//
//    @Test
//    fun `test assertGreaterThan(Float)`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertGreaterThan("test 1 > 1", 1f, 1f)
//        assertions.assertGreaterThan("test 2 > 1", 2f, 1f)
//        assertions.assertGreaterThan("test null > null", null?.toFloat(), null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 1 > 1"), "Fail: test 1 > 1", "1.0 > 1.0"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 2 > 1"), "Pass: test 2 > 1", "2.0 > 1.0"),
//            ValidationError(dataTrace().tag("assertion" to "test null > null"), "Fail: test null > null", "null > null"),
//        )
//    }
//
//    @Test
//    fun `test assertGreaterThan(Double)`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertGreaterThan("test 1 > 1", 1.0, 1.0)
//        assertions.assertGreaterThan("test 2 > 1", 2.0, 1.0)
//        assertions.assertGreaterThan("test null > null", null?.toDouble(), null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 1 > 1"), "Fail: test 1 > 1", "1.0 > 1.0"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 2 > 1"), "Pass: test 2 > 1", "2.0 > 1.0"),
//            ValidationError(dataTrace().tag("assertion" to "test null > null"), "Fail: test null > null", "null > null"),
//        )
//    }
//
//    @Test
//    fun `test assertGreaterThanEquals(Int)`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertGreaterThanEquals("test 0 >= 1", 0.toInt(), 1)
//        assertions.assertGreaterThanEquals("test 1 >= 1", 1.toInt(), 1)
//        assertions.assertGreaterThanEquals("test 2 >= 1", 2.toInt(), 1)
//        assertions.assertGreaterThanEquals("test null >= null", null?.toInt(), null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 0 >= 1"), "Fail: test 0 >= 1", "0 >= 1"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 >= 1"), "Pass: test 1 >= 1", "1 >= 1"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 2 >= 1"), "Pass: test 2 >= 1", "2 >= 1"),
//            ValidationError(dataTrace().tag("assertion" to "test null >= null"), "Fail: test null >= null", "null >= null"),
//        )
//    }
//
//    @Test
//    fun `test assertGreaterThanEquals(Long)`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertGreaterThanEquals("test 0 >= 1", 0L, 1)
//        assertions.assertGreaterThanEquals("test 1 >= 1", 1L, 1L)
//        assertions.assertGreaterThanEquals("test 2 >= 1", 2L, 1L)
//        assertions.assertGreaterThanEquals("test null >= null", null?.toLong(), null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 0 >= 1"), "Fail: test 0 >= 1", "0 >= 1"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 >= 1"), "Pass: test 1 >= 1", "1 >= 1"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 2 >= 1"), "Pass: test 2 >= 1", "2 >= 1"),
//            ValidationError(dataTrace().tag("assertion" to "test null >= null"), "Fail: test null >= null", "null >= null"),
//        )
//    }
//
//    @Test
//    fun `test assertGreaterThanEquals(Float)`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertGreaterThanEquals("test 0 >= 1", 0f, 1f)
//        assertions.assertGreaterThanEquals("test 1 >= 1", 1f, 1f)
//        assertions.assertGreaterThanEquals("test 2 >= 1", 2f, 1f)
//        assertions.assertGreaterThanEquals("test null >= null", null?.toFloat(), null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 0 >= 1"), "Fail: test 0 >= 1", "0.0 >= 1.0"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 >= 1"), "Pass: test 1 >= 1", "1.0 >= 1.0"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 2 >= 1"), "Pass: test 2 >= 1", "2.0 >= 1.0"),
//            ValidationError(dataTrace().tag("assertion" to "test null >= null"), "Fail: test null >= null", "null >= null"),
//        )
//    }
//
//    @Test
//    fun `test assertGreaterThanEquals(Double)`() {
//        val assertions = Assertions(dataTrace())
//
//        assertions.assertGreaterThanEquals("test 0 >= 1", 0.0, 1.0)
//        assertions.assertGreaterThanEquals("test 1 >= 1", 1.0, 1.0)
//        assertions.assertGreaterThanEquals("test 2 >= 1", 2.0, 1.0)
//        assertions.assertGreaterThanEquals("test null >= null", null?.toDouble(), null)
//
//        assertThat(assertions.results).containsExactly(
//            ValidationError(dataTrace().tag("assertion" to "test 0 >= 1"), "Fail: test 0 >= 1", "0.0 >= 1.0"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 1 >= 1"), "Pass: test 1 >= 1", "1.0 >= 1.0"),
//            ValidationInfo(dataTrace().tag("assertion" to "test 2 >= 1"), "Pass: test 2 >= 1", "2.0 >= 1.0"),
//            ValidationError(dataTrace().tag("assertion" to "test null >= null"), "Fail: test null >= null", "null >= null"),
//        )
//    }
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
