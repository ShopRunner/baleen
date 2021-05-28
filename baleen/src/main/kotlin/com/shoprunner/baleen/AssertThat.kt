package com.shoprunner.baleen

/**
 * Container around the value. It can be the correct typed value OR
 * the incorrect type for errors. The `actual` value is kept for
 * reporting purposes.
 */
sealed class AssertThat<T>(val actual: Any?, val messagePrefix: String)

/**
 * The value is the correct type and can be asserted on. The typed value is accessible.
 */
class AssertThatValue<T>(val typedActual: T, messagePrefix: String = "") : AssertThat<T>(typedActual, messagePrefix)

/**
 * The value is incorrect type and cannot be asserted on. The actual value is available for reviewing.
 */
class AssertThatNoValue<T>(actual: Any?, messagePrefix: String = "") : AssertThat<T>(actual, messagePrefix)
