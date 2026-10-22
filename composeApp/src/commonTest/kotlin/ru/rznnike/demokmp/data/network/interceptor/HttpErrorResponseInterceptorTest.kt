package ru.rznnike.demokmp.data.network.interceptor

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import ru.rznnike.demokmp.data.network.error.CustomServerException
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private const val URL = "https://example.com/api"

/**
 * Terminal interceptor that returns a canned response without touching the network.
 *
 * It is registered after [HttpErrorResponseInterceptor], so OkHttp's redirect, retry and
 * authentication machinery (which sits further down the chain) never runs.
 */
private class StubResponseInterceptor(
    var code: Int = 200,
    var body: String = "",
    var headers: Map<String, String> = emptyMap(),
) : Interceptor {
    var proceedCount: Int = 0
        private set

    var lastRequest: Request? = null
        private set

    override fun intercept(chain: Interceptor.Chain): Response {
        proceedCount++
        val request = chain.request()
        lastRequest = request
        val builder = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message("stub")
            .body(body.toResponseBody(null))
        headers.forEach { (name, value) -> builder.header(name, value) }
        return builder.build()
    }
}

private fun clientWith(stub: StubResponseInterceptor): OkHttpClient =
    OkHttpClient.Builder()
        .addInterceptor(HttpErrorResponseInterceptor())
        .addInterceptor(stub)
        .build()

private fun buildRequest(): Request = Request.Builder().url(URL).build()

class HttpErrorResponseInterceptorTest {
    @Test
    fun `intercept returns response for successful status code`() {
        // Arrange
        val client = clientWith(StubResponseInterceptor(code = 200))

        // Act
        val result = client.newCall(buildRequest()).execute().use { it.code }

        // Assert
        assertEquals(200, result)
    }

    @Test
    fun `intercept returns response body for successful status code`() {
        // Arrange
        val client = clientWith(StubResponseInterceptor(code = 200, body = "ok"))

        // Act
        val result = client.newCall(buildRequest()).execute().use { it.body.string() }

        // Assert
        assertEquals("ok", result)
    }

    @Test
    fun `intercept returns response for no content status code`() {
        // Arrange
        val client = clientWith(StubResponseInterceptor(code = 204))

        // Act
        val result = client.newCall(buildRequest()).execute().use { it.code }

        // Assert
        assertEquals(204, result)
    }

    @Test
    fun `intercept returns response at upper success boundary`() {
        // Arrange
        val client = clientWith(StubResponseInterceptor(code = 299))

        // Act
        val result = client.newCall(buildRequest()).execute().use { it.code }

        // Assert
        assertEquals(299, result)
    }

    @Test
    fun `intercept preserves response headers on success`() {
        // Arrange
        val client = clientWith(
            StubResponseInterceptor(code = 200, headers = mapOf("X-Test" to "1")),
        )

        // Act
        val result = client.newCall(buildRequest()).execute().use { it.header("X-Test") }

        // Assert
        assertEquals("1", result)
    }

    @Test
    fun `intercept throws at code just below success range`() {
        // Arrange
        val client = clientWith(StubResponseInterceptor(code = 199))

        // Act
        val error = assertFailsWith<CustomServerException> {
            client.newCall(buildRequest()).execute()
        }

        // Assert
        assertEquals(199, error.httpCode)
    }

    @Test
    fun `intercept throws at code just above success range`() {
        // Arrange
        val client = clientWith(StubResponseInterceptor(code = 300))

        // Act
        val error = assertFailsWith<CustomServerException> {
            client.newCall(buildRequest()).execute()
        }

        // Assert
        assertEquals(300, error.httpCode)
    }

    @Test
    fun `intercept throws with response code for client error`() {
        // Arrange
        val client = clientWith(StubResponseInterceptor(code = 404))

        // Act
        val error = assertFailsWith<CustomServerException> {
            client.newCall(buildRequest()).execute()
        }

        // Assert
        assertEquals(404, error.httpCode)
    }

    @Test
    fun `intercept throws with response code for server error`() {
        // Arrange
        val client = clientWith(StubResponseInterceptor(code = 500))

        // Act
        val error = assertFailsWith<CustomServerException> {
            client.newCall(buildRequest()).execute()
        }

        // Assert
        assertEquals(500, error.httpCode)
    }

    @Test
    fun `intercept marks server error as server side`() {
        // Arrange
        val client = clientWith(StubResponseInterceptor(code = 503))

        // Act
        val error = assertFailsWith<CustomServerException> {
            client.newCall(buildRequest()).execute()
        }

        // Assert
        assertEquals(true, error.isServerSide)
    }

    @Test
    fun `intercept marks client error as not server side`() {
        // Arrange
        val client = clientWith(StubResponseInterceptor(code = 422))

        // Act
        val error = assertFailsWith<CustomServerException> {
            client.newCall(buildRequest()).execute()
        }

        // Assert
        assertEquals(false, error.isServerSide)
    }

    @Test
    fun `intercept maps any non successful status code`() {
        // Arrange
        val stub = StubResponseInterceptor()
        val client = clientWith(stub)
        val codes = listOf(300, 400, 500)

        // Act + Assert
        codes.forEach { code ->
            stub.code = code
            val error = assertFailsWith<CustomServerException>("code=$code") {
                client.newCall(buildRequest()).execute()
            }
            assertEquals(code, error.httpCode, "code=$code")
        }
    }

    @Test
    fun `intercept proceeds exactly once per call`() {
        // Arrange
        val stub = StubResponseInterceptor(code = 200)
        val client = clientWith(stub)

        // Act
        client.newCall(buildRequest()).execute().use { }

        // Assert
        assertEquals(1, stub.proceedCount)
    }

    @Test
    fun `intercept proceeds with the original request`() {
        // Arrange
        val stub = StubResponseInterceptor(code = 200)
        val client = clientWith(stub)

        // Act
        client.newCall(buildRequest()).execute().use { }

        // Assert
        assertEquals(URL, stub.lastRequest?.url?.toString())
        assertEquals("GET", stub.lastRequest?.method)
    }

    @Test
    fun `intercept propagates inner chain exception`() {
        // Arrange
        val client = OkHttpClient.Builder()
            .addInterceptor(HttpErrorResponseInterceptor())
            .addInterceptor { throw IOException("network down") }
            .build()

        // Act
        val error = assertFailsWith<IOException> {
            client.newCall(buildRequest()).execute()
        }

        // Assert
        assertEquals("network down", error.message)
    }
}
