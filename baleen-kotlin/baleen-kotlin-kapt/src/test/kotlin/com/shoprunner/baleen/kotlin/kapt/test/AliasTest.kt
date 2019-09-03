package com.shoprunner.baleen.kotlin.kapt.test

import com.shoprunner.baleen.kotlin.dataDescription
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AliasTest {

    @Test
    fun `test that field names have aliases`() {
        val model = ModelWithAliases("field1", "field2")

        DataDescriptionAssert.assertBaleen(model.dataDescription())
            .hasName("ModelWithAliases")
            .hasAttribute("fieldName") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasAlias("field_name")
            }
            .hasAttribute("anotherName") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasAlias("another_name1", "another_name2")
            }
    }
}
