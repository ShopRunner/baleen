package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.time.temporal.Temporal
import java.time.temporal.TemporalAccessor
import java.time.temporal.TemporalQuery

abstract class StringCoercibleToTemporal<out TemporalBaleenType : BaleenType, out TemporalClass : Temporal>(
    temporalType: TemporalBaleenType,
    private val dateTimeFormatters: Array<DateTimeFormatter>,
    temporalQueries: Array<out (TemporalAccessor) -> Temporal>,
    private val converter: (Any?) -> TemporalClass?
) :
        StringCoercibleToType<TemporalBaleenType>(temporalType, {
            if (dateTimeFormatters.isEmpty() || temporalQueries.isEmpty()) {
                null
            } else {
                dateTimeFormatters.asSequence().map { dateTimeFormatter ->
                    try {

                        val bestMatch = if (temporalQueries.size > 1) {
                            val queries = temporalQueries.map(::toTemporalQuery)
                            dateTimeFormatter.parseBest(it, *queries.toTypedArray())
                        } else {
                            dateTimeFormatter.parse(it, toTemporalQuery(temporalQueries[0]))
                        }

                        converter(bestMatch)
                    } catch (ex: DateTimeParseException) {
                        null
                    }
                }.firstOrNull()
            }
        }) {

    companion object {
        private fun toTemporalQuery(f: (TemporalAccessor) -> Temporal): TemporalQuery<*> {
            return TemporalQuery { t -> f(t) }
        }
    }

    override fun name() = "string coercible to ${type.name()} with formats [${dateTimeFormatters.joinToString(", ")}]"
}
