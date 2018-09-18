package com.shoprunner.baleen.types

import java.time.Instant
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.Temporal
import java.time.temporal.TemporalAccessor

class StringCoercibleToInstant(
    instantType: InstantType,
    dateTimeFormatters: Array<DateTimeFormatter> = arrayOf(DateTimeFormatter.ISO_INSTANT),
    temporalQueries: Array<(TemporalAccessor) -> Temporal> = arrayOf(Instant::from, ZonedDateTime::from, OffsetDateTime::from, LocalDateTime::from)
) :
        StringCoercibleToTemporal<InstantType, Instant>(
                instantType,
                dateTimeFormatters,
                temporalQueries,
                { temporal ->
                    when (temporal) {
                        is LocalDateTime -> temporal.toInstant(ZoneOffset.UTC)
                        is OffsetDateTime -> temporal.toInstant()
                        is ZonedDateTime -> temporal.toInstant()
                        is Instant -> temporal
                        else -> null
                    }
                }) {

    // For backwards compatibility
    constructor(instantType: InstantType, dateTimeFormatter: DateTimeFormatter) :
            this(instantType, arrayOf(dateTimeFormatter))
}