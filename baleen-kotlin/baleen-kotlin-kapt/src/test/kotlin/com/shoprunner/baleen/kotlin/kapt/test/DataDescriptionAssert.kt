package com.shoprunner.baleen.kotlin.kapt.test

import com.shoprunner.baleen.AttributeDescription
import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataDescription
import org.assertj.core.api.AbstractAssert

class DataDescriptionAssert(actual: com.shoprunner.baleen.DataDescription) :
    AbstractAssert<DataDescriptionAssert, DataDescription>(actual, DataDescriptionAssert::class.java) {

    companion object {
        @JvmStatic
        fun assertBaleen(actual: com.shoprunner.baleen.DataDescription) =
            DataDescriptionAssert(actual)

        @JvmStatic
        fun assertThat(actual: DataDescription) = assertBaleen(actual)
    }

    fun hasName(expected: String): DataDescriptionAssert {
        if (actual.name != expected) {
            failWithMessage("Data Descriptions named differently <%s> vs <%s>", actual.name, expected)
        }
        return this
    }

    fun hasNamespace(expected: String): DataDescriptionAssert {
        if (actual.nameSpace != expected) {
            failWithMessage("Data Descriptions namespaced differently <%s> vs <%s>", actual.nameSpace, expected)
        }
        return this
    }

    fun hasAttribute(
        expectedAttributeName: String,
        expectedType: BaleenType
    ): DataDescriptionAssert {
        return hasAttribute(expectedAttributeName) {
            AttributeDescriptionAssert.assertThat(it)
                .hasType(expectedType)
        }
    }

    fun hasAttribute(
        expectedAttributeName: String,
        furtherAssertions: (AttributeDescription) -> Unit = { }
    ): DataDescriptionAssert {
        val allAttributes = actual.attrs

        val matchingAttr = allAttributes.firstOrNull { it.name == expectedAttributeName }

        if (matchingAttr == null) {
            failWithMessage(
                "Cannot find attribute with name $expectedAttributeName. All attributes: %s",
                allAttributes.map { it.name to it.type.name() })
        } else {
            furtherAssertions(matchingAttr)
        }

        return this
    }

    fun hasMarkdownDescription(expectedMarkdownDescription: String): DataDescriptionAssert {
        if (actual?.markdownDescription != expectedMarkdownDescription) {
            failWithMessage(
                "Data Descriptions has different descriptions: <%s> vs <%s>",
                actual?.markdownDescription,
                expectedMarkdownDescription
            )
        }

        return this
    }
}

class AttributeDescriptionAssert(actual: AttributeDescription) :
AbstractAssert<AttributeDescriptionAssert, AttributeDescription>(actual, AttributeDescriptionAssert::class.java) {

    companion object {
        @JvmStatic
        fun assertAttrDescription(actual: AttributeDescription) = AttributeDescriptionAssert(actual)

        @JvmStatic
        fun assertThat(actual: AttributeDescription) = assertAttrDescription(actual)
    }

    fun hasType(expectedType: BaleenType): AttributeDescriptionAssert {
        if (actual.type.name() != expectedType.name()) {
            failWithMessage(
                "Attribute ${actual.name} has a different type. %s vs %s",
                actual.type.name(),
                expectedType.name()
            )
        }
        return this
    }

    fun hasMarkdownDescription(expectedMarkdownDescription: String): AttributeDescriptionAssert {
        if (actual.markdownDescription != expectedMarkdownDescription) {
            failWithMessage(
                "Attribute ${actual.name} has a different description. %s vs %s",
                actual.markdownDescription,
                expectedMarkdownDescription
            )
        }
        return this
    }

    fun hasAlias(vararg expectedAlias: String): AttributeDescriptionAssert {
        if (actual.aliases.size != expectedAlias.size || actual.aliases.zip(expectedAlias).any { it.first != it.second }) {
            failWithMessage(
                "Attribute ${actual.name} has a different aliases. %s vs %s",
                actual.aliases.toList(),
                expectedAlias.toList()
            )
        }
        return this
    }
}
