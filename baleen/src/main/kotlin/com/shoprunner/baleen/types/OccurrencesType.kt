package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult

class OccurrencesType(val memberType: BaleenType) : BaleenType {

    override fun name() = "multiple occurrences of ${memberType.name()}"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when (value) {
                null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                is Iterable<*> -> value.asSequence().withIndex().flatMap { (index, data) ->
                    memberType.validate(dataTrace.plus("index $index"), data)
                }
                else -> {
                    memberType.validate(dataTrace.plus("index 0"), value)
                }
            }
}