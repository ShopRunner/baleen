package com.shoprunner.baleen

/**
 * Describes the path through the data.  It is used to quickly find an issue in the data.
 * It is similar to a stack trace but for finding a path to a data error.
 *
 * For example: file example.csv, line 312, attribute age
 **/
class DataTrace private constructor(
    private val stack: List<String>,
    val tags: Map<String, String> = emptyMap()
) {

    constructor(vararg dataLocations: String): this(dataLocations.toList())

    operator fun plus(dataLocation: String) =
        DataTrace(
            stack = this.stack.plus(dataLocation),
            tags = this.tags)

    fun tag(key: String, value: String) =
        DataTrace(
            stack = this.stack,
            tags = this.tags.plus(key to value))

    /**
     * Exposes the data trace as a list with the top level first
     */
    fun toList(): List<String> = this.stack

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataTrace

        if (stack != other.stack) return false
        if (tags != other.tags) return false

        return true
    }

    override fun hashCode(): Int {
        var result = stack.hashCode()
        result = 31 * result + tags.hashCode()
        return result
    }

    override fun toString(): String {
        return "DataTrace(stack=$stack, tags=$tags)"
    }
}

/** wrapper method for legacy generation of DataTrace objects */
fun dataTrace(vararg trace: String) = DataTrace(*trace)