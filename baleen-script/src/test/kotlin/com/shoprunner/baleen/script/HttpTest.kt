package com.shoprunner.baleen.script

import com.shoprunner.baleen.Baleen.describeAs
import com.shoprunner.baleen.printer.TextPrinter
import com.shoprunner.baleen.types.IntegerType
import com.shoprunner.baleen.types.StringType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockserver.client.MockServerClient
import org.mockserver.junit.jupiter.MockServerExtension
import org.mockserver.model.HttpRequest
import org.mockserver.model.HttpResponse
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockServerExtension::class)
internal class HttpTest {

    @Test
    fun `test POST`(mockServer: MockServerClient) {
        val url = "http://localhost:${mockServer.port}/test"

        // setup an API route with response
        mockServer.`when`(
            HttpRequest.request()
                .withMethod("POST")
                .withPath("/test")
        ).respond(
            HttpResponse.response()
                .withStatusCode(200)
                .withBody(
                    """
                    {
                      "id": 1,
                      "firstName": "Jon",
                      "lastName": "Smith"
                    }
                    """.trimIndent()
                )
        )

        val outputFile = File.createTempFile("post-test", ".txt")

        val desc = "person".describeAs {
            "id".type(IntegerType(), required = true)
            "firstName".type(StringType(0, 32), required = true)
            "lastName".type(StringType(0, 32), required = true)
        }

        outputFile.writer().use {
            validate(
                description = desc,
                data = http(
                    url = url,
                    method = Method.POST,
                    contentType = "application/json",
                    requestBody = "testbody",
                    data = json()
                ),
                printers = arrayOf(TextPrinter(it, prettyPrint = true)),
            )
        }

        val output = outputFile.readText()

        assertThat(output).isEqualToIgnoringWhitespace(
            """
            ValidationSummary(
              dataTrace=DataTrace(stack=[], tags={}),
              summary=Summary,
              numSuccesses=1,
              numInfos=3,
              numErrors=0,
              numWarnings=0,
              topErrorsAndWarnings=[
              ]
            )
            """.trimIndent()
        )
    }

    @Test
    fun `test GET`(mockServer: MockServerClient) {
        val url = "http://localhost:${mockServer.port}/test"

        // setup an API route with response
        mockServer.`when`(
            HttpRequest.request()
                .withMethod("GET")
                .withPath("/test")
        ).respond(
            HttpResponse.response()
                .withStatusCode(200)
                .withBody(
                    """
                    {
                      "id": 1,
                      "firstName": "Jon",
                      "lastName": "Smith"
                    }
                    """.trimIndent()
                )
        )

        val outputFile = File.createTempFile("get-test", ".txt")

        val desc = "person".describeAs {
            "id".type(IntegerType(), required = true)
            "firstName".type(StringType(0, 32), required = true)
            "lastName".type(StringType(0, 32), required = true)
        }

        outputFile.writer().use {
            validate(
                description = desc,
                data = http(
                    url = url,
                    method = Method.GET,
                    contentType = "application/json",
                    data = json()
                ),
                printers = arrayOf(TextPrinter(it, prettyPrint = true)),
            )
        }

        val output = outputFile.readText()

        assertThat(output).isEqualToIgnoringWhitespace(
            """
            ValidationSummary(
              dataTrace=DataTrace(stack=[], tags={}),
              summary=Summary,
              numSuccesses=1,
              numInfos=3,
              numErrors=0,
              numWarnings=0,
              topErrorsAndWarnings=[
              ]
            )
            """.trimIndent()
        )
    }

    @Test
    fun `test PUT`(mockServer: MockServerClient) {
        val url = "http://localhost:${mockServer.port}/test"

        // setup an API route with response
        mockServer.`when`(
            HttpRequest.request()
                .withMethod("PUT")
                .withPath("/test")
        ).respond(
            HttpResponse.response()
                .withStatusCode(200)
                .withBody(
                    """
                    {
                      "id": 1,
                      "firstName": "Jon",
                      "lastName": "Smith"
                    }
                    """.trimIndent()
                )
        )

        val outputFile = File.createTempFile("put-test", ".txt")

        val desc = "person".describeAs {
            "id".type(IntegerType(), required = true)
            "firstName".type(StringType(0, 32), required = true)
            "lastName".type(StringType(0, 32), required = true)
        }

        outputFile.writer().use {
            validate(
                description = desc,
                data = http(
                    url = url,
                    method = Method.PUT,
                    contentType = "application/json",
                    requestBody = "testbody",
                    data = json()
                ),
                printers = arrayOf(TextPrinter(it, prettyPrint = true)),
            )
        }

        val output = outputFile.readText()

        assertThat(output).isEqualToIgnoringWhitespace(
            """
            ValidationSummary(
              dataTrace=DataTrace(stack=[], tags={}),
              summary=Summary,
              numSuccesses=1,
              numInfos=3,
              numErrors=0,
              numWarnings=0,
              topErrorsAndWarnings=[
              ]
            )
            """.trimIndent()
        )
    }

    @Test
    fun `test DELETE`(mockServer: MockServerClient) {
        val url = "http://localhost:${mockServer.port}/test"

        // setup an API route with response
        mockServer.`when`(
            HttpRequest.request()
                .withMethod("DELETE")
                .withPath("/test")
        ).respond(
            HttpResponse.response()
                .withStatusCode(200)
                .withBody(
                    """
                    {
                      "id": 1,
                      "firstName": "Jon",
                      "lastName": "Smith"
                    }
                    """.trimIndent()
                )
        )

        val outputFile = File.createTempFile("delete-test", ".txt")

        val desc = "person".describeAs {
            "id".type(IntegerType(), required = true)
            "firstName".type(StringType(0, 32), required = true)
            "lastName".type(StringType(0, 32), required = true)
        }

        outputFile.writer().use {
            validate(
                description = desc,
                data = http(
                    url = url,
                    method = Method.DELETE,
                    contentType = "application/json",
                    data = json()
                ),
                printers = arrayOf(TextPrinter(it, prettyPrint = true)),
            )
        }

        val output = outputFile.readText()

        assertThat(output).isEqualToIgnoringWhitespace(
            """
            ValidationSummary(
              dataTrace=DataTrace(stack=[], tags={}),
              summary=Summary,
              numSuccesses=1,
              numInfos=3,
              numErrors=0,
              numWarnings=0,
              topErrorsAndWarnings=[
              ]
            )
            """.trimIndent()
        )
    }
}
