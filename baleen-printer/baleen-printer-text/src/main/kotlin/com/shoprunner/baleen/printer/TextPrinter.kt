package com.shoprunner.baleen.printer

import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationResult
import com.shoprunner.baleen.ValidationSuccess
import com.shoprunner.baleen.ValidationSummary
import com.shoprunner.baleen.ValidationWarning
import java.io.OutputStreamWriter

class TextPrinter(private val writer: OutputStreamWriter, val prettyPrint: Boolean = false) : Printer {

    override fun print(validationResult: ValidationResult) {
        if (prettyPrint) {
            prettyPrint(validationResult, 0)
            writer.append("\n")
        } else {
            writer.append("$validationResult\n")
        }
    }

    private fun prettyPrint(result: ValidationResult, nestedLevel: Int) {
        when (result) {
            is ValidationSuccess -> prettyPrint(result, nestedLevel)
            is ValidationInfo -> prettyPrint(result, nestedLevel)
            is ValidationError -> prettyPrint(result, nestedLevel)
            is ValidationWarning -> prettyPrint(result, nestedLevel)
            is ValidationSummary -> prettyPrint(result, nestedLevel)
        }
    }

    private fun prettyPrint(result: ValidationSuccess, nestedLevel: Int) {
        val indent = " ".repeat(2 * nestedLevel)
        writer.append(
            """
            |${indent}ValidationSuccess(
            |$indent  dataTrace=${result.dataTrace},
            |$indent  value=${result.value}
            |$indent)
        """.trimMargin()
        )
    }

    private fun prettyPrint(result: ValidationInfo, nestedLevel: Int) {
        val indent = " ".repeat(2 * nestedLevel)
        writer.append(
            """
            |${indent}ValidationInfo(
            |$indent  dataTrace=${result.dataTrace},
            |$indent  message=${result.message},
            |$indent  value=${result.value}
            |$indent)
        """.trimMargin()
        )
    }

    private fun prettyPrint(result: ValidationError, nestedLevel: Int) {
        val indent = " ".repeat(2 * nestedLevel)
        writer.append(
            """
            |${indent}ValidationError(
            |$indent  dataTrace=${result.dataTrace},
            |$indent  message=${result.message},
            |$indent  value=${result.value}
            |$indent)
        """.trimMargin()
        )
    }

    private fun prettyPrint(result: ValidationWarning, nestedLevel: Int) {
        val indent = " ".repeat(2 * nestedLevel)
        writer.append(
            """
            |${indent}ValidationWarning(
            |$indent  dataTrace=${result.dataTrace},
            |$indent  message=${result.message},
            |$indent  value=${result.value}
            |$indent)
        """.trimMargin()
        )
    }

    private fun prettyPrint(result: ValidationSummary, nestedLevel: Int) {
        val indent = " ".repeat(2 * nestedLevel)
        writer.append(
            """
            |${indent}ValidationSummary(
            |$indent  dataTrace=${result.dataTrace},
            |$indent  summary=${result.summary},
            |$indent  numSuccesses=${result.numSuccesses},
            |$indent  numInfos=${result.numInfos},
            |$indent  numErrors=${result.numErrors},
            |$indent  numWarnings=${result.numWarnings},
            |$indent  topErrorsAndWarnings=[
        """.trimMargin()
        )
        result.topErrorsAndWarnings
            .forEach {
                writer.append("\n")
                prettyPrint(it, nestedLevel + 2)
            }

        writer.append("\n$indent  ]\n$indent)")
    }

    override fun print(validationResults: Iterable<ValidationResult>) {
        validationResults.forEach {
            print(it)
        }
    }
}
