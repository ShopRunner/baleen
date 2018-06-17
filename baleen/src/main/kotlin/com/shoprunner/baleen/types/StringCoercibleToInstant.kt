package com.shoprunner.baleen.types

import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class StringCoercibleToInstant(instantType: InstantType,
                               dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_INSTANT) :
        StringCoercibleToType<InstantType>(instantType, {
            try {
                dateTimeFormatter.parse(it, Instant::from)
            } catch (ex: DateTimeParseException) {
                null
            }
        })