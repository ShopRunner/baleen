package com.shoprunner.baleen

/**
 * Base class for good or bad data.
 */
sealed class MaybeData<E : Throwable, T>(val original: Any?) {
    abstract fun getOrThrow(): T
    abstract fun getOrNull(): T?
}

/**
 * Bad data. Contains the exception as well as the original value
 */
class BadData<E : Throwable, T>(val error: E, original: Any?) : MaybeData<E, T>(original) {
    override fun getOrThrow(): T = throw error
    override fun getOrNull(): T? = null
}

/**
 * Good Data. Contains the correct value with the correct type.
 */
class GoodData<E : Throwable, T>(val value: T) : MaybeData<E, T>(value) {
    override fun getOrThrow(): T = value
    override fun getOrNull(): T? = value
}
