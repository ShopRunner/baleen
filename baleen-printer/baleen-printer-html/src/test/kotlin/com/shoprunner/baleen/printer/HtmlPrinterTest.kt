package com.shoprunner.baleen.printer

import com.shoprunner.baleen.ValidationError
import com.shoprunner.baleen.ValidationInfo
import com.shoprunner.baleen.ValidationSuccess
import com.shoprunner.baleen.ValidationSummary
import com.shoprunner.baleen.ValidationWarning
import com.shoprunner.baleen.dataTrace
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

internal class HtmlPrinterTest {

    @Test
    fun `print html`() {
        val results = sequenceOf(
            ValidationSuccess(dataTrace("test").tag("tag" to "value"), "one"),
            ValidationInfo(dataTrace("test").tag("tag" to "value"), "two", "two"),
            ValidationError(dataTrace("test").tag("tag" to "value"), "three", "three"),
            ValidationWarning(dataTrace("test").tag("tag" to "value"), "four", "four"),
        ).asIterable()

        val file = java.io.File.createTempFile("tmp", ".html")

        file.writer().use {
            HtmlPrinter(it).print(results)
        }

        Assertions.assertThat(file.readText()).isEqualToIgnoringWhitespace(
            """
<html>
<head>
    <title>Baleen Results</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-gtEjrD/SeCtmISkJkNUaaKMoLD0//ElJ19smozuHV6z3Iehds+3Ulb9Bn9Plx0x4"
            crossorigin="anonymous"></script>
</head>
<body>
<h2>All results</h2>
<table class="table table-striped">
    <thead>
    <tr>
        <th scope="col">type</th>
        <th scope="col">message</th>
        <th scope="col">value</th>
        <th scope="col">tags</th>
        <th scope="col">dataTrace</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>SUCCESS</td>
        <td></td>
        <td>one</td>
        <td>
            <table class="table mb-0">
                <thead>
                <tr>
                    <th scope="col">tag</th>
                    <th scope="col">value</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>tag</td>
                    <td>value</td>
                </tr>
                </tbody>
            </table>
        </td>
        <td>test</td>
    </tr>
    <tr>
        <td>INFO</td>
        <td>two</td>
        <td>two</td>
        <td>
            <table class="table mb-0">
                <thead>
                <tr>
                    <th scope="col">tag</th>
                    <th scope="col">value</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>tag</td>
                    <td>value</td>
                </tr>
                </tbody>
            </table>
        </td>
        <td>test</td>
    </tr>
    <tr>
        <td>ERROR</td>
        <td>three</td>
        <td>three</td>
        <td>
            <table class="table mb-0">
                <thead>
                <tr>
                    <th scope="col">tag</th>
                    <th scope="col">value</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>tag</td>
                    <td>value</td>
                </tr>
                </tbody>
            </table>
        </td>
        <td>test</td>
    </tr>
    <tr>
        <td>WARNING</td>
        <td>four</td>
        <td>four</td>
        <td>
            <table class="table mb-0">
                <thead>
                <tr>
                    <th scope="col">tag</th>
                    <th scope="col">value</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>tag</td>
                    <td>value</td>
                </tr>
                </tbody>
            </table>
        </td>
        <td>test</td>
    </tr>
    </tbody>
</table>
</body>
</html>
            """.trimIndent()
        )
    }

