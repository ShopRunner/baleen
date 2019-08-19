package com.shoprunner.baleen.kotlin.kapt.test

import com.shoprunner.baleen.kotlin.validate
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DataTestFunctionsGenerationTest {

    @Test
    fun `tests 'assertStringLength' a @DataTest with Sequence return value validates correctly`() {
        val goodData = StringModel("string", null)
        val badData = StringModel("string".repeat(1000), null)

        assertThat(goodData.validate().isValid()).isTrue()
        assertThat(badData.validate().isValid()).isFalse()
    }

    @Test
    fun `tests 'assertMax' a @DataTest extension fun with Sequence return value validates correctly`() {
        val goodData = IntModel(1, null)
        val badData = IntModel(1000, null)

        assertThat(goodData.validate().isValid()).isTrue()
        assertThat(badData.validate().isValid()).isFalse()
    }

    @Test
    fun `tests 'assertListNotEmpty' a @DataTest with Iterable return value validates correctly`() {
        val goodData = ListStringModel(listOf("string"), null)
        val badData = ListStringModel(emptyList(), null)

        assertThat(goodData.validate().isValid()).isTrue()
        assertThat(badData.validate().isValid()).isFalse()
    }

    @Test
    fun `tests 'assertArrayNotEmpty' a @DataTest with Iterable return value validates correctly`() {
        val goodData = ArrayStringModel(arrayOf("string"), null)
        val badData = ArrayStringModel(emptyArray(), null)

        assertThat(goodData.validate().isValid()).isTrue()
        assertThat(badData.validate().isValid()).isFalse()
    }
}
