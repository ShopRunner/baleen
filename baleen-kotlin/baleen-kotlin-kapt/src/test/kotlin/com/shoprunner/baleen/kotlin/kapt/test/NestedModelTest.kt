package com.shoprunner.baleen.kotlin.kapt.test

import com.shoprunner.baleen.kotlin.dataDescription
import com.shoprunner.baleen.kotlin.validate
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class NestedModelTest {
    private val stringModel = StringModel("string", null)
    private val intModel = IntModel(1, null)

    private val nestedModel = NestedModel(
        nestedStringModel = stringModel,
        nullableNestedStringModel = null,
        nestedIntModel = intModel,
        nullableNestedIntModel = null
    )

    @Test
    fun `test data class with nested data produce valid data descriptions`() {
        DataDescriptionAssert.assertBaleen(nestedModel.dataDescription())
            .hasName("NestedModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("nestedStringModel", stringModel.dataDescription())
            .hasAttribute("nullableNestedStringModel", AllowsNull(stringModel.dataDescription()))
            .hasAttribute("nestedIntModel", intModel.dataDescription())
            .hasAttribute("nullableNestedIntModel", AllowsNull(intModel.dataDescription()))

        assertThat(nestedModel.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with list of nested data produce valid data descriptions`() {
        val model = ListNestedModel(listOf(nestedModel), null)

        DataDescriptionAssert.assertBaleen(model.dataDescription())
            .hasName("ListNestedModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("nested", OccurrencesType(nestedModel.dataDescription()))
            .hasAttribute("nullableNested", AllowsNull(OccurrencesType(nestedModel.dataDescription())))

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with array of nested data produce valid data descriptions`() {
        val model = ArrayNestedModel(arrayOf(nestedModel), null)

        DataDescriptionAssert.assertBaleen(model.dataDescription())
            .hasName("ArrayNestedModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("nested", OccurrencesType(nestedModel.dataDescription()))
            .hasAttribute("nullableNested", AllowsNull(OccurrencesType(nestedModel.dataDescription())))

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with set of nested data produce valid data descriptions`() {
        val model = SetNestedModel(setOf(nestedModel), null)

        DataDescriptionAssert.assertBaleen(model.dataDescription())
            .hasName("SetNestedModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("nested", OccurrencesType(nestedModel.dataDescription()))
            .hasAttribute("nullableNested", AllowsNull(OccurrencesType(nestedModel.dataDescription())))

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with iterable of nested data produce valid data descriptions`() {
        val model = IterableNestedModel(setOf(nestedModel), null)

        DataDescriptionAssert.assertBaleen(model.dataDescription())
            .hasName("IterableNestedModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("nested", OccurrencesType(nestedModel.dataDescription()))
            .hasAttribute("nullableNested", AllowsNull(OccurrencesType(nestedModel.dataDescription())))

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with map of string to ints produce valid data descriptions`() {
        val model = MapNestedModel(
            mapNestedKeys = mapOf(nestedModel to "value"),
            mapNestedValues = mapOf("key" to nestedModel),
            nullableMapNestedKeys = null,
            nullableMapNestedValues = null
        )

        DataDescriptionAssert.assertBaleen(model.dataDescription())
            .hasName("MapNestedModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("mapNestedKeys",
                MapType(
                    nestedModel.dataDescription(),
                    StringType()
                )
            )
            .hasAttribute("mapNestedValues",
                MapType(
                    StringType(),
                    nestedModel.dataDescription()
                )
            )
            .hasAttribute("nullableMapNestedKeys",
                AllowsNull(
                    MapType(
                        nestedModel.dataDescription(),
                        IntType()
                    )
                )
            )
            .hasAttribute("nullableMapNestedValues",
                AllowsNull(
                    MapType(
                        IntType(),
                        nestedModel.dataDescription()
                    )
                )
            )

        assertThat(model.validate().isValid()).isTrue()
    }
}
