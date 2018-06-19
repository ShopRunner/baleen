package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult

class EnumType(val enum: List<String>) : BaleenType {

    constructor(vararg enum: String) : this(enum.toList())

    constructor(enum: Array<out Enum<*>>) : this(enum.map { it.name })


    override fun name() = "enum"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when {
                value == null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                value is String && !enum.contains(value) ->
                    sequenceOf(ValidationError(dataTrace,
                            "is not contained in enum [${enum.joinToString(", ")}]",
                            value))
                value is Enum<*> && !enum.contains(value.name) ->
                    sequenceOf(ValidationError(dataTrace,
                            "is not contained in enum [${enum.joinToString(", ")}]",
                            value))
                else -> emptySequence()
            }
}