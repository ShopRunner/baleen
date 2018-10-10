package com.shoprunner.baleen.jsonschema.v4

import com.shoprunner.baleen.BaleenType

/**
 * Define a mapper that maps Baleen types to JsonSchema types
 */
interface BaleenJsonSchemaMapper {
    /**
     * Given a BaleenType, return the corresponding Json Type as well as the updated object context.
     * @param baleenType The Baleen type that will be mapped. It can be a simple type like IntType or even DataDescription.
     * @param objectContext The complex, non-primitive JsonSchema already created (usually based off a DataDescription). Map of ObjectSchema's $ref to ObjectSchema.
     * @param withAdditionalAttributes Flag that determines if `additionalAttributes` flag is set. This should be propogated throughout the schema.
     */
    fun getJsonSchema(baleenType: BaleenType, objectContext: ObjectContext, withAdditionalAttributes: Boolean): Pair<JsonSchema, ObjectContext>
}