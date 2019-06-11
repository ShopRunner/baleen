package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class LongTypeTest {
    @Test
    fun `passes a Long`() {
        assertThat(LongType().validate(dataTrace(), 0L)).isEmpty()
    }

    @Test
    fun `passes a Int`() {
        assertThat(LongType().validate(dataTrace(), 0)).containsExactly(
            ValidationInfo(dataTrace(), message = "is coerced to long from int", value = 0)
        )
    }

    @Test
    fun `checks minimum value`() {
        assertThat(LongType(min = 1).validate(dataTrace(), 0L)).containsExactly(ValidationError(dataTrace(), "is less than 1", 0L))
        assertThat(LongType(min = 1).validate(dataTrace(), 1L)).isEmpty()
        assertThat(LongType(min = 1).validate(dataTrace(), 2L)).isEmpty()
    }

    @Test
    fun `checks maximum value`() {
        assertThat(LongType(max = 1).validate(dataTrace(), 0L)).isEmpty()
        assertThat(LongType(max = 1).validate(dataTrace(), 1L)).isEmpty()
        assertThat(LongType(max = 1).validate(dataTrace(), 2L)).containsExactly(ValidationError(dataTrace(), "is greater than 1", 2L))
    }

    @Test
    fun `checks null`() {
        assertThat(LongType().validate(dataTrace(), null)).containsExactly(ValidationError(dataTrace(), "is null", null))
    }

    @Test
    fun `checks not Long`() {
        assertThat(LongType().validate(dataTrace(), false)).containsExactly(ValidationError(dataTrace(), "is not a long", false))
    }
}