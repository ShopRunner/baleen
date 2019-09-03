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
 *  * Float
 *  * Double
 *  * DataClass
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
     * Used when type = String. Defaults to ""
     */
    val defaultStringValue: String = "",

    /**
     * Used when type = Boolean. Defaults to false
     */
    val defaultBooleanValue: Boolean = false,

    /**
     * Used when type = Int. Defaults to 0
     */
    val defaultIntValue: Int = 0,

    /**
     * Used when type = Long. Defaults to 0L
     */
    val defaultLongValue: Long = 0L,

    /**
     * Used when type = Float. Defaults to 0.0f
     */
    val defaultFloatValue: Float = 0.0f,

    /**
     * Used when type = Double. Defaults to 0.0
     */
    val defaultDoubleValue: Double = 0.0,

    /**
     * Used when type = DataClass. Code generator is the class with
     * an empty initializer
     *
     * ```
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

    val defaultKeyClass: KClass<out Any> = Any::class,

    val defaultElementClass: KClass<out Any> = Any::class
)

/**
 * Supported Default Value types
 */
enum class DefaultValueType {
    Null, Boolean, String, Int, Long, Float, Double, DataClass, EmptyArray, EmptyList, EmptySet, EmptyMap
}
