package com.shoprunner.baleen.types

import com.shoprunner.baleen.SequenceAssert.Companion.assertThat
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.dataTrace
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class IntegerTypeTest {

    @Test
    fun `passes a Byte`() {
        assertThat(IntegerType().validate(dataTrace(), 0.toByte())).isEmpty()
    }

    @Test
    fun `checks minimum value for Byte`() {
        assertThat(IntegerType(min = 1.toBigInteger()).validate(dataTrace(), 0.toByte())).containsExactly(ValidationError(dataTrace(), "is less than 1", 0.toByte()))
        assertThat(IntegerType(min = 1.toBigInteger()).validate(dataTrace(), 1.toByte())).isEmpty()
        assertThat(IntegerType(min = 1.toBigInteger()).validate(dataTrace(), 2.toByte())).isEmpty()
    }

    @Test
    fun `checks maximum value for Byte`() {
        assertThat(IntegerType(max = 1.toBigInteger()).validate(dataTrace(), 0.toByte())).isEmpty()
        assertThat(IntegerType(max = 1.toBigInteger()).validate(dataTrace(), 1.toByte())).isEmpty()
        assertThat(IntegerType(max = 1.toBigInteger()).validate(dataTrace(), 2.toByte())).containsExactly(ValidationError(dataTrace(), "is greater than 1", 2.toByte()))
    }

    @Test
    fun `passes a Short`() {
        assertThat(IntegerType().validate(dataTrace(), 0.toShort())).isEmpty()
    }

    @Test
    fun `checks minimum value for Short`() {
        assertThat(IntegerType(min = 1.toBigInteger()).validate(dataTrace(), 0.toShort())).containsExactly(ValidationError(dataTrace(), "is less than 1", 0.toShort()))
        assertThat(IntegerType(min = 1.toBigInteger()).validate(dataTrace(), 1.toShort())).isEmpty()
        assertThat(IntegerType(min = 1.toBigInteger()).validate(dataTrace(), 2.toShort())).isEmpty()
    }

    @Test
    fun `checks maximum value for Short`() {
        assertThat(IntegerType(max = 1.toBigInteger()).validate(dataTrace(), 0.toShort())).isEmpty()
        assertThat(IntegerType(max = 1.toBigInteger()).validate(dataTrace(), 1.toShort())).isEmpty()
        assertThat(IntegerType(max = 1.toBigInteger()).validate(dataTrace(), 2.toShort())).containsExactly(ValidationError(dataTrace(), "is greater than 1", 2.toShort()))
    }

    @Test
    fun `passes a Int`() {
        assertThat(IntegerType().validate(dataTrace(), 0)).isEmpty()
    }

    @Test
    fun `checks minimum value for Int`() {
        assertThat(IntegerType(min = 1.toBigInteger()).validate(dataTrace(), 0)).containsExactly(ValidationError(dataTrace(), "is less than 1", 0))
        assertThat(IntegerType(min = 1.toBigInteger()).validate(dataTrace(), 1)).isEmpty()
        assertThat(IntegerType(min = 1.toBigInteger()).validate(dataTrace(), 2)).isEmpty()
    }

    @Test
    fun `checks maximum value for Int`() {
        assertThat(IntegerType(max = 1.toBigInteger()).validate(dataTrace(), 0)).isEmpty()
        assertThat(IntegerType(max = 1.toBigInteger()).validate(dataTrace(), 1)).isEmpty()
        assertThat(IntegerType(max = 1.toBigInteger()).validate(dataTrace(), 2)).containsExactly(ValidationError(dataTrace(), "is greater than 1", 2))
    }

    @Test
    fun `passes a Long`() {
        assertThat(IntegerType().validate(dataTrace(), 0L)).isEmpty()
    }

    @Test
    fun `checks minimum value for Long`() {
        assertThat(IntegerType(min = 1.toBigInteger()).validate(dataTrace(), 0L)).containsExactly(ValidationError(dataTrace(), "is less than 1", 0L))
        assertThat(IntegerType(min = 1.toBigInteger()).validate(dataTrace(), 1L)).isEmpty()
        assertThat(IntegerType(min = 1.toBigInteger()).validate(dataTrace(), 2L)).isEmpty()
    }

    @Test
    fun `checks maximum value for Long`() {
        assertThat(IntegerType(max = 1.toBigInteger()).validate(dataTrace(), 0L)).isEmpty()
        assertThat(IntegerType(max = 1.toBigInteger()).validate(dataTrace(), 1L)).isEmpty()
        assertThat(IntegerType(max = 1.toBigInteger()).validate(dataTrace(), 2L)).containsExactly(ValidationError(dataTrace(), "is greater than 1", 2L))
    }

    @Test
    fun `passes a BigInteger`() {
        assertThat(IntegerType().validate(dataTrace(), 0.toBigInteger())).isEmpty()
    }

    @Test
    fun `checks minimum value for BigInteger`() {
        assertThat(IntegerType(min = 1.toBigInteger()).validate(dataTrace(), 0L.toBigInteger())).containsExactly(ValidationError(dataTrace(), "is less than 1", 0L.toBigInteger()))
        assertThat(IntegerType(min = 1.toBigInteger()).validate(dataTrace(), 1L.toBigInteger())).isEmpty()
        assertThat(IntegerType(min = 1.toBigInteger()).validate(dataTrace(), 2L.toBigInteger())).isEmpty()
    }

    @Test
    fun `checks maximum value for BigInteger`() {
        assertThat(IntegerType(max = 1.toBigInteger()).validate(dataTrace(), 0L.toBigInteger())).isEmpty()
        assertThat(IntegerType(max = 1.toBigInteger()).validate(dataTrace(), 1L.toBigInteger())).isEmpty()
        assertThat(IntegerType(max = 1.toBigInteger()).validate(dataTrace(), 2L.toBigInteger())).containsExactly(ValidationError(dataTrace(), "is greater than 1", 2L.toBigInteger()))
    }

    @Test
    fun `checks null`() {
        assertThat(IntegerType().validate(dataTrace(), null)).containsExactly(ValidationError(dataTrace(), "is null", null))
    }

    @Test
    fun `checks not integer`() {
        assertThat(IntegerType().validate(dataTrace(), false)).containsExactly(ValidationError(dataTrace(), "is not an integer", false))
    }
}
