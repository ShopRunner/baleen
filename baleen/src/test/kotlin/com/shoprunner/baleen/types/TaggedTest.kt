package com.shoprunner.baleen.types

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.Context
import com.shoprunner.baleen.Data
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.TestHelper.dataOf
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.dataTrace
import com.shoprunner.baleen.datawrappers.HashData
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@Suppress("UNUSED_PARAMETER")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class TaggedTest {
    @Test
    fun `test Tagged adds static tags to BaleenType`() {
        val type = StringType(min = 2).tag("tag", "value")

        assertThat(type.validate(dataTrace(), "")).containsExactly(
            ValidationError(
                dataTrace().tag("tag", "value"),
                "is not at least 2 characters",
                ""
            )
        )
    }

    @Test
    fun `test Tagged adds dynamic tags to BaleenType`() {
        val type = StringType(min = 2).tag("tag", withValue())

        assertThat(type.validate(dataTrace(), "a")).containsExactly(
            ValidationError(
                dataTrace().tag("tag", "a"),
                "is not at least 2 characters",
                "a"
            )
        )
    }

    @Test
    fun `test Tagged adds custom dynamic tags to BaleenType`() {
        val type = StringType(min = 2).tag("tag") {
            when (it) {
                null -> "null"
                is String -> "string"
                is Int -> "int"
                else -> "other"
            }
        }

        assertThat(type.validate(dataTrace(), 1)).containsExactly(
            ValidationError(
                dataTrace().tag("tag", "int"),
                "is not a string",
                1
            )
        )
    }

    @Test
    fun `test Tagged adds multiple tags to BaleenType`() {
        val type = StringType(min = 2).tag(
            "staticTag" to withConstantValue("value"),
            "dynamicTag" to withValue()
        )

        assertThat(type.validate(dataTrace(), "a")).containsExactly(
            ValidationError(
                dataTrace().tag("staticTag" to "value", "dynamicTag" to "a"),
                "is not at least 2 characters",
                "a"
            )
        )
    }

    @Test
    fun `test Tagged adds multiple tags in a map to BaleenType`() {
        val type = StringType(min = 2).tag(mapOf(
            "staticTag" to withConstantValue("value"),
            "dynamicTag" to withValue()
        ))

        assertThat(type.validate(dataTrace(), "a")).containsExactly(
            ValidationError(
                dataTrace().tag("staticTag" to "value", "dynamicTag" to "a"),
                "is not at least 2 characters",
                "a"
            )
        )
    }

    @Test
    fun `test Validator with static tag`() {
        fun customValidator(dataTrace: DataTrace, data: Data): Sequence<ValidationResult> {
            return sequenceOf(ValidationInfo(dataTrace, "Validator was called", null))
        }

        val taggedValidator = ::customValidator.tag("tag", "value")

        assertThat(taggedValidator(dataTrace(), HashData(emptyMap()))).containsExactly(
            ValidationInfo(
                dataTrace().tag("tag", "value"),
                "Validator was called",
                null
            )
        )
    }

    @Test
    fun `test Validator with dynamic tag`() {
        fun customValidator(dataTrace: DataTrace, data: Data): Sequence<ValidationResult> {
            return sequenceOf(ValidationInfo(dataTrace, "Validator was called", null))
        }

        val taggedValidator = ::customValidator.tag("tag", withAttributeValue("key"))

        assertThat(taggedValidator(dataTrace(), dataOf("key" to "value"))).containsExactly(
            ValidationInfo(
                dataTrace().tag("tag", "value"),
                "Validator was called",
                null
            )
        )
    }

    @Test
    fun `test Validator with multiple tags`() {
        fun customValidator(dataTrace: DataTrace, data: Data): Sequence<ValidationResult> {
            return sequenceOf(ValidationInfo(dataTrace, "Validator was called", null))
        }

        val taggedValidator = ::customValidator.tag(
            "staticTag" to withConstantValue("value"),
            "dynamicTag" to withAttributeValue("key")
        )

        assertThat(taggedValidator(dataTrace(), dataOf("key" to "attrValue"))).containsExactly(
            ValidationInfo(
                dataTrace().tag("staticTag" to "value", "dynamicTag" to "attrValue"),
                "Validator was called",
                null
            )
        )
    }

    @Test
    fun `test Validator with multiple tags in a map`() {
        fun customValidator(dataTrace: DataTrace, data: Data): Sequence<ValidationResult> {
            return sequenceOf(ValidationInfo(dataTrace, "Validator was called", null))
        }

        val taggedValidator = ::customValidator.tag(mapOf(
            "staticTag" to withConstantValue("value"),
            "dynamicTag" to withAttributeValue("key")
        ))

        assertThat(taggedValidator(dataTrace(), dataOf("key" to "attrValue"))).containsExactly(
            ValidationInfo(
                dataTrace().tag("staticTag" to "value", "dynamicTag" to "attrValue"),
                "Validator was called",
                null
            )
        )
    }

    @Test
    fun `test DataDescription with static tag`() {
        val dogDescription = "Dog".describeAs {
            "name".type(
                type = AllowsNull(StringType()),
                required = true
            )
        }.tag("tag", "value")

        val data = dataOf("name" to "Fido")
        assertThat(dogDescription.validate(dataTrace(), data)).containsExactly(
            ValidationInfo(
                dataTrace().tag("tag", "value"),
                "has attribute \"name\"",
                data
            )
        )
    }

    @Test
    fun `test DataDescription with dynamic tag`() {
        val dogDescription = "Dog".describeAs {
            "name".type(
                type = AllowsNull(StringType()),
                required = true
            )
        }.tag("tag", withAttributeValue("name"))

        val data = dataOf("name" to "Fido")
        assertThat(dogDescription.validate(dataTrace(), data)).containsExactly(
            ValidationInfo(
                dataTrace().tag("tag", "Fido"),
                "has attribute \"name\"",
                data
            )
        )
    }

    @Test
    fun `test DataDescription with multiple tags`() {
        val dogDescription = "Dog".describeAs {
            "name".type(
                type = AllowsNull(StringType()),
                required = true
            )
        }.tag(
            "staticTag" to withConstantValue("value"),
            "dynamicTag" to withAttributeValue("name")
        )

        val data = dataOf("name" to "Fido")
        assertThat(dogDescription.validate(dataTrace(), data)).containsExactly(
            ValidationInfo(
                dataTrace().tag("staticTag" to "value", "dynamicTag" to "Fido"),
                "has attribute \"name\"",
                data
            )
        )
    }

    @Test
    fun `test AttributeDescription with static tag`() {
        val dogDescription = "Dog".describeAs {
            "name".type(
                type = AllowsNull(StringType()),
                required = true
            ).tag("tag", "value")
        }

        val data = dataOf("name" to "Fido")
        assertThat(dogDescription.validate(dataTrace(), data)).containsExactly(
            ValidationInfo(
                dataTrace().tag("tag" to "value"),
                "has attribute \"name\"",
                data
            )
        )
    }

    @Test
    fun `test AttributeDescription with dynamic tag`() {
        val dogDescription = "Dog".describeAs {
            "name".type(
                type = AllowsNull(StringType()),
                required = true
            ).tag("tag", withAttributeValue("name"))
        }

        val data = dataOf("name" to "Fido")
        assertThat(dogDescription.validate(dataTrace(), data)).containsExactly(
            ValidationInfo(
                dataTrace().tag("tag" to "Fido"),
                "has attribute \"name\"",
                data
            )
        )
    }

    @Test
    fun `test AttributeDescription with unknown attribute tag`() {
        val dogDescription = "Dog".describeAs {
            "name".type(
                type = AllowsNull(StringType()),
                required = true
            ).tag("tag", withAttributeValue("unknown"))
        }

        val data = dataOf("name" to "Fido")
        assertThat(dogDescription.validate(dataTrace(), data)).containsExactly(
            ValidationInfo(
                dataTrace().tag("tag" to "attr_not_found"),
                "has attribute \"name\"",
                data
            )
        )
    }

    @Test
    fun `test AttributeDescription with multiple tags`() {
        val dogDescription = "Dog".describeAs {
            "name".type(
                type = AllowsNull(StringType()),
                required = true
            ).tag(
                "staticTag" to withConstantValue("value"),
                "dynamicTag" to withAttributeValue("name")
            )
        }

        val data = dataOf("name" to "Fido")
        assertThat(dogDescription.validate(dataTrace(), data)).containsExactly(
            ValidationInfo(
                dataTrace().tag("staticTag" to "value", "dynamicTag" to "Fido"),
                "has attribute \"name\"",
                data
            )
        )
    }

    @Test
    fun `test Tagged validation with a Context input`() {
        val dogDescription = "Dog".describeAs {
            "name".type(
                type = AllowsNull(StringType()),
                required = true
            )
        }.tag("tag", "value")

        val data = dataOf("name" to "Fido")
        val validation = dogDescription.validate(Context(data, dataTrace()))
        assertThat(validation.results.asSequence()).containsExactly(
            ValidationInfo(
                dataTrace().tag("tag", "value"),
                "has attribute \"name\"",
                data
            )
        )
    }

    @Test
    fun `test Tagged validation with a Data input`() {
        val dogDescription = "Dog".describeAs {
            "name".type(
                type = AllowsNull(StringType()),
                required = true
            )
        }.tag("tag", "value")

        val data = dataOf("name" to "Fido")
        val validation = dogDescription.validate(data)
        assertThat(validation.results.asSequence()).containsExactly(
            ValidationInfo(
                dataTrace().tag("tag", "value"),
                "has attribute \"name\"",
                data
            )
        )
    }
}
