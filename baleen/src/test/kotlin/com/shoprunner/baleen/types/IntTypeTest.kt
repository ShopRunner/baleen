package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class IntTypeTest {
    @Test
    fun `passes a Int`() {
        assertThat(IntType().validate(dataTrace(), 0)).isEmpty()
    }

    @Test
    fun `checks minimum value`() {
        assertThat(IntType(min = 1).validate(dataTrace(), 0)).containsExactly(ValidationError(dataTrace(), "is less than 1", 0))
        assertThat(IntType(min = 1).validate(dataTrace(), 1)).isEmpty()
        assertThat(IntType(min = 1).validate(dataTrace(), 2)).isEmpty()
    }

    @Test
    fun `checks maximum value`() {
        assertThat(IntType(max = 1).validate(dataTrace(), 0)).isEmpty()
        assertThat(IntType(max = 1).validate(dataTrace(), 1)).isEmpty()
        assertThat(IntType(max = 1).validate(dataTrace(), 2)).containsExactly(ValidationError(dataTrace(), "is greater than 1", 2))
    }

    @Test
    fun `checks null`() {
        assertThat(IntType().validate(dataTrace(), null)).containsExactly(ValidationError(dataTrace(), "is null", null))
    }

    @Test
    fun `checks not Int`() {
        assertThat(IntType().validate(dataTrace(), false)).containsExactly(ValidationError(dataTrace(), "is not an Int", false))
    }
}
