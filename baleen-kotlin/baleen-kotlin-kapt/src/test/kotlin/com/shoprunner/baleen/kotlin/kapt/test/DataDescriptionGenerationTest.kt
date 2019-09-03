package com.shoprunner.baleen.kotlin.kapt.test

import com.shoprunner.baleen.kotlin.dataDescription
import com.shoprunner.baleen.kotlin.kapt.test.DataDescriptionAssert.Companion.assertBaleen
import com.shoprunner.baleen.kotlin.validate
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DataDescriptionGenerationTest {

    @Test
    fun `test data class valid data descriptions`() {
        val model = StringModel("hello", null)

        assertBaleen(model.dataDescription())
            .hasName("StringModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasMarkdownDescription("This is a string model")
            .hasAttribute("string") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(StringType())
                    .hasMarkdownDescription("A string field")
            }
            .hasAttribute("nullableString") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(AllowsNull(StringType()))
                    .hasMarkdownDescription("A nullable string field")
            }

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class valid data descriptions with manually specified name and namespace`() {
        val model = StringModelWithOverriddenName("hello", null)

        assertBaleen(model.dataDescription())
            .hasName("ManuallyNamed")
            .hasNamespace("com.shoprunner.baleen.kotlin.different")
            .hasMarkdownDescription("This is a string model")
            .hasAttribute("string") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(StringType())
                    .hasMarkdownDescription("A string field")
            }
            .hasAttribute("nullableString") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(AllowsNull(StringType()))
                    .hasMarkdownDescription("A nullable string field")
            }

        assertThat(model.validate().isValid()).isTrue()
    }
}
