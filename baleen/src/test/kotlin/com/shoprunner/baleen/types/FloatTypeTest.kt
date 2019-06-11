package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
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
    fun `passes a Long`() {
        assertThat(FloatType().validate(dataTrace(), 0L)).containsExactly(
            ValidationInfo(dataTrace(), message = "is coerced to float from Long", value = 0L)
        )
    }

    @Test
    fun `passes a Int`() {
        assertThat(FloatType().validate(dataTrace(), 0)).containsExactly(
            ValidationInfo(dataTrace(), message = "is coerced to float from Integer", value = 0)
        )
    }

    @Test
    fun `passes a float-sized Double`() {
        assertThat(FloatType().validate(dataTrace(), 0.0)).containsExactly(
            ValidationInfo(dataTrace(), message = "is coerced to float from Double", value = 0.0)
        )
    }

    @Test
    fun `checks a large Double`() {
        assertThat(FloatType().validate(dataTrace(), Double.MAX_VALUE)).containsExactly(ValidationError(dataTrace(), "is not a float", Double.MAX_VALUE))
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