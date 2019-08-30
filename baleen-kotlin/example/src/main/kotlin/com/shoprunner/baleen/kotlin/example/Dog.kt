package com.shoprunner.baleen.kotlin.example

import com.shoprunner.baleen.annotation.DataDescription

/**
 * The dog data class
 */
@DataDescription
data class Dog(
    /** A non-nullable name field **/
    var name: String,

    /** A nullable (optional) number of legs field */
    var numLegs: Int? = null
)
