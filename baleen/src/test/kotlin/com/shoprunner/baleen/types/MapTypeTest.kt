package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MapTypeTest {
    @Test
    fun `passes a map`() {
        assertThat(MapType(StringType(), IntType()).validate(dataTrace(), mapOf("One" to 1))).isEmpty()
    }

    @Test
    fun `checks key type`() {
        assertThat(MapType(StringType(), IntType()).validate(dataTrace(), mapOf(1 to 1))).containsExactly(ValidationError(dataTrace("index 0 key"), "is not a string", 1))
    }

    @Test
    fun `checks value type`() {
        assertThat(MapType(StringType(), IntType()).validate(dataTrace(), mapOf("One" to "one"))).containsExactly(ValidationError(dataTrace("index 0 value"), "is not an Int", "one"))
    }

    @Test
    fun `checks null`() {
        assertThat(MapType(StringType(), IntType()).validate(dataTrace(), null)).containsExactly(ValidationError(dataTrace(), "is null", null))
    }

    @Test
    fun `checks not Map`() {
        assertThat(MapType(StringType(), IntType()).validate(dataTrace(), false)).containsExactly(ValidationError(dataTrace(), "is not a map", false))
    }
}
