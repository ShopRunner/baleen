package com.shoprunner.baleen.kotlin.example

import com.shoprunner.baleen.kotlin.dataDescription
import com.shoprunner.baleen.kotlin.validate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class PackValidationTest {
    @Test
    fun `test data description is generated`() {
        val dog = Dog("Fido", 4)
        val pack = Pack("Wolfpack", listOf(dog))
        assertThat(pack.dataDescription()).isNotNull
    }

    @Test
    fun `test validate method is generated`() {
        val dog = Dog("Fido", 4)
        val pack = Pack("Wolfpack", listOf(dog))
        assertThat(pack.validate().isValid()).isTrue()
    }

    @Test
    fun `test data test 'assertNotEmptyPack' is called`() {
        val pack = Pack("Wolfpack", emptyList())
        assertThat(pack.validate().isValid()).isFalse()
    }
}
