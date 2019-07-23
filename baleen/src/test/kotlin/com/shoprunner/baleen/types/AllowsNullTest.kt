package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class AllowsNullTest {
    @Test
    fun `passes a non-null`() {
        assertThat(AllowsNull(LongType()).validate(dataTrace(), 0L)).isEmpty()
    }

    @Test
    fun `passes null`() {
        assertThat(AllowsNull(LongType()).validate(dataTrace(), null)).isEmpty()
    }
}
