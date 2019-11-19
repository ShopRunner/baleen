package com.shoprunner.baleen.kotlin.example

import com.shoprunner.baleen.annotation.DataDescription
import com.shoprunner.baleen.annotation.DefaultNull

/**
 * The dog data class
 */
@DataDescription
data class Dog(
    /** A non-nullable name field **/
    var name: String,

    /** A nullable (optional) number of legs field */
    @DefaultNull
    var numLegs: Int? = null
)
