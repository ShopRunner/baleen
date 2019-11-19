package com.shoprunner.baleen.kotlin.example

import com.shoprunner.baleen.annotation.DataDescription
import com.shoprunner.baleen.annotation.DefaultNull

@DataDescription
data class Pack(
    val packName: String,
    val dogs: List<Dog>,
    @DefaultNull
    val leadDog: Dog? = null
)
