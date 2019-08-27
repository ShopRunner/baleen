package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat as assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class NumericTypeTest {
    @Test
    fun `passes a Double`() {
        assertThat(NumericType().validate(dataTrace(), 0.0)).isEmpty()
    }

    @Test
    fun `checks minimum value for Double`() {
        assertThat(NumericType(min = 1.0.toBigDecimal()).validate(dataTrace(), 0.0)).containsExactly(ValidationError(dataTrace(), "is less than 1.0", 0.0))
        assertThat(NumericType(min = 1.0.toBigDecimal()).validate(dataTrace(), 1.0)).isEmpty()
        assertThat(NumericType(min = 1.0.toBigDecimal()).validate(dataTrace(), 2.0)).isEmpty()
    }

    @Test
    fun `checks maximum value for Double`() {
        assertThat(NumericType(max = 1.0.toBigDecimal()).validate(dataTrace(), 0.0)).isEmpty()
        assertThat(NumericType(max = 1.0.toBigDecimal()).validate(dataTrace(), 1.0)).isEmpty()
        assertThat(NumericType(max = 1.0.toBigDecimal()).validate(dataTrace(), 2.0)).containsExactly(ValidationError(dataTrace(), "is greater than 1.0", 2.0))
    }

    @Test
    fun `passes a Float`() {
        assertThat(NumericType().validate(dataTrace(), 0.0F)).containsExactly(
            ValidationInfo(dataTrace(), message = "is coerced to double from Float", value = 0.0F)
        )
    }

    @Test
    fun `checks minimum value for Float`() {
        assertThat(NumericType(min = 1.0.toBigDecimal()).validate(dataTrace(), 0.0F)).containsExactly(ValidationError(dataTrace(), "is less than 1.0", 0.0F))
        assertThat(NumericType(min = 1.0.toBigDecimal()).validate(dataTrace(), 1.0F)).isEmpty()
        assertThat(NumericType(min = 1.0.toBigDecimal()).validate(dataTrace(), 2.0F)).isEmpty()
    }

    @Test
    fun `checks maximum value for Float`() {
        assertThat(NumericType(max = 1.0.toBigDecimal()).validate(dataTrace(), 0.0F)).isEmpty()
        assertThat(NumericType(max = 1.0.toBigDecimal()).validate(dataTrace(), 1.0F)).isEmpty()
        assertThat(NumericType(max = 1.0.toBigDecimal()).validate(dataTrace(), 2.0F)).containsExactly(ValidationError(dataTrace(), "is greater than 1.0", 2.0F))
    }

    @Test
    fun `passes a BigDecimal`() {
        assertThat(NumericType().validate(dataTrace(), 0.0.toBigDecimal())).isEmpty()
    }

    @Test
    fun `checks minimum value for BigDecimal`() {
        assertThat(NumericType(min = 1.0.toBigDecimal()).validate(dataTrace(), 0.0.toBigDecimal())).containsExactly(ValidationError(dataTrace(), "is less than 1.0", 0.0.toBigDecimal()))
        assertThat(NumericType(min = 1.0.toBigDecimal()).validate(dataTrace(), 1.0.toBigDecimal())).isEmpty()
        assertThat(NumericType(min = 1.0.toBigDecimal()).validate(dataTrace(), 2.0.toBigDecimal())).isEmpty()
    }

    @Test
    fun `checks maximum value for BigDecimal`() {
        assertThat(NumericType(max = 1.0.toBigDecimal()).validate(dataTrace(), 0.0.toBigDecimal())).isEmpty()
        assertThat(NumericType(max = 1.0.toBigDecimal()).validate(dataTrace(), 1.0.toBigDecimal())).isEmpty()
        assertThat(NumericType(max = 1.0.toBigDecimal()).validate(dataTrace(), 2.0.toBigDecimal())).containsExactly(ValidationError(dataTrace(), "is greater than 1.0", 2.0.toBigDecimal()))
    }

    @Test
    fun `passes a Byte`() {
        assertThat(NumericType().validate(dataTrace(), 0.toByte())).isEmpty()
    }

    @Test
    fun `checks minimum value for Byte`() {
        assertThat(NumericType(min = 1.toBigDecimal()).validate(dataTrace(), 0.toByte())).containsExactly(ValidationError(dataTrace(), "is less than 1", 0.toByte()))
        assertThat(NumericType(min = 1.toBigDecimal()).validate(dataTrace(), 1.toByte())).isEmpty()
        assertThat(NumericType(min = 1.toBigDecimal()).validate(dataTrace(), 2.toByte())).isEmpty()
    }

    @Test
    fun `checks maximum value for Byte`() {
        assertThat(NumericType(max = 1.toBigDecimal()).validate(dataTrace(), 0.toByte())).isEmpty()
        assertThat(NumericType(max = 1.toBigDecimal()).validate(dataTrace(), 1.toByte())).isEmpty()
        assertThat(NumericType(max = 1.toBigDecimal()).validate(dataTrace(), 2.toByte())).containsExactly(ValidationError(dataTrace(), "is greater than 1", 2.toByte()))
    }

    @Test
    fun `passes a Short`() {
        assertThat(NumericType().validate(dataTrace(), 0.toShort())).isEmpty()
    }

    @Test
    fun `checks minimum value for Short`() {
        assertThat(NumericType(min = 1.toBigDecimal()).validate(dataTrace(), 0.toShort())).containsExactly(ValidationError(dataTrace(), "is less than 1", 0.toShort()))
        assertThat(NumericType(min = 1.toBigDecimal()).validate(dataTrace(), 1.toShort())).isEmpty()
        assertThat(NumericType(min = 1.toBigDecimal()).validate(dataTrace(), 2.toShort())).isEmpty()
    }

    @Test
    fun `checks maximum value for Short`() {
        assertThat(NumericType(max = 1.toBigDecimal()).validate(dataTrace(), 0.toShort())).isEmpty()
        assertThat(NumericType(max = 1.toBigDecimal()).validate(dataTrace(), 1.toShort())).isEmpty()
        assertThat(NumericType(max = 1.toBigDecimal()).validate(dataTrace(), 2.toShort())).containsExactly(ValidationError(dataTrace(), "is greater than 1", 2.toShort()))
    }

    @Test
    fun `passes a Int`() {
        assertThat(NumericType().validate(dataTrace(), 0)).isEmpty()
    }

    @Test
    fun `checks minimum value for Int`() {
        assertThat(NumericType(min = 1.toBigDecimal()).validate(dataTrace(), 0)).containsExactly(ValidationError(dataTrace(), "is less than 1", 0))
        assertThat(NumericType(min = 1.toBigDecimal()).validate(dataTrace(), 1)).isEmpty()
        assertThat(NumericType(min = 1.toBigDecimal()).validate(dataTrace(), 2)).isEmpty()
    }

    @Test
    fun `checks maximum value for Int`() {
        assertThat(NumericType(max = 1.toBigDecimal()).validate(dataTrace(), 0)).isEmpty()
        assertThat(NumericType(max = 1.toBigDecimal()).validate(dataTrace(), 1)).isEmpty()
        assertThat(NumericType(max = 1.toBigDecimal()).validate(dataTrace(), 2)).containsExactly(ValidationError(dataTrace(), "is greater than 1", 2))
    }

    @Test
    fun `passes a Long`() {
        assertThat(NumericType().validate(dataTrace(), 0L)).isEmpty()
    }

    @Test
    fun `checks minimum value for Long`() {
        assertThat(NumericType(min = 1.toBigDecimal()).validate(dataTrace(), 0L)).containsExactly(ValidationError(dataTrace(), "is less than 1", 0L))
        assertThat(NumericType(min = 1.toBigDecimal()).validate(dataTrace(), 1L)).isEmpty()
        assertThat(NumericType(min = 1.toBigDecimal()).validate(dataTrace(), 2L)).isEmpty()
    }

    @Test
    fun `checks maximum value for Long`() {
        assertThat(NumericType(max = 1.toBigDecimal()).validate(dataTrace(), 0L)).isEmpty()
        assertThat(NumericType(max = 1.toBigDecimal()).validate(dataTrace(), 1L)).isEmpty()
        assertThat(NumericType(max = 1.toBigDecimal()).validate(dataTrace(), 2L)).containsExactly(ValidationError(dataTrace(), "is greater than 1", 2L))
    }

    @Test
    fun `passes a BigInteger`() {
        assertThat(NumericType().validate(dataTrace(), 0.toBigInteger())).isEmpty()
    }

    @Test
    fun `checks minimum value for BigInteger`() {
        assertThat(NumericType(min = 1.toBigDecimal()).validate(dataTrace(), 0L.toBigInteger())).containsExactly(ValidationError(dataTrace(), "is less than 1", 0L.toBigInteger()))
        assertThat(NumericType(min = 1.toBigDecimal()).validate(dataTrace(), 1L.toBigInteger())).isEmpty()
        assertThat(NumericType(min = 1.toBigDecimal()).validate(dataTrace(), 2L.toBigInteger())).isEmpty()
    }

    @Test
    fun `checks maximum value for BigInteger`() {
        assertThat(NumericType(max = 1.toBigDecimal()).validate(dataTrace(), 0L.toBigInteger())).isEmpty()
        assertThat(NumericType(max = 1.toBigDecimal()).validate(dataTrace(), 1L.toBigInteger())).isEmpty()
        assertThat(NumericType(max = 1.toBigDecimal()).validate(dataTrace(), 2L.toBigInteger())).containsExactly(ValidationError(dataTrace(), "is greater than 1", 2L.toBigInteger()))
    }

    @Test
    fun `checks null`() {
        assertThat(NumericType().validate(dataTrace(), null)).containsExactly(ValidationError(dataTrace(), "is null", null))
    }

    @Test
    fun `checks not numeric`() {
        assertThat(NumericType().validate(dataTrace(), false)).containsExactly(ValidationError(dataTrace(), "is not a number", false))
    }
}
