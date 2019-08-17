package com.shoprunner.baleen.kotlin.kapt.test

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataDescription
import org.assertj.core.api.AbstractAssert

class DataDescriptionAssert(actual: com.shoprunner.baleen.DataDescription) : AbstractAssert<DataDescriptionAssert, DataDescription>(actual, DataDescriptionAssert::class.java) {

    companion object {
        fun assertBaleen(actual: com.shoprunner.baleen.DataDescription) =
            DataDescriptionAssert(actual)
    }

    fun hasName(expected: String): DataDescriptionAssert {
        if (actual.name != expected) {
            failWithMessage("Data Descriptions named differently <%s> vs <%s>", actual.name, expected)
        }
        return this
    }

    fun hasNamespace(expected: String): DataDescriptionAssert {
        if (actual.nameSpace != expected) {
            failWithMessage("Data Descriptions named differently <%s> vs <%s>", actual.nameSpace, expected)
        }
        return this
    }

    fun hasAttribute(expectedAttributeName: String, expectedAttributeType: BaleenType): DataDescriptionAssert {
        val allAttributeNamesAndTypes = actual.attrs.map { it.name to it.type }

        val matchingAttr = allAttributeNamesAndTypes.firstOrNull { it.first == expectedAttributeName }

        if (matchingAttr == null) {
            failWithMessage("Cannot find attribute with name $expectedAttributeName. All attributes: %s", allAttributeNamesAndTypes.map { it.first to it.second.name() })
        }
        if (matchingAttr?.second?.name() != expectedAttributeType.name()) {
            failWithMessage("Attribute $expectedAttributeName has a different type. %s vs %s", matchingAttr?.second?.name(), expectedAttributeType.name())
        }
        return this
    }
}
