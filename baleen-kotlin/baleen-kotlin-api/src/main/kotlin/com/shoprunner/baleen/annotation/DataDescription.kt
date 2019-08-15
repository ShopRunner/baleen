package com.shoprunner.baleen.annotation

@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS)
@MustBeDocumented
annotation class DataDescription(
    /**
     * The name of the data object. If not specified then the data class name is use.
     */
    val name: String = "",
    /**
     * The package of the data object. If not specified then the package's data class name is use.
     */
    val packageName: String = ""
)
