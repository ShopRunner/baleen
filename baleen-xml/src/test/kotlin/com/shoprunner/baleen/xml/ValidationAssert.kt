package com.shoprunner.baleen.xml

import com.shoprunner.baleen.Validation
import org.assertj.core.api.AbstractAssert

class ValidationAssert(actual: Validation) : AbstractAssert<ValidationAssert, Validation>(actual, ValidationAssert::class.java) {

    companion object {
        fun assertThat(actual: Validation) = ValidationAssert(actual)
    }

    fun isValid(): ValidationAssert {
        if (!actual.isValid()) {
            failWithMessage("Validation Results <%s> were not valid", actual)
        }
        return this
    }

    fun isNotValid(): ValidationAssert {
        if (actual.isValid()) {
            failWithMessage("Validation Results <%s> is valid", actual)
        }
        return this
    }
}