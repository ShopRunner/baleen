package com.shoprunner.baleen.types

import java.time.Instant

class LongCoercibleToInstant(instantType: InstantType, val precision: Precision = Precision.millis) :
        LongCoercibleToType<InstantType>(instantType, {
            when (precision) {
                Precision.millis -> Instant.ofEpochMilli(it)
            }
        }) {

    enum class Precision {
        millis
        // TODO: Add nanos, since Avro supports nano resolution
    }
}
