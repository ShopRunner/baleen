package com.shoprunner.baleen

import com.shoprunner.baleen.Data.Companion.getAs
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Container around the value
 */
sealed class AssertThat<T>(val actual: Any?, val messagePrefix: String)

class AssertThatValue<T>(val typedActual: T, messagePrefix: String = "") : AssertThat<T>(typedActual, messagePrefix)
class AssertThatNoValue<T>(actual: Any?, messagePrefix: String = "") : AssertThat<T>(actual, messagePrefix)

/**
 * Simplify writing tests by using JUnit style assertions.
 * @param dataTrace The dataTrace for the test
 */
class Assertions(val dataTrace: DataTrace) {
    private var validationResults: Sequence<ValidationResult> = sequenceOf()

    val results: Sequence<ValidationResult> get() = validationResults

    fun addValidationResult(result: ValidationResult) {
        validationResults += result
    }

    fun pass(message: String, value: Any?) {
        addValidationResult(
            ValidationInfo(dataTrace.tag("assertion" to message), "Pass: $message", value)
        )
    }

    fun fail(message: String, value: Any?) {
        addValidationResult(
            ValidationError(dataTrace.tag("assertion" to message), "Fail: $message", value)
        )
    }

    /**
     * Begin assertions for a single value
     * @param actual the value we are testing
     * @param messagePrefix an optional prefix to make the data more informative
     */
    fun <T> assertThat(actual: T, messagePrefix: String = ""): AssertThat<T> {
        return AssertThatValue(actual, messagePrefix)
    }

    /**
     * Begin assertions for a single value
     * @param actual the value we are testing
     * @param messagePrefix an optional prefix to make the data more informative
     */
    @ExperimentalContracts
    inline fun <reified T> assertThat(data: Data, key: String, messagePrefix: String = "data[$key] "): AssertThat<T> {
        return assertThat(messagePrefix) {
            data.getAs(key)
        }
    }

    /**
     * Begin assertions for a single value
     * @param actual the value we are testing
     * @param messagePrefix an optional prefix to make the data more informative
     */
    @ExperimentalContracts
    inline fun <reified T> assertThat(messagePrefix: String = "", getFun: () -> MaybeData<*, T>): AssertThat<T> {
        return when (val result = getFun()) {
            is BadData -> {
                fail(
                    "assertThat<${T::class.qualifiedName}>(${messagePrefix.trim()})",
                    "${result.original} is a ${result.original?.let { it::class.qualifiedName } ?: "${T::class.qualifiedName}?"}"
                )
                AssertThatNoValue(result.original, messagePrefix)
            }
            is GoodData -> {
                val value = result.value
                // TODO Need to add ? for nullable types.
                if (assertInstanceOf<T>("assertThat<${T::class.qualifiedName}>(${messagePrefix.trim()})", value)) {
                    AssertThatValue(value, messagePrefix)
                } else {
                    AssertThatNoValue(value, messagePrefix)
                }
            }
        }
    }

    fun assertTrue(message: String, test: Boolean?, value: Any? = null): Boolean {
        return if (test != null && test) {
            pass(message, value)
            true
        } else {
            fail(message, value)
            false
        }
    }

