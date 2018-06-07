package com.shoprunner.baleen

class AttributeDescription(
    val dataDescription: DataDescription,
    val name: String,
    val type: BaleenType,
    val markdownDescription: String,
    val aliases: Array<String>,
    val required: Boolean
) {
    fun test(validator: Validator) {
        // TODO change context
        dataDescription.test(validator)
    }

    fun describe(block: (AttributeDescription) -> Unit) {
        block(this)
    }
}