package com.shoprunner.baleen.types

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class StringCoercibleToTimestamp(
    timestampType: TimestampMillisType,
    dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
) :
        StringCoercibleToType<TimestampMillisType>(timestampType, {
            try {
                LocalDateTime.parse(it, dateTimeFormatter)
            } catch (ex: DateTimeParseException) {
                null
            }
        })
