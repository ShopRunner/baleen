package com.shoprunner.baleen

import com.shoprunner.baleen.Data.Companion.getAs
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

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
     * Begin assertions for a data map
     * @param actual the data we are testing
     * @param messagePrefix an optional prefix to make the data more informative
     */
    fun assertThat(actual: Data, messagePrefix: String = ""): AssertThat<Data> {
        return AssertThatValue(actual, messagePrefix)
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

    fun AssertThat<Data>.hasAttribute(attribute: String, attributeAssertions: Assertions.(AssertThat<Any?>)-> Unit = {}): AssertThat<Data> {
        when(this) {
            is AssertThatValue<Data> ->
                if (this.typedActual.containsKey(attribute)) {
                    pass(messagePrefix + "data[$attribute] exists", this.typedActual)
                    attributeAssertions(assertThat(this.typedActual[attribute], messagePrefix + "data[$attribute] "))
                } else {
                    fail(messagePrefix + "data[$attribute] exists", this.typedActual)
                    attributeAssertions(AssertThatNoValue(null, messagePrefix + "data[$attribute] "))
                }
            is AssertThatNoValue -> {
                fail(messagePrefix + "data[$attribute] exists", this.actual)
                attributeAssertions(AssertThatNoValue(null, messagePrefix + "data[$attribute] "))
            }
        }
        return this
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

    fun AssertThat<Boolean>.isTrue(message: String = "is true"): AssertThat<Boolean> {
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

    fun AssertThat<Boolean>.isFalse(message: String = "is false"): AssertThat<Boolean> {
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

    fun AssertThat<Int>.isLessThan(expected: Int, message: String = "is less than $expected"): AssertThat<Int> {
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

    fun AssertThat<Long>.isLessThan(expected: Long, message: String = "is less than $expected"): AssertThat<Long> {
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

    fun AssertThat<Float>.isLessThan(expected: Float, message: String = "is less than $expected"): AssertThat<Float> {
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

    fun AssertThat<Double>.isLessThan(expected: Double, message: String = "is less than $expected"): AssertThat<Double> {
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

    fun AssertThat<Int>.isLessThanEquals(expected: Int, message: String = "is less than equals $expected"): AssertThat<Int> {
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

    fun AssertThat<Long>.isLessThanEquals(expected: Long, message: String = "is less than equals $expected"): AssertThat<Long> {
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

    fun AssertThat<Float>.isLessThanEquals(expected: Float, message: String = "is less than equals $expected"): AssertThat<Float> {
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

    fun AssertThat<Double>.isLessThanEquals(expected: Double, message: String = "is less than equals $expected"): AssertThat<Double> {
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

    fun AssertThat<Int>.isGreaterThan(expected: Int, message: String = "is greater than $expected"): AssertThat<Int> {
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

    fun AssertThat<Long>.isGreaterThan(expected: Long, message: String = "is greater than $expected"): AssertThat<Long> {
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

    fun AssertThat<Float>.isGreaterThan(expected: Float, message: String = "is greater than $expected"): AssertThat<Float> {
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

    fun AssertThat<Double>.isGreaterThan(expected: Double, message: String = "is greater than $expected"): AssertThat<Double> {
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

    fun AssertThat<Int>.isGreaterThanEquals(expected: Int, message: String = "is greater than equals $expected"): AssertThat<Int> {
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

    fun AssertThat<Long>.isGreaterThanEquals(expected: Long, message: String = "is greater than equals $expected"): AssertThat<Long> {
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

    fun AssertThat<Float>.isGreaterThanEquals(expected: Float, message: String = "is greater than equals $expected"): AssertThat<Float> {
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

    fun AssertThat<Double>.isGreaterThanEquals(expected: Double, message: String = "is greater than equals $expected"): AssertThat<Double> {
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

    fun <T, C: Collection<T>> AssertThat<C>.contains(expected: T, message: String = "contains $expected"): AssertThat<C> {
        when (this) {
            is AssertThatValue ->
                assertContains(this.messagePrefix + message, this.typedActual, expected)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "$expected in ${this.actual}")
        }
        return this
    }

    fun <T: Any?> AssertThat<T>.isOneOf(collection: Collection<T>, message: String = "is one of $collection"): AssertThat<T> {
        when(this) {
            is AssertThatValue ->
                assertContains(this.messagePrefix + message, collection, this.typedActual)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, "${this.actual} in $collection")
        }
        return this
    }

    fun assertNotContains(message: String, collection: Collection<*>?, value: Any?) {
        assertTrue(message, collection?.contains(value) == false, "$value not in $collection")
    }

    fun <T, C: Collection<T>> AssertThat<C>.notContains(expected: T, message: String = "not contains $expected"): AssertThat<C> {
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

    fun <T, C: Collection<T>> AssertThat<C>.isEmpty(message: String = "is empty"): AssertThat<C> {
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

    fun <T, C: Collection<T>> AssertThat<C?>.isNullOrEmpty(message: String = "is null or empty"): AssertThat<C?> {
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

    fun <T, C: Collection<T>?> AssertThat<C>.isNotEmpty(message: String = "is not empty"): AssertThat<C> {
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

    fun <T, C: Collection<T>?> AssertThat<C>.isSizeEquals(size: Int, message: String = "is size equal to $size"): AssertThat<C> {
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
    fun <T> AssertThat<T?>.isNull(message: String = "is null"): AssertThat<Unit> {
        when (this) {
            is AssertThatValue ->
                assertNull(this.messagePrefix + message, this.typedActual)
            is AssertThatNoValue ->
                fail(this.messagePrefix + message, this.actual)
        }
        return AssertThatNoValue(this.actual, messagePrefix)
    }

    fun <T> AssertThat<T?>.isNullOr(message: String = "is null", body: Assertions.(AssertThat<T>) -> Unit): AssertThat<T?> {
        when (this) {
            is AssertThatValue -> {
                when(typedActual) {
                    null -> pass(messagePrefix + message, typedActual)
                    else -> body(AssertThatValue(typedActual, messagePrefix))
                }
            }
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
    fun <T> AssertThat<T?>.isNotNull(message: String = "is not null"): AssertThat<T> {
        return when (this) {
            is AssertThatValue ->
                if(assertNotNull(this.messagePrefix + message, this.typedActual)) {
                    AssertThatValue(this.typedActual, this.messagePrefix)
                } else {
                    AssertThatNoValue(this.typedActual, this.messagePrefix)
                }
            is AssertThatNoValue -> {
                fail(this.messagePrefix + message, this.actual)
                AssertThatNoValue(this.actual, this.messagePrefix)
            }
        }
    }

    @ExperimentalContracts
    inline fun <reified T> assertInstanceOf(message: String, value: Any?): Boolean {
        contract {
            returns(true) implies (value is T)
        }
        return assertTrue(message, value is T, "$value is a ${value?.let { it::class.qualifiedName } ?: "${T::class.qualifiedName}?"}")
    }

    inline fun <reified T : Any?> AssertThat<Any?>.isA(message: String = "is a ${T::class.qualifiedName}"): AssertThat<T> {
        return when (this) {
            is AssertThatValue ->
                if(this.typedActual is T) {
                    pass(messagePrefix + message, this.typedActual)
                    AssertThatValue(this.typedActual, this.messagePrefix)
                } else {
                    fail(messagePrefix + message, this.typedActual)
                    AssertThatNoValue(this.typedActual, this.messagePrefix)
                }
            is AssertThatNoValue -> {
                fail(this.messagePrefix + message, this.actual)
                AssertThatNoValue(this.actual, this.messagePrefix)
            }
        }
    }

    /**
     *
     */
    fun <T: Any?> AssertThat<T>.or(vararg ors: Assertions.(AssertThat<T>) -> Unit): AssertThat<T> {
        val testResults = ors.map { orFun ->
            val orAssertions = Assertions(dataTrace)
            orAssertions.orFun(this)
            orAssertions.results.toList()
        }

        val firstSuccessOrAllErrors = testResults.firstOrNull { r -> r.none { it is ValidationError } }
            ?: testResults.flatten()

        firstSuccessOrAllErrors.forEach(this@Assertions::addValidationResult)

        return this
    }
}
