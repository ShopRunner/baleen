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
    private val tags = mutableMapOf<String, String>()
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

    fun test(validator: Validator): AttributeDescription {
        // TODO change context
        tests.add(validator)
        return this
    }

    fun describe(block: (AttributeDescription) -> Unit): AttributeDescription {
        block(this)
        return this
    }

    fun asWarnings(): AttributeDescription {
        warn = true
        return this
    }

    fun tag(key: String, value: String): AttributeDescription {
        tags[key] = value
        return this
    }

    fun tag(tags: Map<String, String>): AttributeDescription {
        this.tags.putAll(tags)
        return this
    }

    fun tag(vararg tags: Pair<String, String>): AttributeDescription {
        this.tags.putAll(tags)
        return this
    }

    private fun validate(dataTrace: DataTrace, data: Data): Sequence<ValidationResult> {
        val taggedDataTrace = dataTrace.tag(tags)
        return when {
            data.containsKey(name) -> {
                val (value, attrDataTrace) = data.attributeDataValue(name, taggedDataTrace)
                sequenceOf(ValidationInfo(taggedDataTrace, "has attribute \"${name}\"", data)).plus(
                    type.validate(
                        attrDataTrace,
                        value
                    )
                )
            }
            default != NoDefault -> sequenceOf(
                ValidationInfo(
                    taggedDataTrace,
                    "has attribute \"${name}\" defaulted to `$default` since it wasn't set.",
                    data
                )
            )
            required -> sequenceOf(ValidationError(taggedDataTrace, "missing required attribute \"${name}\"", data))
            else -> sequenceOf(ValidationInfo(taggedDataTrace, "missing attribute \"${name}\"", data))
        }
    }
}
