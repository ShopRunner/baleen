package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat as assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DoubleTypeTest {
    @Test
    fun `passes a Double`() {
        assertThat(DoubleType().validate(dataTrace(), 0.0)).isEmpty()
    }

    @Test
    fun `passes a Float`() {
        assertThat(DoubleType().validate(dataTrace(), 0.0F)).containsExactly(
            ValidationInfo(dataTrace(), message = "is coerced to double from Float", value = 0.0F)
        )
    }

    @Test
    fun `passes a Long`() {
        assertThat(DoubleType().validate(dataTrace(), 0L)).containsExactly(
            ValidationInfo(dataTrace(), message = "is coerced to double from Long", value = 0L)
        )
    }

    @Test
    fun `passes a Int`() {
        assertThat(DoubleType().validate(dataTrace(), 0)).containsExactly(
            ValidationInfo(dataTrace(), message = "is coerced to double from Integer", value = 0)
        )
    }

    @Test
    fun `checks minimum value`() {
        assertThat(DoubleType(min = 1.0).validate(dataTrace(), 0.0)).containsExactly(ValidationError(dataTrace(), "is less than 1.0", 0.0))
        assertThat(DoubleType(min = 1.0).validate(dataTrace(), 1.0)).isEmpty()
        assertThat(DoubleType(min = 1.0).validate(dataTrace(), 2.0)).isEmpty()
    }

    @Test
    fun `checks maximum value`() {
        assertThat(DoubleType(max = 1.0).validate(dataTrace(), 0.0)).isEmpty()
        assertThat(DoubleType(max = 1.0).validate(dataTrace(), 1.0)).isEmpty()
        assertThat(DoubleType(max = 1.0).validate(dataTrace(), 2.0)).containsExactly(ValidationError(dataTrace(), "is greater than 1.0", 2.0))
    }

    @Test
    fun `checks null`() {
        assertThat(DoubleType().validate(dataTrace(), null)).containsExactly(ValidationError(dataTrace(), "is null", null))
    }

    @Test
    fun `checks not Double`() {
        assertThat(DoubleType().validate(dataTrace(), false)).containsExactly(ValidationError(dataTrace(), "is not a double", false))
    }
}