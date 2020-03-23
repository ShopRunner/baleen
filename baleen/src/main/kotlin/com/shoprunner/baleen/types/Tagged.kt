package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.Context
import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.Validation
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.Validator
import com.shoprunner.baleen.dataTrace

/**
 * BaleenType extension that encapsulates new tags used to markup the other BaleenTypes during the validation output.
 *
 * @param type the BaleenType to tag.
 * @param tags the tags to apply to the ValidationResults.
 */
class Tagged(val type: BaleenType, val tags: Map<String, Tagger>) : BaleenType {
    constructor(type: BaleenType, vararg tags: Pair<String, Tagger>) : this(type, tags.toMap())

    override fun name(): String = type.name()

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
        type.validate(dataTrace.tag(tags.mapValues { it.value(value) }), value)
}

/**
 * A Tagging Function that is called when Validate is called.
 */
typealias Tagger = (Any?) -> String

/**
 * Constant tagger always returns the tag value passed in
 */
fun withConstantValue(value: String): Tagger = WithConstantValue(value)

class WithConstantValue(val value: String) : Tagger {
    override fun invoke(data: Any?): String = value
}

/**
 * Value tagger always returns the value of the data passed in
 */
fun withValue(): Tagger = WithValue()

class WithValue() : Tagger {
    override fun invoke(data: Any?): String = data?.toString() ?: "null"
}

/**
 * Dynamic tagger returns the value set for
 */
fun withAttributeValue(attrName: String): Tagger = WithAttributeValue(attrName)

class WithAttributeValue(val attrName: String) : Tagger {
    override fun invoke(data: Any?): String = when {
        data is Data && data.containsKey(attrName) -> data[attrName]?.toString() ?: "null"
        else -> "attr_not_found"
    }
}

/**
 * Tags a BaleenType with a tag and value that will appear on the DataTrace on the results.
 */
fun BaleenType.tag(tag: String, value: String): Tagged =
        Tagged(this, tag to withConstantValue(value))

/**
 * Tags a BaleenType with a tag and Tagger function that will evaluate on the input data and appear on the DataTrace on
 * the results.
 */
fun BaleenType.tag(tag: String, tagger: Tagger): Tagged =
        Tagged(this, tag to tagger)

/**
 * Tags a BaleenType with a tags and Tagger functions that will evaluate on the input data and appear on the DataTrace on
 * the results.
 */
fun BaleenType.tag(vararg tags: Pair<String, Tagger>): Tagged =
        Tagged(this, *tags)

/**
 * Tags a BaleenType with a map of tags to Tagger functions that will evaluate on the input data and appear on the DataTrace on
 * the results.
 */
fun BaleenType.tag(tags: Map<String, Tagger>): Tagged =
    Tagged(this, tags)

/**
 * Wraps the Validator function with a Validator function that add tags to the datatrace
 */
fun Validator.tag(vararg tags: Pair<String, Tagger>): Validator = { dataTrace, data ->
    this(dataTrace.tag(tags.map { (k, v) -> k to v(data) }.toMap()), data)
}

/**
 * Wraps the Validator function with a Validator function that add tags to the datatrace
 */
fun Validator.tag(tags: Map<String, Tagger>): Validator = { dataTrace, data ->
    this(dataTrace.tag(tags.map { (k, v) -> k to v(data) }.toMap()), data)
}

/**
 * Wraps the Validator function with a Validator function that add tags to the datatrace
 */
fun Validator.tag(tag: String, value: String): Validator =
    this.tag(tag to withConstantValue(value))

/**
 * Wraps the Validator function with a Validator function that add tags to the datatrace
 */
fun Validator.tag(tag: String, tagger: Tagger): Validator =
    this.tag(tag to tagger)

/**
 * Given a context, return the Validation result. Useful when tagging DataDescription and wanting to use same validate method
 * signature
 */
fun Tagged.validate(ctx: Context): Validation {
    return Validation(ctx, validate(ctx.dataTrace, ctx.data).asIterable())
}

/**
 * Given Data, return the Validation result. Useful when tagging DataDescription and wanting to use same validate method
 * signature
 */
fun Tagged.validate(data: Data): Validation = validate(Context(data, dataTrace()))
