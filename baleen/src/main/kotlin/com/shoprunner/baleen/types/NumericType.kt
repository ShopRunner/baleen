package com.shoprunner.baleen.types

import com.shoprunner.baleen.BaleenType
import com.shoprunner.baleen.DataTrace
import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationResult
import java.math.BigDecimal
import java.math.BigInteger

/**
 * A type that can be Byte, Short, Int, Long, BigInteger, Float, Double, or BigDecimal.  Least strict of any numeric types
 */
class NumericType(val min: BigDecimal? = null, val max: BigDecimal? = null) : BaleenType {

    override fun name(): String = "number"

    override fun validate(dataTrace: DataTrace, value: Any?): Sequence<ValidationResult> =
            when (value) {
                null -> sequenceOf(ValidationError(dataTrace, "is null", value))
                is Byte ->
                    when {
                        min != null && value.toInt().toBigDecimal() < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                        max != null && value.toInt().toBigDecimal() > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                        else -> sequenceOf()
                    }
                is Short ->
                    when {
                        min != null && value.toInt().toBigDecimal() < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                        max != null && value.toInt().toBigDecimal() > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                        else -> sequenceOf()
                    }
                is Int ->
                    when {
                        min != null && value.toBigDecimal() < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                        max != null && value.toBigDecimal() > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                        else -> sequenceOf()
                    }
                is Long ->
                    when {
                        min != null && value.toBigDecimal() < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                        max != null && value.toBigDecimal() > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                        else -> sequenceOf()
                    }
                is BigInteger ->
                    when {
                        min != null && value.toBigDecimal() < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                        max != null && value.toBigDecimal() > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                        else -> sequenceOf()
                    }
                is Float ->
                    when {
                        min != null && value.toBigDecimal() < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                        max != null && value.toBigDecimal() > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                        else -> sequenceOf()
                    }
                is Double ->
                    when {
                        min != null && value.toBigDecimal() < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                        max != null && value.toBigDecimal() > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                        else -> sequenceOf()
                    }
                is BigDecimal ->
                    when {
                        min != null && value < min -> sequenceOf(ValidationError(dataTrace, "is less than $min", value))
                        max != null && value > max -> sequenceOf(ValidationError(dataTrace, "is greater than $max", value))
                        else -> sequenceOf()
                    }
                else -> sequenceOf(ValidationError(dataTrace, "is not a number", value))
            }
}
