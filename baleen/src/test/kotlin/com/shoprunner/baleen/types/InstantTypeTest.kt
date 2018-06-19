package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class InstantTypeTest {
    @Test
    fun `passes an Instant`() {
        assertThat(InstantType().validate(dataTrace(), Instant.now())).isEmpty()
    }

    @Test
    fun `checks value after`() {
        val day = Instant.parse("2018-07-01T10:15:30Z")
        val before = Instant.parse("2018-06-30T10:15:30Z")
        val after = Instant.parse("2018-07-02T10:15:30Z")
        assertThat(InstantType(after = day).validate(dataTrace(), before)).containsExactly(ValidationError(dataTrace(), "is before 2018-07-01T10:15:30Z", 0L))
        assertThat(InstantType(after = day).validate(dataTrace(), day)).isEmpty()
        assertThat(InstantType(after = day).validate(dataTrace(), after)).isEmpty()
    }

    @Test
    fun `checks value before`() {
        val day = Instant.parse("2018-07-01T10:15:30Z")
        val before = Instant.parse("2018-06-30T10:15:30Z")
        val after = Instant.parse("2018-07-02T10:15:30Z")
        assertThat(InstantType(before = day).validate(dataTrace(), before)).isEmpty()
        assertThat(InstantType(before = day).validate(dataTrace(), day)).isEmpty()
        assertThat(InstantType(before = day).validate(dataTrace(), after)).containsExactly(ValidationError(dataTrace(), "is after 2018-07-01T10:15:30Z", 2L))
    }

    @Test
    fun `checks null`() {
        assertThat(InstantType().validate(dataTrace(), null)).containsExactly(ValidationError(dataTrace(), "is null", null))
    }

    @Test
    fun `checks not Instant`() {
        assertThat(InstantType().validate(dataTrace(), false)).containsExactly(ValidationError(dataTrace(), "is not an instant", false))
    }
}