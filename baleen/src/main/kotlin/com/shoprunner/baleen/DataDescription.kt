package com.shoprunner.baleen

class DataDescription(
    val name: String,
    val nameSpace: String = "",
    val markdownDescription: String
) : BaleenType {
    val attrs = mutableListOf<AttributeDescription>()
    private val tests = mutableListOf<Validator>()

    override fun name() = name

    private fun attrTest(attr: AttributeDescription, type: BaleenType) = fun(dataTrace: DataTrace, data: Data): Sequence<ValidationResult> {
        return when {
            data.containsKey(attr.name) -> {
                val value = data[attr.name]
                // TODO add to context
                val attrDataTrace = dataTrace + "attribute \"${attr.name}\""
                sequenceOf(ValidationInfo(dataTrace, "has attribute \"${attr.name}\"", data)).plus(type.validate(attrDataTrace, value))
            }
            attr.default is Default -> sequenceOf(ValidationInfo(dataTrace, "has attribute \"${attr.name}\" defaulted to `${attr.default.value}`. Ignoring the null value", data))
            attr.required -> sequenceOf(ValidationError(dataTrace, "missing required attribute \"${attr.name}\"", data))
            else -> sequenceOf(ValidationInfo(dataTrace, "missing attribute \"${attr.name}\"", data))
        }
    }

    fun attr(
        name: String,
        type: BaleenType,
        markdownDescription: String = "",
        aliases: Array<String> = arrayOf(),
        required: Boolean = false,
        default: DefaultValue = NoDefault
    ): AttributeDescription {
        val attr = AttributeDescription(this, name, type, markdownDescription, aliases, required, default)
        attrs.add(attr)
        tests.add(attrTest(attr, type))
        return attr
    }

    fun warnOnExtraAttributes() {
        tests.add(fun(dataTrace: DataTrace, data: Data): Sequence<ValidationResult> {
            val extraAttributes = data.keys - attrs.map { it.name }.toSet()
            return extraAttributes.asSequence().map { ValidationWarning(dataTrace, "extra attribute \"$it\"", data) }
        })
    }

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> {
        if (value == null) {
            return sequenceOf(ValidationError(dataTrace, "is null", value))
        }
        if (value !is Data) {
            return sequenceOf(ValidationError(dataTrace, "expected to be of type Data but is " + value.javaClass, value))
        }

        return tests.asSequence().flatMap { it(dataTrace, value) }
    }

    fun validate(ctx: Context): Validation {
        val results = tests.flatMap { it(ctx.dataTrace, ctx.data).asIterable() }

        if (results.none { it is ValidationError }) {
            // TODO should we have ValidationSuccess (it isn't very recursive)
            return Validation(ctx, results.plus(ValidationSuccess(ctx.dataTrace, ctx.data)))
        }

        return Validation(ctx, results)
    }

    fun validate(data: Data) = validate(Context(data, dataTrace()))

    fun test(validation: Validator) {
        tests.add(validation)
    }
}