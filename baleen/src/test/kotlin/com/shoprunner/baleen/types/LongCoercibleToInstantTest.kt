package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class LongCoercibleToInstantTest {
    @Test
    fun `passes an instant as long`() {
        assertThat(LongCoercibleToInstant(InstantType()).validate(dataTrace(), Instant.now().toEpochMilli())).isEmpty()
    }

    @Test
    fun `checks null`() {
        assertThat(LongCoercibleToInstant(InstantType()).validate(dataTrace(), null)).containsExactly(ValidationError(dataTrace(), "is null", null))
    }

    @Test
    fun `checks not long`() {
        assertThat(LongCoercibleToInstant(InstantType()).validate(dataTrace(), false)).containsExactly(ValidationError(dataTrace(), "is not a long", false))
    }
}