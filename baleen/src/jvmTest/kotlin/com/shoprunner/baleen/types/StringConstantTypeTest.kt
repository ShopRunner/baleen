package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class StringConstantTypeTest {

    @Test
    fun `passes a constant string`() {
        assertThat(StringConstantType("abc").validate(dataTrace(), "abc")).isEmpty()
    }

    @Test
    fun `checks not constant`() {
        assertThat(StringConstantType("abc").validate(dataTrace(), "")).containsExactly(ValidationError(dataTrace(), "value is not 'abc'", ""))
    }
}