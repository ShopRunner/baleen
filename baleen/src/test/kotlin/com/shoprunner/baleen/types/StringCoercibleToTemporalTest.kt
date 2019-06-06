package com.shoprunner.baleen.types

import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationResult
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.time.temporal.TemporalAccessor

internal class StringCoercibleToTemporalTest {
    val localTimeCoercibleToString = object : CoercibleType(StringType()) {
        override fun name() = "localtime"
        override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> {
            return when (value) {
                is LocalTime -> sequenceOf(ValidationInfo(dataTrace, "is LocalTime", value)) +
                        this.type.validate(dataTrace, value.format(DateTimeFormatter.ISO_LOCAL_TIME))
                else -> sequenceOf(ValidationError(dataTrace, "is not LocalTime", value))

            }
        }
    }

    val stringCoercibleToLocalTime = StringCoercibleToTemporal<CoercibleType, LocalTime>(
        localTimeCoercibleToString,
        arrayOf(DateTimeFormatter.ISO_LOCAL_TIME),
        arrayOf<(TemporalAccessor) -> Temporal>(LocalTime::from)
    ) { temporal ->
        when (temporal) {
            is LocalTime -> temporal
            else -> null
        }
    }

    @Test
    fun `test parse string to LocalTime`() {

        val results = stringCoercibleToLocalTime.validate(DataTrace("hour_offset"), "03:00").toList()

        assertThat(results).allMatch { it is ValidationInfo }
    }

    @Test
    fun `test fails to LocalTime`() {

        val results = stringCoercibleToLocalTime.validate(DataTrace("hour_offset"), "XYZ").toList()

        assertThat(results).allMatch { it is ValidationError }
    }
}