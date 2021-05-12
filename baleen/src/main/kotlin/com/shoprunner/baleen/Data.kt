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
    fun getAsString(key: String): MaybeData<Exception, String?> = getAs(key)
    fun getAsStringOrNull(key: String): String? = getAs<String?>(key).getOrNull()
    fun getAsStringOrThrow(key: String): String? = getAs<String?>(key).getOrThrow()

    /**
     * Retrieve the value using the key and cast it into a String or throw an Exception
     */
    fun getAsInt(key: String): MaybeData<Exception, Int?> = getAs(key)
    fun getAsIntOrNull(key: String): Int? = getAs<Int?>(key).getOrNull()
    fun getAsIntOrThrow(key: String): Int? = getAs<Int?>(key).getOrThrow()

    /**
     * Retrieve the value using the key and cast it into a String or throw an Exception
     */
    fun getAsLong(key: String): MaybeData<Exception, Long?> = getAs(key)
    fun getAsLongOrNull(key: String): Long? = getAs<Long?>(key).getOrNull()
    fun getAsLongOrThrow(key: String): Long? = getAs<Long?>(key).getOrThrow()

    /**
     * Retrieve the value using the key and cast it into a String or throw an Exception
     */
    fun getAsFloat(key: String): MaybeData<Exception, Float?> = getAs(key)
    fun getAsFloatOrNull(key: String): Float? = getAs<Float?>(key).getOrNull()
    fun getAsFloatOrThrow(key: String): Float? = getAs<Float?>(key).getOrThrow()

    /**
     * Retrieve the value using the key and cast it into a String or throw an Exception
     */
    fun getAsDouble(key: String): MaybeData<Exception, Double?> = getAs(key)
    fun getAsDoubleOrNull(key: String): Double? = getAs<Double?>(key).getOrNull()
    fun getAsDoubleOrThrow(key: String): Double? = getAs<Double?>(key).getOrThrow()

    companion object {
        /**
         * Retrieve the value using the key directly and casts it into expected value or throw Exception
         */
        inline fun <reified T> Data.getAs(key: String): MaybeData<Exception, T> = try {
            GoodData(this[key] as T)
        } catch (e: ClassCastException) {
            BadData(e, this[key])
        } catch (e: NullPointerException) {
            BadData(e, this[key])
        }
    }
}
