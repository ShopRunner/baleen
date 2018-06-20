package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult

class MapType(val keyType: BaleenType, val valueType: BaleenType) : BaleenType {

    override fun name() = "map of occurrences of ${keyType.name()} to ${valueType.name()}"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when (value) {
                null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                value !is Map<*, *> -> sequenceOf(ValidationError(dataTrace, "is not a map", value))
                is Map<*, *> -> value.asSequence().withIndex().flatMap { (index, data) ->
                    keyType.validate(dataTrace.plus("index $index key"), data.key) +
                    valueType.validate(dataTrace.plus("index $index value"), data.value)
                }
                else -> emptySequence()
            }
}