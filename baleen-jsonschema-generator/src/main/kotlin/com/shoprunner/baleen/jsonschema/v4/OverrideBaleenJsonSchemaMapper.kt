package com.shoprunner.baleen.jsonschema.v4

import com.shoprunner.baleen.BaleenType

/**
 * A solution to provide point overrides to individual `BaleenType` and uses the `DefaultBaleenJsonMapper` for the rest.
 * The constructor takes a list of `(BaleenType) -> Pair<JsonSchema, ObjectContext>` functions casted to `BaleenOverride` alias.
 *
 * To use, cast a function of either type `(BaleenType) -> JsonSchema` or `(BaleenType) -> Pair<JsonSchema, ObjectContext>`
 *
 * ```
 * fun mapIntType(intType: IntType): JsonSchema { ... }
 * fun mapDogType(dogType: DogType, context: ObjectContext): Pair<JsonSchema, ObjectContext> { ... }
 * fun mapPackType(packType: packType, context: ObjectContext): Pair<JsonSchema, ObjectContext> { ... }
 *
 * val overrideMapper = OverrideBaleenJsonSchemaMapper(
 *      (::mapIntType).asBaleenOverride(),
 *      (::mapDogType).asBaleenObjectOverrideFor("Dog"),
 *      (::mapDogType).asBaleenObjectOverrideFor("Pack")
 * )
 * ```
 */
class OverrideBaleenJsonSchemaMapper(val mappingOverrides: List<BaleenOverride>) : DefaultBaleenJsonSchemaMapper() {

    override fun getJsonSchema(baleenType: BaleenType, objectContext: ObjectContext, withAdditionalAttributes: Boolean): Pair<JsonSchema, ObjectContext> {
        val overrideFun = mappingOverrides.firstOrNull { it.matches(baleenType) }
        if (overrideFun != null) {
            val (jsonSchema, overrideObjectContext) = overrideFun(baleenType, objectContext)
            return jsonSchema to (objectContext + overrideObjectContext)
        }

        return super.getJsonSchema(baleenType, objectContext, withAdditionalAttributes)
    }
}