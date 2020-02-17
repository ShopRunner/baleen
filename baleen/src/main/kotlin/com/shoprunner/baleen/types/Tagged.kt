package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.Validator

class Tagged(val type: BaleenType, val tags: Map<String, String>) : BaleenType {
    constructor(type: BaleenType, vararg tags: Pair<String, String>) : this(type, tags.toMap())

    override fun name(): String = type.name()

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
        type.validate(dataTrace.tag(tags), value)
}

fun BaleenType.tag(key: String, value: String): Tagged = Tagged(this, key to value)

fun BaleenType.tag(vararg tags: Pair<String, String>): Tagged = Tagged(this, *tags)

/**
 * Wraps the Validator function with a Validator function that add tags to the datatrace
 */
fun Validator.tag(vararg tags: Pair<String, String>): Validator = { dataTrace, data ->
    this(dataTrace.tag(*tags), data)
}
