package com.shoprunner.baleen.types

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * Validate that a String can be parsed as a InstantType using the provided formatter or ISO_INSTANT
 * if none.
 */
class StringCoercibleToInstant(
    instantType: InstantType,
    /**
     * The DateTimeFormatter used to read the input string
     */
    val dateTimeFormatter: DateTimeFormatter,
    /**
     * The pattern used to create the DateTimeFormatter if any provided. DataTimeFormatter.ISO_INSTANT and others
     * do not carry over the pattern unfortunately.
     */
    val pattern: String?
) :
        StringCoercibleToType<InstantType>(instantType, {
            try {
                dateTimeFormatter.parse(it, Instant::from)
            } catch (ex: DateTimeParseException) {
                null
            }
        }) {

    constructor(instantType: InstantType) :
            this(instantType, DateTimeFormatter.ISO_INSTANT, null)

    constructor(instantType: InstantType, dateTimeFormatter: DateTimeFormatter) :
            this(instantType, dateTimeFormatter, null)

    constructor(instantType: InstantType, pattern: String) :
            this(instantType, DateTimeFormatter.ofPattern(pattern), pattern)
}
