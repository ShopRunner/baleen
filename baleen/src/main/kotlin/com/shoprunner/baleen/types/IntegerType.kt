package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult
import java.math.BigInteger

/**
 * A type that can be Byte, Short, Int, Long, or BigInteger.  Less strict in typing than IntType or LongType.
 */
class IntegerType(val min: BigInteger? = null, val max: BigInteger? = null) : BaleenType {

    override fun name(): String = "integer"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when (value) {
                null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                is Byte ->
                    when {
                        min != null && value.toInt().toBigInteger() < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                        max != null && value.toInt().toBigInteger() > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                        else -> sequenceOf()
                    }
                is Short ->
                    when {
                        min != null && value.toInt().toBigInteger() < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                        max != null && value.toInt().toBigInteger() > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                        else -> sequenceOf()
                    }
                is Int ->
                    when {
                        min != null && value.toBigInteger() < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                        max != null && value.toBigInteger() > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                        else -> sequenceOf()
                    }
                is Long ->
                    when {
                        min != null && value.toBigInteger() < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                        max != null && value.toBigInteger() > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                        else -> sequenceOf()
                    }
                is BigInteger ->
                    when {
                        min != null && value < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                        max != null && value > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                        else -> sequenceOf()
                    }
                else -> sequenceOf(ValidationError(dataTrace, "is not an integer", value))
            }
}
