package com.shoprunner.baleen.kotlin.kapt.test

import com.shoprunner.baleen.kotlin.dataDescription
import com.shoprunner.baleen.kotlin.validate
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class OccurrencesModelTest {
    @Test
    fun `test data class with list of strings produce valid data descriptions`() {
        val model = ListStringModel(listOf("hello"), null)

        DataDescriptionAssert.assertBaleen(model.dataDescription())
            .hasName("ListStringModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute(
                "stringList",
                OccurrencesType(StringType())
            )
            .hasAttribute(
                "nullableStringList",
                AllowsNull(OccurrencesType(StringType()))
            )

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with list of primitive ints produce valid data descriptions`() {
        val model = ListIntModel(listOf(1), null)

        DataDescriptionAssert.assertBaleen(model.dataDescription())
            .hasName("ListIntModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("intList", OccurrencesType(IntType()))
            .hasAttribute(
                "nullableIntList",
                AllowsNull(OccurrencesType(IntType()))
            )

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with array of strings produce valid data descriptions`() {
        val model = ArrayStringModel(arrayOf("hello"), null)

        DataDescriptionAssert.assertBaleen(model.dataDescription())
            .hasName("ArrayStringModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute(
                "stringArray",
                OccurrencesType(StringType())
            )
            .hasAttribute(
                "nullableStringArray",
                AllowsNull(OccurrencesType(StringType()))
            )

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with array of primitive ints produce valid data descriptions`() {
        val model = ArrayIntModel(arrayOf(1), null)

        DataDescriptionAssert.assertBaleen(model.dataDescription())
            .hasName("ArrayIntModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute(
                "intArray",
                OccurrencesType(IntType())
            )
            .hasAttribute(
                "nullableIntArray",
                AllowsNull(OccurrencesType(IntType()))
            )

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with set of strings produce valid data descriptions`() {
        val model = SetStringModel(setOf("hello"), null)

        DataDescriptionAssert.assertBaleen(model.dataDescription())
            .hasName("SetStringModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute(
                "stringSet",
                OccurrencesType(StringType())
            )
            .hasAttribute(
                "nullableStringSet",
                AllowsNull(OccurrencesType(StringType()))
            )

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with set of primitive ints produce valid data descriptions`() {
        val model = SetIntModel(setOf(1), null)

        DataDescriptionAssert.assertBaleen(model.dataDescription())
            .hasName("SetIntModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("intSet", OccurrencesType(IntType()))
            .hasAttribute(
                "nullableIntSet",
                AllowsNull(OccurrencesType(IntType()))
            )

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with iterable of strings produce valid data descriptions`() {
        val model = IterableStringModel(setOf("hello"), null)

        DataDescriptionAssert.assertBaleen(model.dataDescription())
            .hasName("IterableStringModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute(
                "stringIterable",
                OccurrencesType(StringType())
            )
            .hasAttribute(
                "nullableStringIterable",
                AllowsNull(OccurrencesType(StringType()))
            )

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with iterable of primitive ints produce valid data descriptions`() {
        val model = IterableIntModel(setOf(1), null)

        DataDescriptionAssert.assertBaleen(model.dataDescription())
            .hasName("IterableIntModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute(
                "intIterable",
                OccurrencesType(IntType())
            )
            .hasAttribute(
                "nullableIntIterable",
                AllowsNull(OccurrencesType(IntType()))
            )

        assertThat(model.validate().isValid()).isTrue()
    }
}
