package com.shoprunner.baleen.kotlin

import com.shoprunner.baleen.BaleenType
import kotlin.reflect.KClass

/**
 * The options to choose for which direction to interpret the CoericibleTypes
 */
enum class CoercibleHandlerOption {
    /**
     * Interpret the CoericibleType to use the FROM generics type. For example, from CoercibleType<StringType, LongType> use StringType
     */
    FROM,

    /**
     * Interpret the CoericibleType to use the FROM generics type. For example, from CoercibleType<StringType, LongType> use LongType
     */
    TO
}

/**
 * A representation of how a BaleenType can be overridden.
 */
data class TypeOverride(
    /**
     * A function that given a BaleenType, returns true if should override that type
     */
    val isOverridable: (BaleenType) -> Boolean,

    /**
     * A function that given a BaleenType, returns the KClass to override with.
     */
    val override: (BaleenType) -> KClass<out Any>
)

/**
 * The optional configuration that can change generating the data classes.
 */
data class Options(
    /**
     * Option for determining how a CoercibleType gets interpreted. Default is FROM.
     */
    val coercibleHandler: CoercibleHandlerOption = CoercibleHandlerOption.FROM,

    /**
     * Option for overriding BaleenTypes. Takes a list. Default is no overrides.
     */
    val typeOverrides: List<TypeOverride> = emptyList()
)
