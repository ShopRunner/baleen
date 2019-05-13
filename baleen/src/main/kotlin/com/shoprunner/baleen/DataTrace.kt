package com.shoprunner.baleen

/**
 * A location in the data source that a debugger can trace to.
 */
data class TraceLocation(val location: String, val lineNumber: Int? = null, val columnNumber: Int? = null)

/**
 * Describes the path through the data.  It is used to quickly find an issue in the data.
 * It is similar to a stack trace but for finding a path to a data error.
 *
 * For example: file example.csv, line 312, attribute age
 **/
class DataTrace internal constructor(private val stack: List<TraceLocation>) {

    constructor(): this(emptyList())

    constructor(trace: String, vararg dataLocations: String): this(listOf(TraceLocation(trace)) + dataLocations.map { TraceLocation(it) }.toList())

    constructor(trace: TraceLocation, vararg dataLocations: TraceLocation): this(listOf(trace) + dataLocations.toList())

    operator fun plus(dataLocation: String): DataTrace {
        // Inherits the parent's line information if available
        val parent = this.stack.lastOrNull()
        return DataTrace(this.stack.plus(TraceLocation(dataLocation, parent?.lineNumber, parent?.columnNumber)))
    }

    operator fun plus(dataLocation: TraceLocation): DataTrace = DataTrace(this.stack.plus(dataLocation))

    /**
     * Exposes the data trace as a list with the top level first
     */
    fun toList(): List<TraceLocation> = this.stack

    override fun toString(): String {
        return stack.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataTrace

        if (stack != other.stack) return false

        return true
    }

    override fun hashCode(): Int {
        return stack.hashCode()
    }
}

/** wrapper method for legacy generation of DataTrace objects */
fun dataTrace(vararg trace: String): DataTrace = DataTrace(trace.map { TraceLocation(it) }.toList())