    @Test
    fun `print summary html`() {
        val results = sequenceOf(
            ValidationSummary(
                dataTrace(), "Summary1", 1, 1, 1, 1,
                listOf(
                    ValidationError(dataTrace("test").tag("tag" to "value"), "three", "three"),
                    ValidationWarning(dataTrace("test").tag("tag" to "value"), "four", "four"),
                )
            ),
            ValidationSummary(
                dataTrace("test").tag("tag" to "value"), "Summary2", 1, 1, 1, 1,
                listOf(
                    ValidationError(dataTrace("test").tag("tag" to "value"), "three", "three"),
                    ValidationWarning(dataTrace("test").tag("tag" to "value"), "four", "four"),
                )
            ),
        ).asIterable()

        val file = java.io.File.createTempFile("tmp", ".html")

        file.writer().use {
            HtmlPrinter(it).print(results)
        }

        Assertions.assertThat(file.readText()).isEqualToIgnoringWhitespace(
            """
<html>
<head>
    <title>Baleen Results</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-+0n0xVW2eSR5OomGNYDnhzAbDsOXxcvSN1TPprVMTNDbiYZCxYbOOl7+AMvyTG2x" crossorigin="anonymous">
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.1/dist/js/bootstrap.bundle.min.js"
            integrity="sha384-gtEjrD/SeCtmISkJkNUaaKMoLD0//ElJ19smozuHV6z3Iehds+3Ulb9Bn9Plx0x4"
            crossorigin="anonymous"></script>
</head>
<body>
<h2>Summary</h2>
<table class="table table-striped">
    <thead>
    <tr>
        <th scope="col">summary</th>
        <th scope="col">numSuccesses</th>
        <th scope="col">numInfos</th>
        <th scope="col">numWarnings</th>
        <th scope="col">numErrors</th>
        <th scope="col">tags</th>
        <th scope="col">dataTrace</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>Summary1</td>
        <td>1</td>
        <td>1</td>
        <td>1</td>
        <td>1</td>
        <td>
            <table class="table mb-0">
                <thead>
                <tr>
                    <th scope="col">tag</th>
                    <th scope="col">value</th>
                </tr>
                </thead>
                <tbody>
                <tr></tr>
                </tbody>
            </table>
        </td>
        <td></td>
    </tr>
    <tr>
        <td>Summary2</td>
        <td>1</td>
        <td>1</td>
        <td>1</td>
        <td>1</td>
        <td>
            <table class="table mb-0">
                <thead>
                <tr>
                    <th scope="col">tag</th>
                    <th scope="col">value</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>tag</td>
                    <td>value</td>
                </tr>
                </tbody>
            </table>
        </td>
        <td>test</td>
    </tr>
    </tbody>
</table>
<h2>Summary1 - Top Errors and Warnings</h2>
<table class="table table-striped">
    <thead>
    <tr>
        <th scope="col">type</th>
        <th scope="col">message</th>
        <th scope="col">value</th>
        <th scope="col">tags</th>
        <th scope="col">dataTrace</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>ERROR</td>
        <td>three</td>
        <td>three</td>
        <td>
            <table class="table mb-0">
                <thead>
                <tr>
                    <th scope="col">tag</th>
                    <th scope="col">value</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>tag</td>
                    <td>value</td>
                </tr>
                </tbody>
            </table>
        </td>
        <td>test</td>
    </tr>
    <tr>
        <td>WARNING</td>
        <td>four</td>
        <td>four</td>
        <td>
            <table class="table mb-0">
                <thead>
                <tr>
                    <th scope="col">tag</th>
                    <th scope="col">value</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>tag</td>
                    <td>value</td>
                </tr>
                </tbody>
            </table>
        </td>
        <td>test</td>
    </tr>
    </tbody>
</table>
<h2>Summary2 - Top Errors and Warnings</h2>
<table class="table table-striped">
    <thead>
    <tr>
        <th scope="col">type</th>
        <th scope="col">message</th>
        <th scope="col">value</th>
        <th scope="col">tags</th>
        <th scope="col">dataTrace</th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td>ERROR</td>
        <td>three</td>
        <td>three</td>
        <td>
            <table class="table mb-0">
                <thead>
                <tr>
                    <th scope="col">tag</th>
                    <th scope="col">value</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>tag</td>
                    <td>value</td>
                </tr>
                </tbody>
            </table>
        </td>
        <td>test</td>
    </tr>
    <tr>
        <td>WARNING</td>
        <td>four</td>
        <td>four</td>
        <td>
            <table class="table mb-0">
                <thead>
                <tr>
                    <th scope="col">tag</th>
                    <th scope="col">value</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>tag</td>
                    <td>value</td>
                </tr>
                </tbody>
            </table>
        </td>
        <td>test</td>
    </tr>
    </tbody>
</table>
</body>
</html>
            """.trimIndent()
        )
    }
}
