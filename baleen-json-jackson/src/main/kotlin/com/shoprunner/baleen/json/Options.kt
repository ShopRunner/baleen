package com.shoprunner.baleen.json

/**
 * Parameters to pass to JsonUtil for how to read certain values
 */
data class Options(
    /**
     * Instructions to read decimal number values
     */
    val decimalOptions: DecimalOptions = DecimalOptions.BIG_DECIMAL,
    /**
     * Instructions to read integer number values
     */
    val integerOptions: IntegerOptions = IntegerOptions.BIG_INTEGER
)

/**
 * The supported decimal values
 */
enum class DecimalOptions {
    FLOAT, DOUBLE, BIG_DECIMAL
}

/**
 * The supported integer values
 */
enum class IntegerOptions {
    INT, LONG, BIG_INTEGER, BIG_DECIMAL
}
