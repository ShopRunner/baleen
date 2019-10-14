package com.shoprunner.baleen.kotlin

import com.shoprunner.baleen.BaleenType
import kotlin.reflect.KClass

enum class CoercibleHandler {
    FROM, TO
}

data class TypeMapOverride(
    val isOverridable: (BaleenType) -> Boolean,
    val override: (BaleenType) -> KClass<out Any>
)

data class Options(
    val coercibleHandler: CoercibleHandler = CoercibleHandler.FROM,
    val overrides: List<TypeMapOverride> = emptyList()
)
