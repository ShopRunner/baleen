package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class EnumTypeTest {

    enum class Alphabet {
        a, b, c
    }

    enum class Numbers {
        one, two, three
    }

    @Test
    fun `passes string enum`() {
        assertThat(EnumType("Alphabet", "a", "b", "c").validate(dataTrace(), "a")).isEmpty()
        assertThat(EnumType("Alphabet", "a", "b", "c").validate(dataTrace(), "b")).isEmpty()
        assertThat(EnumType("Alphabet", "a", "b", "c").validate(dataTrace(), "c")).isEmpty()
    }

    @Test
    fun `passes enum objects`() {
        assertThat(EnumType("Alphabet", Alphabet.values()).validate(dataTrace(), Alphabet.a)).isEmpty()
        assertThat(EnumType("Alphabet", Alphabet.values()).validate(dataTrace(), Alphabet.b)).isEmpty()
        assertThat(EnumType("Alphabet", Alphabet.values()).validate(dataTrace(), Alphabet.c)).isEmpty()
    }

    @Test
    fun `checks unsupported string enums`() {
        assertThat(EnumType("Alphabet", "a", "b", "c").validate(dataTrace(), "one")).containsExactly(ValidationError(dataTrace(), "is not contained in enum Alphabet [a, b, c]", "one"))
    }

    @Test
    fun `checks unsupported enums`() {
        assertThat(EnumType("Alphabet", Alphabet.values()).validate(dataTrace(), Numbers.one)).containsExactly(ValidationError(dataTrace(), "is not contained in enum Alphabet [a, b, c]", Numbers.one))
    }

    @Test
    fun `checks null`() {
        assertThat(EnumType("Alphabet", "a", "b", "c").validate(dataTrace(), null)).containsExactly(ValidationError(dataTrace(), "is null", null))
    }
}