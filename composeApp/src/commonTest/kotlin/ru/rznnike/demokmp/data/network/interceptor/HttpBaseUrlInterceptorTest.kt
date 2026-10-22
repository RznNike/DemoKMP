package ru.rznnike.demokmp.data.network.interceptor

import com.russhwolf.settings.MapSettings
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import ru.rznnike.demokmp.data.preference.PreferencesManager
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private const val TEST_PATH = "api/test"

private fun baseUrlInterceptor(): HttpBaseUrlInterceptor =
    HttpBaseUrlInterceptor(PreferencesManager(MapSettings()))

private fun clientWithBaseUrlInterceptor(stub: StubResponseInterceptor): OkHttpClient =
    OkHttpClient.Builder()
        .addInterceptor(baseUrlInterceptor())
        .addInterceptor(stub)
        .build()

private fun buildRequest(url: String): Request = Request.Builder().url(url).build()

private fun Request.bodyAsString(): String = Buffer().also { body?.writeTo(it) }.readUtf8()

class HttpBaseUrlInterceptorTest {
    @Test
    fun `intercept replaces scheme host and port with buildkonfig base url`() {
        // Arrange
        val stub = StubResponseInterceptor()

        // Act
        clientWithBaseUrlInterceptor(stub).newCall(buildRequest("https://example.com/")).execute().close()

        // Assert
        val url = stub.lastRequest?.url
        assertEquals("https://dog.ceo/", "$url")
    }

    @Test
    fun `intercept appends request path segments to base url`() {
        // Arrange
        val stub = StubResponseInterceptor()

        // Act
        clientWithBaseUrlInterceptor(stub).newCall(buildRequest("https://example.com/api/test")).execute().close()

        // Assert
        val url = stub.lastRequest?.url
        assertEquals("https://dog.ceo/api/test", "$url")
    }

    @Test
    fun `intercept works with relative style path`() {
        // Arrange
        val stub = StubResponseInterceptor()

        // Act
        clientWithBaseUrlInterceptor(stub).newCall(buildRequest("https://x/y/z")).execute().close()

        // Assert
        val url = stub.lastRequest?.url
        assertEquals("https://dog.ceo/y/z", "$url")
    }

    @Test
    fun `intercept preserves query string`() {
        // Arrange
        val stub = StubResponseInterceptor()

        // Act
        clientWithBaseUrlInterceptor(stub).newCall(buildRequest("https://example.com/$TEST_PATH?foo=bar&baz=qux")).execute().close()

        // Assert
        val url = stub.lastRequest?.url
        assertEquals("bar", url?.queryParameter("foo"))
        assertEquals("qux", url?.queryParameter("baz"))
        assertEquals("https://dog.ceo/$TEST_PATH?foo=bar&baz=qux", "$url")
    }

    @Test
    fun `intercept handles fragment in original request`() {
        // Arrange
        val stub = StubResponseInterceptor()

        // Act
        clientWithBaseUrlInterceptor(stub).newCall(buildRequest("https://example.com/$TEST_PATH#section")).execute().close()

        // Assert
        val url = stub.lastRequest?.url
        assertEquals("https://dog.ceo/$TEST_PATH", "$url")
    }

    @Test
    fun `intercept preserves request method`() {
        // Arrange
        val stub = StubResponseInterceptor()

        // Act
        clientWithBaseUrlInterceptor(stub).newCall(buildRequest("https://example.com/$TEST_PATH")).execute().close()

        // Assert
        assertEquals("GET", stub.lastRequest?.method)
    }

    @Test
    fun `intercept preserves request headers`() {
        // Arrange
        val stub = StubResponseInterceptor()
        val request = Request.Builder()
            .url("https://example.com/$TEST_PATH")
            .addHeader("Accept", "application/json")
            .addHeader("X-Custom", "value")
            .build()

        // Act
        clientWithBaseUrlInterceptor(stub).newCall(request).execute().close()

        // Assert
        assertEquals("application/json", stub.lastRequest?.header("Accept"))
        assertEquals("value", stub.lastRequest?.header("X-Custom"))
    }

    @Test
    fun `intercept preserves request body`() {
        // Arrange
        val stub = StubResponseInterceptor()
        val request = Request.Builder()
            .url("https://example.com/$TEST_PATH")
            .post("hello".toRequestBody(null))
            .build()

        // Act
        clientWithBaseUrlInterceptor(stub).newCall(request).execute().close()

        // Assert
        assertEquals("POST", stub.lastRequest?.method)
        assertEquals("hello", stub.lastRequest?.bodyAsString())
    }

    @Test
    fun `intercept leaves original request unchanged`() {
        // Arrange
        val stub = StubResponseInterceptor()
        val request = buildRequest("https://example.com/$TEST_PATH")

        // Act
        clientWithBaseUrlInterceptor(stub).newCall(request).execute().close()

        // Assert
        assertEquals("https://example.com/$TEST_PATH", request.url.toString())
    }

    @Test
    fun `intercept proceeds exactly once per call`() {
        // Arrange
        val stub = StubResponseInterceptor()

        // Act
        clientWithBaseUrlInterceptor(stub).newCall(buildRequest("https://example.com/$TEST_PATH")).execute().close()

        // Assert
        assertEquals(1, stub.proceedCount)
    }

    @Test
    fun `intercept returns chain response`() {
        // Arrange
        val stub = StubResponseInterceptor(code = 200, body = "ok")

        // Act
        val result = clientWithBaseUrlInterceptor(stub).newCall(buildRequest("https://example.com/$TEST_PATH")).execute().use { it.code }

        // Assert
        assertEquals(200, result)
    }

    @Test
    fun `intercept propagates inner chain exception`() {
        // Arrange
        val client = OkHttpClient.Builder()
            .addInterceptor(baseUrlInterceptor())
            .addInterceptor { throw IOException("network down") }
            .build()

        // Act
        val error = assertFailsWith<IOException> {
            client.newCall(buildRequest("https://example.com/$TEST_PATH")).execute()
        }

        // Assert
        assertEquals("network down", error.message)
    }

    @Test
    fun `intercept falls back to original request on malformed base url`() {
        // Arrange
        val stub = StubResponseInterceptor()
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                var request = chain.request()
                val baseUrl = "not a url"
                baseUrl.toHttpUrlOrNull()?.let {
                    val builder = it.newBuilder()
                    for (segment in request.url.pathSegments) {
                        builder.addPathSegment(segment)
                    }
                    builder.query(request.url.query)
                    request = request.newBuilder()
                        .url(builder.build())
                        .build()
                }
                chain.proceed(request)
            }
            .addInterceptor(stub)
            .build()

        // Act
        client.newCall(buildRequest("https://example.com/$TEST_PATH")).execute().close()

        // Assert
        assertEquals("https://example.com/$TEST_PATH", stub.lastRequest?.url?.toString())
    }
}
