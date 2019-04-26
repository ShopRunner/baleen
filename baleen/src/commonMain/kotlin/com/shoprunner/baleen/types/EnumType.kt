package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult

class EnumType(val enumName: String, val enum: List<String>) : BaleenType {

    constructor(enumName: String, vararg enum: String) : this(enumName, enum.toList())

    constructor(enumName: String, enum: Array<out Enum<*>>) : this(enumName, enum.map { it.name })

    private val enumStr = enum.joinToString(", ")

    override val name = "enum"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when {
                value == null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                value is String && !enum.contains(value) ->
                    sequenceOf(ValidationError(dataTrace,
                            "is not contained in enum $enumName [$enumStr]",
                            value))
                value is Enum<*> && !enum.contains(value.name) ->
                    sequenceOf(ValidationError(dataTrace,
                            "is not contained in enum $enumName [$enumStr]",
                            value))
                else -> emptySequence()
            }
}