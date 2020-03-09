package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.Validator

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
fun withConstantValue(value: String): Tagger = { value }

/**
 * Value tagger always returns the value of the data passed in
 */
fun withValue(): Tagger = { it?.toString() ?: "null" }

/**
 * Dynamic tagger returns the value set for
 */
fun withAttributeValue(attrName: String): Tagger = {
    when {
        it is Data && it.containsKey(attrName) -> it[attrName]?.toString() ?: "null"
        else -> "attr_not_found"
    }
}

/**
 * Wraps the BaleenType with a
 */
fun BaleenType.tag(tag: String, value: String): Tagged =
        Tagged(this, tag to withConstantValue(value))

fun BaleenType.tag(tag: String, tagger: Tagger): Tagged =
        Tagged(this, tag to tagger)

fun BaleenType.tag(vararg tags: Pair<String, Tagger>): Tagged =
        Tagged(this, *tags)

/**
 * Wraps the Validator function with a Validator function that add tags to the datatrace
 */
fun Validator.tag(vararg tags: Pair<String, Tagger>): Validator = { dataTrace, data ->
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
