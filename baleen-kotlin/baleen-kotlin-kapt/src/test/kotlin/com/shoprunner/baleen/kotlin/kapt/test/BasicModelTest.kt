package com.shoprunner.baleen.kotlin.kapt.test

import com.shoprunner.baleen.kotlin.dataDescription
import com.shoprunner.baleen.kotlin.kapt.test.DataDescriptionAssert.Companion.assertBaleen
import com.shoprunner.baleen.kotlin.validate
import com.shoprunner.baleen.types.AllowsNull
import com.shoprunner.baleen.types.BooleanType
import com.shoprunner.baleen.types.DoubleType
import com.shoprunner.baleen.types.FloatType
import com.shoprunner.baleen.types.InstantType
import com.shoprunner.baleen.types.IntType
import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.LongType
import com.shoprunner.baleen.types.NumericType
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.time.Instant

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class BasicModelTest {

    @Test
    fun `test data class with strings produce valid data descriptions`() {
        val model = StringModel("hello", null)

        assertBaleen(model.dataDescription())
            .hasName("StringModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("string", StringType())
            .hasAttribute("nullableString", AllowsNull(StringType()))

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with ints produce valid data descriptions`() {
        val model = IntModel(1, null)

        assertBaleen(model.dataDescription())
            .hasName("IntModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("intNumber", IntType())
            .hasAttribute("nullableIntNumber", AllowsNull(IntType()))

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with longs produce valid data descriptions`() {
        val model = LongModel(1, null)

        assertBaleen(model.dataDescription())
            .hasName("LongModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("longNumber", LongType())
            .hasAttribute("nullableLongNumber", AllowsNull(LongType()))

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with floats produce valid data descriptions`() {
        val model = FloatModel(1.0f, null)

        assertBaleen(model.dataDescription())
            .hasName("FloatModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("floatNumber", FloatType())
            .hasAttribute("nullableFloatNumber", AllowsNull(FloatType()))

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with doubles produce valid data descriptions`() {
        val model = DoubleModel(1.0, null)

        assertBaleen(model.dataDescription())
            .hasName("DoubleModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("doubleNumber", DoubleType())
            .hasAttribute("nullableDoubleNumber", AllowsNull(DoubleType()))

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with bytes produce valid data descriptions`() {
        val model = ByteModel(1, null)

        assertBaleen(model.dataDescription())
            .hasName("ByteModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("byteNumber", IntegerType(min = Byte.MIN_VALUE.toInt().toBigInteger(), max = Byte.MAX_VALUE.toInt().toBigInteger()))
            .hasAttribute("nullableByteNumber", AllowsNull(IntegerType(min = Byte.MIN_VALUE.toInt().toBigInteger(), max = Byte.MAX_VALUE.toInt().toBigInteger())))

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with shorts produce valid data descriptions`() {
        val model = ShortModel(1, null)

        assertBaleen(model.dataDescription())
            .hasName("ShortModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("shortNumber", IntegerType(min = Short.MIN_VALUE.toInt().toBigInteger(), max = Short.MAX_VALUE.toInt().toBigInteger()))
            .hasAttribute("nullableShortNumber", AllowsNull(IntegerType(min = Short.MIN_VALUE.toInt().toBigInteger(), max = Short.MAX_VALUE.toInt().toBigInteger())))

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with BigIntegers produce valid data descriptions`() {
        val model = BigIntegerModel(1.toBigInteger(), null)

        assertBaleen(model.dataDescription())
            .hasName("BigIntegerModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("bigIntegerNumber", IntegerType())
            .hasAttribute("nullableBigIntegerNumber", AllowsNull(IntegerType()))

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with BigDecimal produce valid data descriptions`() {
        val model = BigDecimalModel(1.0.toBigDecimal(), null)

        assertBaleen(model.dataDescription())
            .hasName("BigDecimalModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("bigDecimalNumber", NumericType())
            .hasAttribute("nullableBigDecimalNumber", AllowsNull(NumericType()))

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with booleans produce valid data descriptions`() {
        val model = BooleanModel(true, null)

        assertBaleen(model.dataDescription())
            .hasName("BooleanModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("bool", BooleanType())
            .hasAttribute("nullableBool", AllowsNull(BooleanType()))

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class with instants produce valid data descriptions`() {
        val model = InstantModel(Instant.now(), null)

        assertBaleen(model.dataDescription())
            .hasName("InstantModel")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("instant", InstantType())
            .hasAttribute("nullableInstant", AllowsNull(InstantType()))

        assertThat(model.validate().isValid()).isTrue()
    }

    @Test
    fun `test data class won't serialize companion object`() {
        val model = ModelWithCompanionObject("hello")

        assertBaleen(model.dataDescription())
            .hasName("ModelWithCompanionObject")
            .hasNamespace("com.shoprunner.baleen.kotlin.kapt.test")
            .hasAttribute("message", StringType())

        assertThat(model.validate().isValid()).isTrue()
    }
}
