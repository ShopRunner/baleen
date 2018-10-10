package com.shoprunner.baleen.jsonschema.v4

import com.shoprunner.baleen.BaleenType

/**
 * Interface that extends the Baleen mapping function and adds a `matches` method. This is
 * designed to make sure that only Baleen types that match are overridden.
 */
interface BaleenOverride : (BaleenType, ObjectContext) -> Pair<JsonSchema, ObjectContext> {
    fun matches(baleenType: BaleenType): Boolean
}

/**
 * Maps a `BaleenType` to a `JsonSchema`. Use this signature for overriding BaleenTypes where
 * the result is a JsonSchema primitive (ie, not a RefType that points to an ObjectSchema).
 * use `((B) -> Pair<JsonSchema, ObjectContext>).asBaleenObjectOverride()` for more
 * complicated use-cases.
 */
inline fun <reified B : BaleenType> ((B) -> JsonSchema).asBaleenOverride(): BaleenOverride {
    val receiver = this
    return object : BaleenOverride {
        override fun matches(baleenType: BaleenType): Boolean {
            return baleenType is B
        }

        override fun invoke(baleenType: BaleenType, context: ObjectContext): Pair<JsonSchema, Map<String, ObjectSchema>> {
            return receiver(baleenType as B) to emptyMap()
        }
    }
}

/**
 * Maps a `BaleenType` to a `JsonSchema` but also includes the `ObjectContext` so that implementations can use and
 * append to the ObjectContext as needed. The context parameter is immutable and if updated, needs to be returned.
 *
 * The name parameter is used for an additional filter on the `BaleenType`'s name. This is important for DataDescriptions
 */
inline fun <reified B : BaleenType> ((B, ObjectContext) -> Pair<JsonSchema, Map<String, ObjectSchema>>).asBaleenObjectOverrideFor(name: String? = null): BaleenOverride {
    val receiver = this
    return object : BaleenOverride {
        override fun matches(baleenType: BaleenType): Boolean {
            return baleenType is B && (name?.let { baleenType.name() == name} ?: true)
        }

        override fun invoke(baleenType: BaleenType, context: ObjectContext): Pair<JsonSchema, Map<String, ObjectSchema>> {
            return receiver(baleenType as B, context)
        }
    }
}
