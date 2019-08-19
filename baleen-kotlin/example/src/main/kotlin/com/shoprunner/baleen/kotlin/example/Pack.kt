package com.shoprunner.baleen.kotlin.example

import com.shoprunner.baleen.annotation.DataDescription

@DataDescription
data class Pack(
    val packName: String,
    val dogs: List<Dog>,
    val leadDog: Dog? = null
)
