package com.shoprunner.baleen

/**
 * Data retrieved can be either "good" because it is of the correct type. Or it is "bad"
 * and some Throwable was created.
 *
 * @param original the original data if present or null.
 */
sealed class MaybeData<E : Throwable, T>(val original: Any?) {
    /**
     * Retrieve the value or throw the Throwable.
     *
     * @return the value
     * @throws E if bad data
     */
    abstract fun getOrThrow(): T

    /**
     * Retrieve the value or return null.
     *
     * @return the value or null
     */
    abstract fun getOrNull(): T?

    /**
     * Given a function, apply the function on the value and return a new MaybeData.
     * @param f map function taking T and returning R
     * @throws Throwable if one occurs in the map function
     */
    abstract fun <R> map(f: (T) -> R): MaybeData<E, R>

    /**
     * Given a function, apply the function on the value and return a new MaybeData. If a
     * Throwable occurs, then the error is handled by returning [BadData].
     *
     * @param f map function taking T and returning R
     * @return [GoodData] if mapping is successful or [BadData] if error occures.
     */
    abstract fun <R> tryMap(f: (T) -> R): MaybeData<Throwable, R>

    /**
     * Given a function that also returns [MaybeData], apply that function and return the result.
     *
     * @param f map function taking T and returning [MaybeData]<E2, R>
     * @return [MaybeData] from [f] or [BadData] if this has an exception.
     */
    abstract fun <E2 : Throwable, R> flatMap(f: (T) -> MaybeData<E2, R>): MaybeData<Throwable, R>
}

/**
 * Bad data. Contains the exception as well as the original value
 *
 * @param error the throwable error that occurred.
 * @param original the original data if present or null.
 */
class BadData<E : Throwable, T>(val error: E, original: Any?) : MaybeData<E, T>(original) {
    override fun getOrThrow(): T = throw error
    override fun getOrNull(): T? = null
    override fun <R> map(f: (T) -> R): MaybeData<E, R> = BadData(this.error, this.original)
    override fun <R> tryMap(f: (T) -> R): MaybeData<Throwable, R> = BadData(this.error, this.original)
    override fun <E2 : Throwable, R> flatMap(f: (T) -> MaybeData<E2, R>): MaybeData<Throwable, R> =
        BadData(this.error, this.original)
}

/**
 * Good Data. Contains the correct value with the correct type.
 *
 * @param value the value correctly typed.
 * @param original the original data if present or null.
 */
class GoodData<E : Throwable, T>(val value: T, original: Any? = value) : MaybeData<E, T>(original) {
    override fun getOrThrow(): T = value
    override fun getOrNull(): T? = value
    override fun <R> map(f: (T) -> R): MaybeData<E, R> = GoodData(f(this.value), this.original)
    override fun <R> tryMap(f: (T) -> R): MaybeData<Throwable, R> =
        try {
            GoodData(f(this.value), this.original)
        } catch (e: Throwable) {
            BadData(e, this.original)
        }
    override fun <E2 : Throwable, R> flatMap(f: (T) -> MaybeData<E2, R>): MaybeData<Throwable, R> =
        when (val result = f(this.value)) {
            is GoodData -> GoodData(result.value, result.original)
            is BadData -> BadData(result.error, result.original)
        }
}

/**
 * Flatten nested [MaybeData]'s calling [MaybeData.flatMap].
 *
 * @return flattened [MaybeData].
 */
fun <T> MaybeData<*, MaybeData<*, T>>.flatten(): MaybeData<Throwable, T> = this.flatMap { it }
