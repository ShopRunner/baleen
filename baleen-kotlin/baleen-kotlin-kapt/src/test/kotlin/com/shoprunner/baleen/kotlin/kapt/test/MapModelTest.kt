package com.shoprunner.baleen.kotlin.kapt.test

import com.shoprunner.baleen.kotlin.dataDescription
import com.shoprunner.baleen.kotlin.validate
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MapModelTest {

    @Test
    fun `test data class with map of string to ints produce valid data descriptions`() {
        val model = MapStringIntModel(mapOf("key" to 1), null)

        DataDescriptionAssert.assertBaleen(model.dataDescription())
            .hasName("MapStringIntModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("map",
                MapType(
                    StringType(),
                    IntType()
                )
            )
            .hasAttribute("nullableMap",
                AllowsNull(
                    MapType(
                        StringType(),
                        IntType()
                    )
                )
            )

        assertThat(model.validate().isValid()).isTrue()
    }
}
