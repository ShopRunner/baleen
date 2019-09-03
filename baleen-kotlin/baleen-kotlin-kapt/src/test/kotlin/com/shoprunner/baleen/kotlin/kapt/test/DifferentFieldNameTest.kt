package com.shoprunner.baleen.kotlin.kapt.test

import com.shoprunner.baleen.kotlin.dataDescription
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DifferentFieldNameTest {

    @Test
    fun `test that field names are renamed`() {
        val model = ModelWithDifferentFieldNames("field name")

        DataDescriptionAssert.assertBaleen(model.dataDescription())
            .hasName("ModelWithDifferentFieldNames")
            .hasAttribute("field_name")
    }
}
