package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class StringCoercibleToFloatTest {
    @Test
    fun `passes an instant as string`() {
        assertThat(StringCoercibleToFloat(FloatType()).validate(dataTrace(), "1.0")).isEmpty()
    }

    @Test
    fun `checks if an string is not float format`() {
        assertThat(StringCoercibleToFloat(FloatType()).validate(dataTrace(), "bad string")).containsExactly(ValidationError(dataTrace(), "could not be parsed to float", "bad string"))
    }

    @Test
    fun `checks null`() {
        assertThat(StringCoercibleToFloat(FloatType()).validate(dataTrace(), null)).containsExactly(ValidationError(dataTrace(), "is null", null))
    }

    @Test
    fun `checks not string`() {
        assertThat(StringCoercibleToFloat(FloatType()).validate(dataTrace(), false)).containsExactly(ValidationError(dataTrace(), "is not a string", false))
    }
}