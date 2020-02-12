package com.shoprunner.baleen

import com.shoprunner.baleen.types.asWarnings

class AttributeDescription(
    val dataDescription: DataDescription,
    val name: String,
    val type: BaleenType,
    val markdownDescription: String,
    val aliases: Array<String>,
    val required: Boolean,
    val default: Any?
) {
    private val tests: MutableList<Validator> = mutableListOf()
    private var warn: Boolean = false

    internal val allTests: List<Validator>
        get() {
            val allTests = tests + this::validate
            return if (warn) {
                allTests.map { it.asWarnings() }
            } else {
                allTests
            }
        }

    fun test(validator: Validator) {
        // TODO change context
        tests.add(validator)
    }

    fun describe(block: (AttributeDescription) -> Unit) {
        block(this)
    }

    fun asWarnings(): AttributeDescription {
        warn = true
        return this
    }

    private fun validate(dataTrace: DataTrace, data: Data): Sequence<ValidationResult> {
        return when {
            data.containsKey(name) -> {
                val (value, attrDataTrace) = data.attributeDataValue(name, dataTrace)
                sequenceOf(ValidationInfo(dataTrace, "has attribute \"${name}\"", data)).plus(
                    type.validate(
                        attrDataTrace,
                        value
                    )
                )
            }
            default != NoDefault -> sequenceOf(
                ValidationInfo(
                    dataTrace,
                    "has attribute \"${name}\" defaulted to `$default` since it wasn't set.",
                    data
                )
            )
            required -> sequenceOf(ValidationError(dataTrace, "missing required attribute \"${name}\"", data))
            else -> sequenceOf(ValidationInfo(dataTrace, "missing attribute \"${name}\"", data))
        }
    }
}
