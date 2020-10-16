package com.shoprunner.baleen.kotlin.kapt.test

import com.shoprunner.baleen.kotlin.dataDescription
import com.shoprunner.baleen.kotlin.validate
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.BooleanType
import com.shoprunner.baleen.types.DoubleType
import com.shoprunner.baleen.types.FloatType
import com.shoprunner.baleen.types.InstantType
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.MapType
import com.shoprunner.baleen.types.NumericType
import com.shoprunner.baleen.types.OccurrencesType
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class DefaultValueTest {
    @Test
    fun `test data class with defaults produce valid data descriptions`() {
        val model = ModelWithDefaultValues(
            noDefault = "IGNORE",
            noDefaultNullable = null,
            noDefaultInt = -1,
            noDefaultLong = -1L,
            noDefaultBigInt = -1L.toBigInteger(),
            noDefaultFloat = -1.0f,
            noDefaultDouble = -1.0,
            noDefaultBigDecimal = (-1L).toBigDecimal(),
            noDefaultInstant = Instant.now(),
            noDefaultArray = emptyArray(),
            noDefaultList = emptyList(),
            noDefaultSet = emptySet(),
            noDefaultMap = emptyMap(),
            noDefaultSubModel = SubModelWithDefaults(),
            noDefaultStringModel = StringModel("IGNORE", null)
        )

        DataDescriptionAssert.assertThat(model.dataDescription())
            .hasName("ModelWithDefaultValues")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("nullDefault") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(AllowsNull(StringType()))
                    .hasDefaultValue(null)
            }
            .hasAttribute("booleanDefault") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(BooleanType())
                    .hasDefaultValue(true)
            }
            .hasAttribute("stringDefault") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(StringType())
                    .hasDefaultValue("default")
            }
            .hasAttribute("intDefault") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(IntType())
                    .hasDefaultValue(100)
            }
            .hasAttribute("longDefault") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(LongType())
                    .hasDefaultValue(100L)
            }
            .hasAttribute("bigIntDefault") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(IntegerType())
                    .hasDefaultValue(100L.toBigInteger())
            }
            .hasAttribute("floatDefault") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(FloatType())
                    .hasDefaultValue(1.1f)
            }
            .hasAttribute("doubleDefault") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(DoubleType())
                    .hasDefaultValue(1.1)
            }
            .hasAttribute("bigDecimalDefault") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(NumericType())
                    .hasDefaultValue(100.01.toBigDecimal())
            }
            .hasAttribute("classDefault") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(SubModelWithDefaults().dataDescription())
                    .hasDefaultValue(SubModelWithDefaults())
            }
            .hasAttribute("arrayDefault") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(OccurrencesType(IntType()))
                    .hasDefaultValue(emptyArray<Int>())
            }
            .hasAttribute("listDefault") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(OccurrencesType(IntType()))
                    .hasDefaultValue(emptyList<Int>())
            }
            .hasAttribute("setDefault") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(OccurrencesType(IntType()))
                    .hasDefaultValue(emptySet<Int>())
            }
            .hasAttribute("mapDefault") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(MapType(StringType(), IntType()))
                    .hasDefaultValue(emptyMap<String, Int>())
            }
            .hasAttribute("listOfModelDefault") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(OccurrencesType(SubModelWithDefaults().dataDescription()))
                    .hasDefaultValue(emptyList<SubModelWithDefaults>())
            }
            .hasAttribute("noDefault") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(StringType())
                    .hasNoDefaultValue()
            }
            .hasAttribute("noDefaultNullable") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(AllowsNull(StringType()))
                    .hasNoDefaultValue()
            }
            .hasAttribute("noDefaultInt") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(IntType())
                    .hasNoDefaultValue()
            }
            .hasAttribute("noDefaultLong") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(LongType())
                    .hasNoDefaultValue()
            }
            .hasAttribute("noDefaultBigInt") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(IntegerType())
                    .hasNoDefaultValue()
            }
            .hasAttribute("noDefaultFloat") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(FloatType())
                    .hasNoDefaultValue()
            }
            .hasAttribute("noDefaultDouble") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(DoubleType())
                    .hasNoDefaultValue()
            }
            .hasAttribute("noDefaultBigDecimal") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(NumericType())
                    .hasNoDefaultValue()
            }
            .hasAttribute("noDefaultInstant") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(InstantType())
                    .hasNoDefaultValue()
            }
            .hasAttribute("noDefaultArray") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(OccurrencesType(IntType()))
                    .hasNoDefaultValue()
            }
            .hasAttribute("noDefaultList") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(OccurrencesType(IntType()))
                    .hasNoDefaultValue()
            }
            .hasAttribute("noDefaultSet") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(OccurrencesType(IntType()))
                    .hasNoDefaultValue()
            }
            .hasAttribute("noDefaultMap") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(MapType(StringType(), IntType()))
                    .hasNoDefaultValue()
            }
            .hasAttribute("noDefaultSubModel") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(SubModelWithDefaults().dataDescription())
                    .hasNoDefaultValue()
            }
            .hasAttribute("noDefaultStringModel") {
                AttributeDescriptionAssert.assertThat(it)
                    .hasType(StringModel("None", null).dataDescription())
                    .hasNoDefaultValue()
            }

        Assertions.assertThat(model.validate().isValid()).isTrue()
    }
}
