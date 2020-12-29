package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class OccurrencesTypeTest {

    @Test
    fun `fails on null object`() {
        val type = OccurrencesType(StringConstantType("foo"))
        assertThat(type.validate(dataTrace(), null)).containsExactly(
            ValidationError(dataTrace(), "is null", null)
        )
    }

    @Test
    fun `passes a single object`() {
        val type = OccurrencesType(StringConstantType("foo"))
        assertThat(type.validate(dataTrace(), "foo")).isEmpty()
    }

    @Test
    fun `passes a list`() {
        val type = OccurrencesType(StringConstantType("foo"))
        assertThat(type.validate(dataTrace(), listOf("foo"))).isEmpty()
    }

    @Test
    fun `fails if single occurrence fails`() {
        val type = OccurrencesType(StringConstantType("foo"))
        assertThat(type.validate(dataTrace(), "bar")).containsExactly(
            ValidationError(dataTrace("index 0"), "value is not 'foo'", "bar")
        )
    }

    @Test
    fun `fails if last occurrence fails`() {
        val type = OccurrencesType(StringConstantType("foo"))
        assertThat(type.validate(dataTrace(), listOf("foo", "bar"))).containsExactly(
            ValidationError(dataTrace("index 1"), "value is not 'foo'", "bar")
        )
    }

    @Test
    fun `captures all failures`() {
        val type = OccurrencesType(StringConstantType("foo"))
        assertThat(type.validate(dataTrace(), listOf("foo0", "foo1"))).containsExactly(
            ValidationError(dataTrace("index 0"), "value is not 'foo'", "foo0"),
            ValidationError(dataTrace("index 1"), "value is not 'foo'", "foo1")
        )
    }
}
