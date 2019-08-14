package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BooleanTypeTest {
    @Test
    fun `passes a boolean`() {
        assertThat(BooleanType().validate(dataTrace(), true)).isEmpty()
        assertThat(BooleanType().validate(dataTrace(), false)).isEmpty()
    }

    @Test
    fun `checks null`() {
        assertThat(BooleanType().validate(dataTrace(), null)).containsExactly(ValidationError(dataTrace(), "is null", null))
    }

    @Test
    fun `checks not Boolean`() {
        assertThat(BooleanType().validate(dataTrace(), 0L)).containsExactly(ValidationError(dataTrace(), "is not a boolean", 0L))
    }
}