    fun <T : Boolean?> AssertThat<T>.isTrue(message: String = "is true"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertTrue(this.messagePrefix + message, this.typedActual, this.actual)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, this.actual)
        }
        return this
    }

    fun assertFalse(message: String, test: Boolean?, right: Any? = null) {
        assertTrue(message, test?.not(), right)
    }

    fun <T : Boolean?> AssertThat<T>.isFalse(message: String = "is false"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertFalse(this.messagePrefix + message, this.typedActual, this.actual)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, this.actual)
        }
        return this
    }

    fun assertEquals(message: String, left: Any?, right: Any?) {
        assertTrue(message, left == right, "$left == $right")
    }

    fun <T : Any?> AssertThat<T>.isEqualTo(expected: Any?, message: String = "is equal to $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertEquals(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} == $expected")
        }
        return this
    }

    fun assertNotEquals(message: String, left: Any?, right: Any?) {
        assertTrue(message, left != right, "$left != $right")
    }

    fun <T : Any?> AssertThat<T>.isNotEqualTo(expected: Any?, message: String = "is not equal to $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertNotEquals(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} != $expected")
        }
        return this
    }

    fun assertLessThan(message: String, left: Int?, right: Int?) {
        assertTrue(message, left != null && right != null && left < right, "$left < $right")
    }

    fun <T : Int?> AssertThat<T>.isLessThan(expected: Int, message: String = "is less than $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertLessThan(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} < $expected")
        }
        return this
    }

    fun assertLessThan(message: String, left: Long?, right: Long?) {
        assertTrue(message, left != null && right != null && left < right, "$left < $right")
    }

    fun <T : Long?> AssertThat<T>.isLessThan(expected: Long, message: String = "is less than $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertLessThan(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} < $expected")
        }
        return this
    }

    fun assertLessThan(message: String, left: Float?, right: Float?) {
        assertTrue(message, left != null && right != null && left < right, "$left < $right")
    }

    fun <T : Float?> AssertThat<T>.isLessThan(expected: Float, message: String = "is less than $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertLessThan(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} < $expected")
        }
        return this
    }

    fun assertLessThan(message: String, left: Double?, right: Double?) {
        assertTrue(message, left != null && right != null && left < right, "$left < $right")
    }

    fun <T : Double?> AssertThat<T>.isLessThan(expected: Double, message: String = "is less than $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertLessThan(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} < $expected")
        }
        return this
    }

    fun assertLessThanEquals(message: String, left: Int?, right: Int?) {
        assertTrue(message, left != null && right != null && left <= right, "$left <= $right")
    }

    fun <T : Int?> AssertThat<T>.isLessThanEquals(expected: Int, message: String = "is less than equals $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertLessThanEquals(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} <= $expected")
        }
        return this
    }

    fun assertLessThanEquals(message: String, left: Long?, right: Long?) {
        assertTrue(message, left != null && right != null && left <= right, "$left <= $right")
    }

    fun <T : Long?> AssertThat<T>.isLessThanEquals(expected: Long, message: String = "is less than equals $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertLessThanEquals(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} <= $expected")
        }
        return this
    }

    fun assertLessThanEquals(message: String, left: Float?, right: Float?) {
        assertTrue(message, left != null && right != null && left <= right, "$left <= $right")
    }

    fun <T : Float?> AssertThat<T>.isLessThanEquals(expected: Float, message: String = "is less than equals $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertLessThanEquals(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} <= $expected")
        }
        return this
    }

    fun assertLessThanEquals(message: String, left: Double?, right: Double?) {
        assertTrue(message, left != null && right != null && left <= right, "$left <= $right")
    }

    fun <T : Double?> AssertThat<T>.isLessThanEquals(expected: Double, message: String = "is less than equals $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertLessThanEquals(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} <= $expected")
        }
        return this
    }

    fun assertGreaterThan(message: String, left: Int?, right: Int?) {
        assertTrue(message, left != null && right != null && left > right, "$left > $right")
    }

    fun <T : Int?> AssertThat<T>.isGreaterThan(expected: Int, message: String = "is greater than $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertGreaterThan(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} > $expected")
        }
        return this
    }

    fun assertGreaterThan(message: String, left: Long?, right: Long?) {
        assertTrue(message, left != null && right != null && left > right, "$left > $right")
    }

    fun <T : Long?> AssertThat<T>.isGreaterThan(expected: Long, message: String = "is greater than $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertGreaterThan(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} > $expected")
        }
        return this
    }

    fun assertGreaterThan(message: String, left: Float?, right: Float?) {
        assertTrue(message, left != null && right != null && left > right, "$left > $right")
    }

    fun <T : Float?> AssertThat<T>.isGreaterThan(expected: Float, message: String = "is greater than $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertGreaterThan(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} > $expected")
        }
        return this
    }

    fun assertGreaterThan(message: String, left: Double?, right: Double?) {
        assertTrue(message, left != null && right != null && left > right, "$left > $right")
    }

    fun <T : Double?> AssertThat<T>.isGreaterThan(expected: Double, message: String = "is greater than $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertGreaterThan(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} > $expected")
        }
        return this
    }

    fun assertGreaterThanEquals(message: String, left: Int?, right: Int?) {
        assertTrue(message, left != null && right != null && left >= right, "$left >= $right")
    }

    fun <T : Int?> AssertThat<T>.isGreaterThanEquals(expected: Int, message: String = "is greater than equals $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertGreaterThanEquals(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} >= $expected")
        }
        return this
    }

    fun assertGreaterThanEquals(message: String, left: Long?, right: Long?) {
        assertTrue(message, left != null && right != null && left >= right, "$left >= $right")
    }

    fun <T : Long?> AssertThat<T>.isGreaterThanEquals(expected: Long, message: String = "is greater than equals $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertGreaterThanEquals(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} >= $expected")
        }
        return this
    }

    fun assertGreaterThanEquals(message: String, left: Float?, right: Float?) {
        assertTrue(message, left != null && right != null && left >= right, "$left >= $right")
    }

    fun <T : Float?> AssertThat<T>.isGreaterThanEquals(expected: Float, message: String = "is greater than equals $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertGreaterThanEquals(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} >= $expected")
        }
        return this
    }

    fun assertGreaterThanEquals(message: String, left: Double?, right: Double?) {
        assertTrue(message, left != null && right != null && left >= right, "$left >= $right")
    }

    fun <T : Double?> AssertThat<T>.isGreaterThanEquals(expected: Double, message: String = "is greater than equals $expected"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertGreaterThanEquals(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} >= $expected")
        }
        return this
    }

    fun assertContains(message: String, collection: Collection<*>?, value: Any?) {
        assertTrue(message, collection?.contains(value) == true, "$value in $collection")
    }

    fun <T, C : Collection<T>?> AssertThat<C>.contains(expected: T, message: String = "contains $expected"): AssertThat<C> {
        when (this) {
            is AssertThatValue ->
                assertContains(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "$expected in ${this.actual}")
        }
        return this
    }

    fun assertNotContains(message: String, collection: Collection<*>?, value: Any?) {
        assertTrue(message, collection?.contains(value) == false, "$value not in $collection")
    }

    fun <T, C : Collection<T>?> AssertThat<C>.notContains(expected: T, message: String = "not contains $expected"): AssertThat<C> {
        when (this) {
            is AssertThatValue ->
                assertNotContains(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "$expected not in ${this.actual}")
        }
        return this
    }

    fun assertEmpty(message: String, collection: Collection<*>?) {
        assertTrue(message, collection?.isEmpty() == true, collection)
    }

    fun <T : Collection<*>?> AssertThat<T>.isEmpty(message: String = "is empty"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertEmpty(this.messagePrefix + message, this.typedActual)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, this.actual)
        }
        return this
    }

    fun assertNullOrEmpty(message: String, collection: Collection<*>?) {
        assertTrue(message, collection.isNullOrEmpty(), collection)
    }

    fun <T : Collection<*>?> AssertThat<T>.isNullOrEmpty(message: String = "is null or empty"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertNullOrEmpty(this.messagePrefix + message, this.typedActual)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, this.actual)
        }
        return this
    }

    fun assertNotEmpty(message: String, collection: Collection<*>?) {
        assertTrue(message, collection?.isNotEmpty() == true, collection)
    }

    fun <T : Collection<*>?> AssertThat<T>.isNotEmpty(message: String = "is not empty"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertNotEmpty(this.messagePrefix + message, this.typedActual)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, this.actual)
        }
        return this
    }

    fun assertSizeEquals(message: String, collection: Collection<*>?, size: Int) {
        assertTrue(message, collection?.size == size, "$size = size($collection)")
    }

    fun <T : Collection<*>?> AssertThat<T>.isSizeEquals(size: Int, message: String = "is size equal to $size"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertSizeEquals(this.messagePrefix + message, this.typedActual, size)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "$size = size(${this.actual})")
        }
        return this
    }

    @ExperimentalContracts
    fun assertNull(message: String, value: Any?): Boolean {
        contract {
            returns(true) implies (value == null)
        }
        return assertTrue(message, value == null, value)
    }

    @ExperimentalContracts
    fun <T : Any?> AssertThat<T>.isNull(message: String = "is null"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertNull(this.messagePrefix + message, this.typedActual)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, this.actual)
        }
        return this
    }

    @ExperimentalContracts
    fun assertNotNull(message: String, value: Any?): Boolean {
        contract {
            returns(true) implies (value != null)
        }
        return assertTrue(message, value != null, value)
    }

    @ExperimentalContracts
    fun <T : Any?> AssertThat<T>.isNotNull(message: String = "is not null"): AssertThat<T> {
        when (this) {
            is AssertThatValue ->
                assertNotNull(this.messagePrefix + message, this.typedActual)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, this.actual)
        }
        return this
    }

    @ExperimentalContracts
    inline fun <reified T> assertInstanceOf(message: String, value: Any?): Boolean {
        contract {
            returns(true) implies (value is T)
        }
        return assertTrue(message, value is T, "$value is a ${value?.let { it::class.qualifiedName } ?: "${T::class.qualifiedName}?"}")
    }
}
