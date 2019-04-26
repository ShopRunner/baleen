package com.shoprunner.baleen

/**
 * Describes the path through the data.  It is used to quickly find an issue in the data.
 * It is similar to a stack trace but for finding a path to a data error.
 *
 * For example: file example.csv, line 312, attribute age
 **/
class DataTrace private constructor(private val stack: List<String>) {

    constructor(vararg dataLocations: String): this(dataLocations.toList())

    operator fun plus(dataLocation: String) = DataTrace(this.stack.plus(dataLocation))

    /**
     * Exposes the data trace as a list with the top level first
     */
    fun toList(): List<String> = this.stack

    override fun toString(): String {
        return stack.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is DataTrace) return false

        if (stack != other.stack) return false

        return true
    }

    override fun hashCode(): Int {
        return stack.hashCode()
    }
}

/** wrapper method for legacy generation of DataTrace objects */
fun dataTrace(vararg trace: String) = DataTrace(*trace)