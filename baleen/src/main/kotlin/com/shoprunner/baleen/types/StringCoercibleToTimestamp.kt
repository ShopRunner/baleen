package com.shoprunner.baleen.types

import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.time.temporal.TemporalAccessor

@Deprecated("Use com.shoprunner.baleen.types.StringCoercibleToInstant")
class StringCoercibleToTimestamp(
    timestampType: TimestampMillisType,
    dateTimeFormatters: Array<DateTimeFormatter> = arrayOf(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
    temporalQueries: Array<out (TemporalAccessor)-> Temporal> = arrayOf(LocalDateTime::from, OffsetDateTime::from, ZonedDateTime::from)
) :
        StringCoercibleToTemporal<TimestampMillisType, LocalDateTime>(
                timestampType,
                dateTimeFormatters,
                temporalQueries,
                { temporal ->
                    when (temporal) {
                        is LocalDateTime -> temporal
                        is OffsetDateTime -> temporal.toLocalDateTime()
                        is ZonedDateTime -> temporal.toLocalDateTime()
                        else -> null
                    }
                }) {

    // For backwards compatibility
    constructor(timestampType: TimestampMillisType, dateTimeFormatter: DateTimeFormatter) :
        this(timestampType, arrayOf(dateTimeFormatter))
}