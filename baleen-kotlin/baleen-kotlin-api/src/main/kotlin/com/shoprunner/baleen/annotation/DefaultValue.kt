package com.shoprunner.baleen.annotation

import kotlin.reflect.KClass

/**
 * For manually setting default values for attributes.  Currently annotation processing
 * does not support reading default values specified in code.
 *
 * Currently supports
 *  * Null
 *  * Boolean
 *  * String
 *  * Int
 *  * Long
 *  * BigInteger
 *  * Float
 *  * Double
 *  * BigDecimal
 *  * DataClass
 *  * EmptyArray
 *  * EmptyList
 *  * EmptySet
 *  * EmptyMap
 */
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FIELD)
@MustBeDocumented
annotation class DefaultValue(
    /**
     * Required. Tells that annotation processor type of default value to generate.
     */
    val type: DefaultValueType,

    /**
     * Used when `type = String`. Defaults to "".
     *
     * ```
     * type = DataValueType.String,
     * defaultStringValue = "hello"
     * ```
     *
     * becomes
     *
     * ```
     * default = "hello"
     * ```
     *
     * Also used for `type = BigInteger` or `type = BigDecimal` since these are string backed numeric types and can
     * be larger and have better precision than Double and Long.
     *
     * ```
     * type = DataValueType.BigInteger,
     * defaultStringValue = "100"
     * ```
     *
     * becomes
     *
     * ```
     * default = "100".toBigInteger()
     * ```
     *
     * and
     *
     * ```
     * type = DataValueType.BigDecimal,
     * defaultStringValue = "100.00"
     * ```
     *
     * becomes
     *
     * ```
     * default = "100.00".toBigDecimal()
     * ```
     */
    val defaultStringValue: String = "",

    /**
     * Used when `type = Boolean`. Defaults to false.
     *
     * ```
     * type = DataValueType.Boolean,
     * defaultBooleanValue = true
     * ```
     *
     * becomes
     *
     * ```
     * default = true
     * ```
     */
    val defaultBooleanValue: Boolean = false,

    /**
     * Used when `type = Int`. Defaults to 0.
     *
     * ```
     * type = DataValueType.Int,
     * defaultIntValue = 100
     * ```
     *
     * becomes
     *
     * ```
     * default = 100
     * ```
     */
    val defaultIntValue: Int = 0,

    /**
     * Used when `type = Long`. Defaults to 0L.
     *
     * ```
     * type = DataValueType.Long,
     * defaultLongValue = 100L
     * ```
     *
     * becomes
     *
     * ```
     * default = 100L
     * ```
     */
    val defaultLongValue: Long = 0L,

    /**
     * Used when `type = Float`. Defaults to 0.0f.
     *
     * ```
     * type = DataValueType.Float,
     * defaultFloatValue = 9.9f
     * ```
     *
     * becomes
     *
     * ```
     * default = 9.9f
     * ```
     */
    val defaultFloatValue: Float = 0.0f,

    /**
     * Used when `type = Double`. Defaults to 0.0.
     *
     * ```
     * type = DataValueType.Double,
     * defaultDoubleValue = 9.9
     * ```
     *
     * becomes
     *
     * ```
     * default = 9.9
     * ```
     */
    val defaultDoubleValue: Double = 0.0,

    /**
     * Used when `type = DataClass`. Code generator is the class with
     * an empty initializer
     *
     * ```
     * type = DataValueType.DataClass,
     * defaultDataClassValue = MyClass::class
     * ```
     *
     * becomes
     *
     * ```
     * default = MyClass()
     * ```
     *
     */
    val defaultDataClassValue: KClass<out Any> = Any::class,

    /**
     * The key class for a Map. Paired with defaultElementClass for value.
     *
     * ```
     * type = DataValueType.EmptyMap,
     * defaultKeyClass = String::class,
     * defaultElementClass = Int::class
     * ```
     *
     * becomes
     *
     * ```
     * default = emptyMap<String, Int>()
     * ```
     *
     */
    val defaultKeyClass: KClass<out Any> = Any::class,

    /**
     * The element class used for EmptyArray, EmptyList, EmptySet as well as the value for Map. Defaults to `Any::class`
     *
     * ```
     * type = DataValueType.EmptyList,
     * defaultElementClass = Int::class
     * ```
     *
     * becomes
     *
     * ```
     * default = emptyList<Int>()
     * ```
     *
     * The same is true for EmptyArray and EmptySet. For EmptyMap, defaultKeyClass is also to be used.
     *
     */
    val defaultElementClass: KClass<out Any> = Any::class
)

/**
 * Supported Default Value types
 */
enum class DefaultValueType {
    Null, Boolean, String, Int, Long, BigInteger, Float, Double, BigDecimal, DataClass, EmptyArray, EmptyList, EmptySet, EmptyMap
}
