package com.shoprunner.baleen.types

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Validate that a String can be parsed as a TimestampMillisType using the provided formatter or ISO_LOCAL_DATE_TIME
 * if none.
 */
class StringCoercibleToTimestamp private constructor(
    /**
     * The TimestampMillisType baleen type for validation the parsed value
     */
    timestampType: TimestampMillisType,
    /**
     * The DateTimeFormatter used to read the input string
     */
    val dateTimeFormatter: DateTimeFormatter,
    /**
     * The pattern used to create the DateTimeFormatter if any provided.  DataTimeFormatter.ISO_LOCAL_DATE_TIME and others
     * do not carry over the pattern unfortunately.
     */
    val pattern: String?
) :
    StringCoercibleToType<TimestampMillisType>(
        timestampType,
        {
            try {
                LocalDateTime.parse(it, dateTimeFormatter)
            } catch (ex: DateTimeParseException) {
                null
            }
        }
    ) {

    constructor(timestampType: TimestampMillisType) :
        this(timestampType, DateTimeFormatter.ISO_LOCAL_DATE_TIME, null)

    constructor(timestampType: TimestampMillisType, dateTimeFormatter: DateTimeFormatter) :
        this(timestampType, dateTimeFormatter, null)

    constructor(timestampType: TimestampMillisType, pattern: String) :
        this(timestampType, DateTimeFormatter.ofPattern(pattern), pattern)
}
