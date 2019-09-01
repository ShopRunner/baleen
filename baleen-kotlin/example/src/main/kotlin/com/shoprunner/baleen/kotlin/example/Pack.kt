package com.shoprunner.baleen.kotlin.example

import com.shoprunner.baleen.annotation.DataDescription
import com.shoprunner.baleen.annotation.DefaultValue
import com.shoprunner.baleen.annotation.DefaultValueType

@DataDescription
data class Pack(
    val packName: String,
    val dogs: List<Dog>,
    @DefaultValue(DefaultValueType.Null)
    val leadDog: Dog? = null
)
