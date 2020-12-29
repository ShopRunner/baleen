package com.shoprunner.baleen

class DataDescription(
    val name: String,
    val nameSpace: String = "",
    val markdownDescription: String
) : BaleenType {
    val attrs = mutableListOf<AttributeDescription>()
    private val tests = mutableListOf<Validator>()

    override fun name() = name

    fun attr(
        name: String,
        type: BaleenType,
        markdownDescription: String = "",
        aliases: Array<String> = arrayOf(),
        required: Boolean = false,
        default: Any? = NoDefault,
    ): AttributeDescription {
        val attr = AttributeDescription(this, name, type, markdownDescription, aliases, required, default)
        attrs.add(attr)
        return attr
    }

    /**
     * Create an attribute with a nested data description.  The nested data description will have the
     * same name as the attribute but capitalized.
     */
    fun attr(
        name: String,
        markdownDescription: String = "",
        aliases: Array<String> = arrayOf(),
        required: Boolean = false,
        default: Any? = NoDefault,
        description: DataDescription.() -> Unit = {},
    ): AttributeDescription {
        val dd = DataDescription(
            name = name.capitalize(),
            markdownDescription = markdownDescription
        )
        dd.apply(description)

        val attr = AttributeDescription(this, name, dd, markdownDescription, aliases, required, default)
        attrs.add(attr)
        return attr
    }

    fun String.type(
        type: BaleenType,
        markdownDescription: String = "",
        aliases: Array<String> = arrayOf(),
        required: Boolean = false,
        default: Any? = NoDefault,
    ): AttributeDescription {
        val attr = AttributeDescription(this@DataDescription, this, type, markdownDescription, aliases, required, default)
        attrs.add(attr)
        return attr
    }

    /**
     * Create an attribute with a nested data description.  The nested data description will have the
     * same name as the attribute but capitalized.
     */
    fun String.type(
        markdownDescription: String = "",
        aliases: Array<String> = arrayOf(),
        required: Boolean = false,
        default: Any? = NoDefault,
        description: DataDescription.() -> Unit = {},
    ): AttributeDescription {
        val dd = DataDescription(
            name = this.capitalize(),
            markdownDescription = markdownDescription
        )
        dd.apply(description)

        val attr = AttributeDescription(this@DataDescription, this, dd, markdownDescription, aliases, required, default)
        attrs.add(attr)
        return attr
    }

    fun warnOnExtraAttributes() {
        tests.add(
            fun(dataTrace: DataTrace, data: Data): Sequence<ValidationResult> {
                val extraAttributes = data.keys - attrs.map { it.name }.toSet()
                return extraAttributes.asSequence().map { ValidationWarning(dataTrace, "extra attribute \"$it\"", data) }
            }
        )
    }

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> {
        if (value == null) {
            return sequenceOf(ValidationError(dataTrace, "is null", value))
        }
        if (value !is Data) {
            return sequenceOf(ValidationError(dataTrace, "expected to be of type Data but is " + value.javaClass, value))
        }

        return allTests.asSequence().flatMap { it(dataTrace, value) }
    }

    fun validate(ctx: Context): Validation {
        val results = allTests.flatMap { it(ctx.dataTrace, ctx.data).asIterable() }

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

    private val allTests: List<Validator>
        get() = attrs.flatMap { it.allTests } + tests
}
