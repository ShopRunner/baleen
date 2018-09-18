package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.format.DateTimeFormatter

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class StringCoercibleToInstantTest {
    @Test
    fun `passes an instant as string`() {
        assertThat(StringCoercibleToInstant(InstantType()).validate(dataTrace(), "2018-07-01T10:15:30Z")).isEmpty()
    }

    @Test
    fun `passes an zoned datetime as string`() {
        assertThat(StringCoercibleToInstant(InstantType(), DateTimeFormatter.ISO_ZONED_DATE_TIME).validate(dataTrace(), "2018-07-01T10:15:30+01:00[Europe/Paris]")).isEmpty()
    }

    @Test
    fun `passes an offset datetime as string`() {
        assertThat(StringCoercibleToInstant(InstantType(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).validate(dataTrace(), "2018-07-01T10:15:30-05:00")).isEmpty()
    }

    @Test
    fun `passes an local datetime as string`() {
        assertThat(StringCoercibleToInstant(InstantType(), DateTimeFormatter.ISO_LOCAL_DATE_TIME).validate(dataTrace(), "2018-07-01T10:15:30")).isEmpty()
    }

    @Test
    fun `passes an iso datetime as string`() {
        assertThat(StringCoercibleToInstant(InstantType(), DateTimeFormatter.ISO_DATE_TIME).validate(dataTrace(), "2018-07-01T10:15:30")).isEmpty()
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

    @Test
    fun `checks no formatters`() {
        assertThat(StringCoercibleToInstant(InstantType(), dateTimeFormatters = emptyArray()).validate(dataTrace(), "2018-07-01T10:15:30Z")).containsExactly(ValidationError(dataTrace(), "could not be parsed to instant", "2018-07-01T10:15:30Z"))
    }

    @Test
    fun `checks no temporal queries`() {
        val r = StringCoercibleToInstant(InstantType(), temporalQueries = emptyArray()).validate(dataTrace(), "2018-07-01T10:15:30Z")
        assertThat(r).containsExactly(ValidationError(dataTrace(), "could not be parsed to instant", "2018-07-01T10:15:30Z"))
    }
}