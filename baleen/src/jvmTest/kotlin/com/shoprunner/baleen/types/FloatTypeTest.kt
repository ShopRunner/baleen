package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class FloatTypeTest {
    @Test
    fun `passes a Float`() {
        assertThat(FloatType().validate(dataTrace(), 0.0F)).isEmpty()
    }

    @Test
    fun `checks minimum value`() {
        assertThat(FloatType(min = 1.0F).validate(dataTrace(), 0.0F)).containsExactly(ValidationError(dataTrace(), "is less than 1.0", 0.0F))
        assertThat(FloatType(min = 1.0F).validate(dataTrace(), 1.0F)).isEmpty()
        assertThat(FloatType(min = 1.0F).validate(dataTrace(), 2.0F)).isEmpty()
    }

    @Test
    fun `checks maximum value`() {
        assertThat(FloatType(max = 1.0F).validate(dataTrace(), 0.0F)).isEmpty()
        assertThat(FloatType(max = 1.0F).validate(dataTrace(), 1.0F)).isEmpty()
        assertThat(FloatType(max = 1.0F).validate(dataTrace(), 2.0F)).containsExactly(ValidationError(dataTrace(), "is greater than 1.0", 2.0F))
    }

    @Test
    fun `checks null`() {
        assertThat(FloatType().validate(dataTrace(), null)).containsExactly(ValidationError(dataTrace(), "is null", null))
    }

    @Test
    fun `checks not Float`() {
        assertThat(FloatType().validate(dataTrace(), false)).containsExactly(ValidationError(dataTrace(), "is not a float", false))
    }
}