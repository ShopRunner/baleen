package com.shoprunner.baleen.types

import com.soywiz.klock.DateFormat

class StringCoercibleToDateTime(
    dateTimeType: DateTimeType,
    dateTimeFormatter: DateFormat = DateFormat.FORMAT1
) : StringCoercibleToType<DateTimeType>(dateTimeType, { dateTimeFormatter.tryParse(it) })