package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult

/**
 * Union of a number of other types, if any types are valid then the type is valid.  If none of the types are valid
 * then all errors are returned.
 */
class UnionType(vararg val types: BaleenType) : BaleenType {
    override fun name(): String = "union of ${types.joinToString { it.name() }}"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> {
        val successful = types.any { !it.validate(dataTrace, value).any { it is ValidationError } }
        return if (successful) {
            emptySequence()
        } else {
            types.asSequence().flatMap { it.validate(dataTrace, value) }
        }
    }
}
