package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class StringCoercibleToLongTest {
    @Test
    fun `passes an instant as string`() {
        assertThat(StringCoercibleToLong(LongType()).validate(dataTrace(), "1")).isEmpty()
    }

    @Test
    fun `checks if an string is not long format`() {
        assertThat(StringCoercibleToLong(LongType()).validate(dataTrace(), "bad string")).containsExactly(ValidationError(dataTrace(), "could not be parsed to long", "bad string"))
    }

    @Test
    fun `checks null`() {
        assertThat(StringCoercibleToLong(LongType()).validate(dataTrace(), null)).containsExactly(ValidationError(dataTrace(), "is null", null))
    }

    @Test
    fun `checks not string`() {
        assertThat(StringCoercibleToLong(LongType()).validate(dataTrace(), false)).containsExactly(ValidationError(dataTrace(), "is not a string", false))
    }
}