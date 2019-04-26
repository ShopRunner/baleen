package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class StringTypeTest {

    @Test
    fun `passes a string`() {
        assertThat(StringType().validate(dataTrace(), "")).isEmpty()
        assertThat(StringType().validate(dataTrace(), "abc")).isEmpty()
    }

    @Test
    fun `checks minimum length`() {
        assertThat(StringType(min = 1).validate(dataTrace(), "")).containsExactly(ValidationError(dataTrace(), "is not at least 1 characters", ""))
        assertThat(StringType(min = 1).validate(dataTrace(), "a")).isEmpty()
        assertThat(StringType(min = 1).validate(dataTrace(), "ab")).isEmpty()
    }
}