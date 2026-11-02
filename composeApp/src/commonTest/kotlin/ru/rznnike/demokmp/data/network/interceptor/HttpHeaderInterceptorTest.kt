package ru.rznnike.demokmp.data.network.interceptor

import com.russhwolf.settings.MapSettings
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okio.Buffer
import ru.rznnike.demokmp.data.preference.PreferencesManager
import ru.rznnike.demokmp.data.utils.DataConstants
import java.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

private const val HEADER_URL = "https://example.com/api"
private const val HEADER_VALUE = "test header content"

private fun headerInterceptor(): HttpHeaderInterceptor =
    HttpHeaderInterceptor(PreferencesManager(MapSettings()))

private fun clientWithHeaderInterceptor(stub: StubResponseInterceptor): OkHttpClient =
    OkHttpClient.Builder()
        .addInterceptor(headerInterceptor())
        .addInterceptor(stub)
        .build()

private fun buildGetRequest(): Request = Request.Builder().url(HEADER_URL).build()

private fun Request.bodyAsString(): String = Buffer().also { body?.writeTo(it) }.readUtf8()

class HttpHeaderInterceptorTest {
    @Test
    fun `intercept adds test header`() {
        // Arrange
        val stub = StubResponseInterceptor()

        // Act
        clientWithHeaderInterceptor(stub).newCall(buildGetRequest()).execute().close()

        // Assert
        assertEquals(HEADER_VALUE, stub.lastRequest?.header(DataConstants.HEADER_TEST))
    }

    @Test
    fun `intercept replaces existing header with same name`() {
        // Arrange
        val stub = StubResponseInterceptor()
        val request = Request.Builder()
            .url(HEADER_URL)
            .addHeader(DataConstants.HEADER_TEST, "stale")
            .build()

        // Act
        clientWithHeaderInterceptor(stub).newCall(request).execute().close()

        // Assert
        assertEquals(
            listOf(HEADER_VALUE),
            stub.lastRequest?.headers?.values(DataConstants.HEADER_TEST)
        )
    }

    @Test
    fun `intercept does not duplicate header when applied twice`() {
        // Arrange
        val stub = StubResponseInterceptor()
        val client = OkHttpClient.Builder()
            .addInterceptor(headerInterceptor())
            .addInterceptor(headerInterceptor())
            .addInterceptor(stub)
            .build()

        // Act
        client.newCall(buildGetRequest()).execute().close()

        // Assert
        assertEquals(
            listOf(HEADER_VALUE),
            stub.lastRequest?.headers?.values(DataConstants.HEADER_TEST)
        )
    }

    @Test
    fun `intercept preserves unrelated headers`() {
        // Arrange
        val stub = StubResponseInterceptor()
        val request = Request.Builder()
            .url(HEADER_URL)
            .addHeader("Accept", "application/json")
            .build()

        // Act
        clientWithHeaderInterceptor(stub).newCall(request).execute().close()

        // Assert
        assertEquals("application/json", stub.lastRequest?.header("Accept"))
    }

    @Test
    fun `intercept preserves request url`() {
        // Arrange
        val stub = StubResponseInterceptor()

        // Act
        clientWithHeaderInterceptor(stub).newCall(buildGetRequest()).execute().close()

        // Assert
        assertEquals(HEADER_URL, stub.lastRequest?.url?.toString())
    }

    @Test
    fun `intercept preserves request method`() {
        // Arrange
        val stub = StubResponseInterceptor()

        // Act
        clientWithHeaderInterceptor(stub).newCall(buildGetRequest()).execute().close()

        // Assert
        assertEquals("GET", stub.lastRequest?.method)
    }

    @Test
    fun `intercept preserves request body`() {
        // Arrange
        val stub = StubResponseInterceptor()
        val request = Request.Builder()
            .url(HEADER_URL)
            .post("payload".toRequestBody(null))
            .build()

        // Act
        clientWithHeaderInterceptor(stub).newCall(request).execute().close()

        // Assert
        assertEquals("payload", stub.lastRequest?.bodyAsString())
    }

    @Test
    fun `intercept leaves original request unchanged`() {
        // Arrange
        val stub = StubResponseInterceptor()
        val request = buildGetRequest()

        // Act
        clientWithHeaderInterceptor(stub).newCall(request).execute().close()

        // Assert
        assertNull(request.header(DataConstants.HEADER_TEST))
    }

    @Test
    fun `intercept returns chain response`() {
        // Arrange
        val stub = StubResponseInterceptor(code = 201, body = "created")

        // Act
        val result = clientWithHeaderInterceptor(stub).newCall(buildGetRequest()).execute().use { it.code }

        // Assert
        assertEquals(201, result)
    }

    @Test
    fun `intercept proceeds exactly once per call`() {
        // Arrange
        val stub = StubResponseInterceptor()

        // Act
        clientWithHeaderInterceptor(stub).newCall(buildGetRequest()).execute().close()

        // Assert
        assertEquals(1, stub.proceedCount)
    }

    @Test
    fun `intercept propagates inner chain exception`() {
        // Arrange
        val client = OkHttpClient.Builder()
            .addInterceptor(headerInterceptor())
            .addInterceptor { throw IOException("network down") }
            .build()

        // Act
        val error = assertFailsWith<IOException> {
            client.newCall(buildGetRequest()).execute()
        }

        // Assert
        assertEquals("network down", error.message)
    }
}
