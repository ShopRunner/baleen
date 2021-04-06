package com.shoprunner.baleen

interface Data {
    fun containsKey(key: String): Boolean

    // returns null if value does not exist
    operator fun get(key: String): Any?

    fun attributeDataValue(key: String, dataTrace: DataTrace): DataValue = DataValue(get(key), dataTrace + "attribute \"$key\"")

    val keys: Set<String>

    /**
     * Retrieve the value using the key and cast it into a String or throw an Exception
     */
    fun getAsString(key: String): String? = getAs(key)

    /**
     * Retrieve the value using the key and cast it into a String or throw an Exception
     */
    fun getAsInt(key: String): Int? = getAs(key)

    /**
     * Retrieve the value using the key and cast it into a String or throw an Exception
     */
    fun getAsLong(key: String): Long? = getAs(key)

    /**
     * Retrieve the value using the key and cast it into a String or throw an Exception
     */
    fun getAsFloat(key: String): Float? = getAs(key)

    /**
     * Retrieve the value using the key and cast it into a String or throw an Exception
     */
    fun getAsDouble(key: String): Double? = getAs(key)

    companion object {
        /**
         * Retrieve the value using the key directly and casts it into expected value or throw Exception
         */
        inline fun <reified T> Data.getAs(key: String): T? = this[key]?.takeIf { it is T }?.let { it as T }
            ?: throw IllegalArgumentException("Cannot cast $this to expected type ${T::class}")
    }
}
