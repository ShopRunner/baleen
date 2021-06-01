package com.shoprunner.baleen.script

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
import java.nio.file.Files.createTempDirectory

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

        val outDir = createTempDirectory("post-test").toFile()

        baleen(outDir, Output.console, Output.text) {
            http {
                post(url, "application/json", "testbody") { data ->
                    json("post", data!!.byteInputStream()) {
                        "id".type(IntegerType(), required = true)
                        "firstName".type(StringType(0, 32), required = true)
                        "lastName".type(StringType(0, 32), required = true)
                    }
                }
            }
        }

        val output = File(outDir, "summary.txt").readText()

        assertThat(output).isEqualToIgnoringWhitespace(
            """
            ValidationSummary(
              dataTrace=DataTrace(stack=[], tags={file=post}),
              summary=Summary for file=post,
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

        val outDir = createTempDirectory("get-test").toFile()

        baleen(outDir, Output.console, Output.text) {
            http {
                get(url, "application/json") { data ->
                    json("get", data!!.byteInputStream()) {
                        "id".type(IntegerType(), required = true)
                        "firstName".type(StringType(0, 32), required = true)
                        "lastName".type(StringType(0, 32), required = true)
                    }
                }
            }
        }

        val output = File(outDir, "summary.txt").readText()

        assertThat(output).isEqualToIgnoringWhitespace(
            """
            ValidationSummary(
              dataTrace=DataTrace(stack=[], tags={file=get}),
              summary=Summary for file=get,
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

        val outDir = createTempDirectory("put-test").toFile()

        baleen(outDir, Output.console, Output.text) {
            http {
                put(url, "application/json", "testbody") { data ->
                    json("put", data!!.byteInputStream()) {
                        "id".type(IntegerType(), required = true)
                        "firstName".type(StringType(0, 32), required = true)
                        "lastName".type(StringType(0, 32), required = true)
                    }
                }
            }
        }

        val output = File(outDir, "summary.txt").readText()

        assertThat(output).isEqualToIgnoringWhitespace(
            """
            ValidationSummary(
              dataTrace=DataTrace(stack=[], tags={file=put}),
              summary=Summary for file=put,
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

        val outDir = createTempDirectory("delete-test").toFile()

        baleen(outDir, Output.console, Output.text) {
            http {
                delete(url, "application/json") { data ->
                    json("delete", data!!.byteInputStream()) {
                        "id".type(IntegerType(), required = true)
                        "firstName".type(StringType(0, 32), required = true)
                        "lastName".type(StringType(0, 32), required = true)
                    }
                }
            }
        }

        val output = File(outDir, "summary.txt").readText()

        assertThat(output).isEqualToIgnoringWhitespace(
            """
            ValidationSummary(
              dataTrace=DataTrace(stack=[], tags={file=delete}),
              summary=Summary for file=delete,
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
