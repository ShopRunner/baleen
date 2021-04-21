package com.shoprunner.baleen

/**
 * Simplify writing tests by using JUnit style assertions.
 */
class Assertions(val dataTrace: DataTrace) {
    private var validationResults: Sequence<ValidationResult> = sequenceOf()

    val results: Sequence<ValidationResult> get() = validationResults

    fun addValidationResult(result: ValidationResult) {
        validationResults += result
    }

    fun assertTrue(message: String, test: Boolean?, value: Any? = null) {
        addValidationResult(
            if (test != null && test) ValidationInfo(dataTrace.tag("assertion" to message), "Pass: $message", value)
            else ValidationError(dataTrace.tag("assertion" to message), "Fail: $message", value)
        )
    }

    fun assertFalse(message: String, test: Boolean?, right: Any? = null) {
        assertTrue(message, test?.not(), right)
    }

    fun assertEquals(message: String, left: Any?, right: Any?) {
        assertTrue(message, left == right, "$left == $right")
    }

    fun assertNotEquals(message: String, left: Any?, right: Any?) {
        assertTrue(message, left != right, "$left != $right")
    }

    fun assertLessThan(message: String, left: Int?, right: Int?) {
        assertTrue(message, left != null && right != null && left < right, "$left < $right")
    }

    fun assertLessThan(message: String, left: Long?, right: Long?) {
        assertTrue(message, left != null && right != null && left < right, "$left < $right")
    }

    fun assertLessThan(message: String, left: Float?, right: Float?) {
        assertTrue(message, left != null && right != null && left < right, "$left < $right")
    }

    fun assertLessThan(message: String, left: Double?, right: Double?) {
        assertTrue(message, left != null && right != null && left < right, "$left < $right")
    }

    fun assertLessThanEquals(message: String, left: Int?, right: Int?) {
        assertTrue(message, left != null && right != null && left <= right, "$left <= $right")
    }

    fun assertLessThanEquals(message: String, left: Long?, right: Long?) {
        assertTrue(message, left != null && right != null && left <= right, "$left <= $right")
    }

    fun assertLessThanEquals(message: String, left: Float?, right: Float?) {
        assertTrue(message, left != null && right != null && left <= right, "$left <= $right")
    }

    fun assertLessThanEquals(message: String, left: Double?, right: Double?) {
        assertTrue(message, left != null && right != null && left <= right, "$left <= $right")
    }

    fun assertGreaterThan(message: String, left: Int?, right: Int?) {
        assertTrue(message, left != null && right != null && left > right, "$left > $right")
    }

    fun assertGreaterThan(message: String, left: Long?, right: Long?) {
        assertTrue(message, left != null && right != null && left > right, "$left > $right")
    }

    fun assertGreaterThan(message: String, left: Float?, right: Float?) {
        assertTrue(message, left != null && right != null && left > right, "$left > $right")
    }

    fun assertGreaterThan(message: String, left: Double?, right: Double?) {
        assertTrue(message, left != null && right != null && left > right, "$left > $right")
    }

    fun assertGreaterThanEquals(message: String, left: Int?, right: Int?) {
        assertTrue(message, left != null && right != null && left >= right, "$left >= $right")
    }

    fun assertGreaterThanEquals(message: String, left: Long?, right: Long?) {
        assertTrue(message, left != null && right != null && left >= right, "$left >= $right")
    }

    fun assertGreaterThanEquals(message: String, left: Float?, right: Float?) {
        assertTrue(message, left != null && right != null && left >= right, "$left >= $right")
    }

    fun assertGreaterThanEquals(message: String, left: Double?, right: Double?) {
        assertTrue(message, left != null && right != null && left >= right, "$left >= $right")
    }

    fun assertContains(message: String, collection: Collection<*>?, value: Any?) {
        assertTrue(message, collection?.contains(value) == true, "$value in $collection")
    }

    fun assertNotContains(message: String, collection: Collection<*>?, value: Any?) {
        assertTrue(message, collection?.contains(value) == false, "$value not in $collection")
    }

    fun assertEmpty(message: String, collection: Collection<*>?) {
        assertTrue(message, collection?.isEmpty() == true, collection)
    }

    fun assertNullOrEmpty(message: String, collection: Collection<*>?) {
        assertTrue(message, collection.isNullOrEmpty(), collection)
    }

    fun assertNotEmpty(message: String, collection: Collection<*>?) {
        assertTrue(message, collection?.isNotEmpty() == true, collection)
    }

    fun assertSizeEquals(message: String, collection: Collection<*>?, size: Int) {
        assertTrue(message, collection?.size == size, "$size = size($collection)")
    }

    fun assertNull(message: String, value: Any?) {
        assertTrue(message, value == null, value)
    }

    fun assertNotNull(message: String, value: Any?) {
        assertTrue(message, value != null, value)
    }
}
