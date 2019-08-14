package com.shoprunner.baleen.kotlin.example

import com.shoprunner.baleen.kotlin.dataDescription
import com.shoprunner.baleen.kotlin.validate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DogValidationTest {
    @Test
    fun `test data description is generated`() {
        val dog = Dog("Fido", 4)
        assertThat(dog.dataDescription()).isNotNull
    }

    @Test
    fun `test validate method is generated`() {
        val dog = Dog("Fido", 4)
        assertThat(dog.validate().isValid()).isTrue()
    }

    @Test
    fun `test data test assert 3 or more legs is called`() {
        val dog = Dog("Fido", 2)
        assertThat(dog.validate().isValid()).isFalse()
    }
}
