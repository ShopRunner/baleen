package com.shoprunner.baleen

/**
 * Base class for good or bad data.
 */
sealed class MaybeData<E : Throwable, T>(val original: Any?) {
    abstract fun getOrThrow(): T
    abstract fun getOrNull(): T?
    abstract fun <R> map(f: (T)-> R): MaybeData<E, R>
    abstract fun <E2: Throwable, R> flatMap(f: (T)-> MaybeData<E2,R>): MaybeData<Throwable, R>
}

/**
 * Bad data. Contains the exception as well as the original value
 */
class BadData<E : Throwable, T>(val error: E, original: Any?) : MaybeData<E, T>(original) {
    override fun getOrThrow(): T = throw error
    override fun getOrNull(): T? = null
    override fun <R> map(f: (T) -> R): MaybeData<E, R> = BadData(this.error, this.original)
    override fun <E2: Throwable, R> flatMap(f: (T) -> MaybeData<E2, R>): MaybeData<Throwable, R> =
        BadData(this.error, this.original)
}

/**
 * Good Data. Contains the correct value with the correct type.
 */
class GoodData<E : Throwable, T>(val value: T) : MaybeData<E, T>(value) {
    override fun getOrThrow(): T = value
    override fun getOrNull(): T? = value
    override fun <R> map(f: (T) -> R): MaybeData<E, R> = GoodData(f(this.value))
    override fun <E2 : Throwable, R> flatMap(f: (T) -> MaybeData<E2, R>): MaybeData<Throwable, R> =
        when(val result = f(this.value)) {
            is GoodData -> GoodData(result.value)
            is BadData -> BadData(result.error, result.original)
        }
}

fun <T> MaybeData<*, MaybeData<*, T>>.flatten(): MaybeData<Throwable, T> = this.flatMap { it }
