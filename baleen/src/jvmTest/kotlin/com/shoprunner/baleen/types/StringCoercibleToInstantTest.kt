package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class StringCoercibleToInstantTest {
    @Test
    fun `passes an instant as string`() {
        assertThat(StringCoercibleToInstant(InstantType()).validate(dataTrace(), "2018-07-01T10:15:30Z")).isEmpty()
    }

    @Test
    fun `checks if an string is not instant format`() {
        assertThat(StringCoercibleToInstant(InstantType()).validate(dataTrace(), "bad string")).containsExactly(ValidationError(dataTrace(), "could not be parsed to instant", "bad string"))
    }

    @Test
    fun `checks null`() {
        assertThat(StringCoercibleToInstant(InstantType()).validate(dataTrace(), null)).containsExactly(ValidationError(dataTrace(), "is null", null))
    }

    @Test
    fun `checks not string`() {
        assertThat(StringCoercibleToInstant(InstantType()).validate(dataTrace(), false)).containsExactly(ValidationError(dataTrace(), "is not a string", false))
    }
}