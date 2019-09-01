package com.shoprunner.baleen.kotlin.example

import com.shoprunner.baleen.annotation.DataDescription
import com.shoprunner.baleen.annotation.DefaultValue
import com.shoprunner.baleen.annotation.DefaultValueType

/**
 * The dog data class
 */
@DataDescription
data class Dog(
    /** A non-nullable name field **/
    var name: String,

    /** A nullable (optional) number of legs field */
    @DefaultValue(DefaultValueType.Null)
    var numLegs: Int? = null
)
