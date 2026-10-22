package ru.rznnike.demokmp.data.utils

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import okio.Buffer
import okio.BufferedSink
import okio.GzipSink
import okio.buffer
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val URL = "https://example.com/api"
private const val JSON_TYPE = "application/json"
private const val TEXT_TYPE = "text/plain"
private const val MAX_LOGGED_BODY_SIZE = 100 * 1024

private fun buildRequest(
    url: String = URL,
    method: String = "GET",
    body: RequestBody? = null,
    headers: Map<String, String> = emptyMap(),
): Request {
    val builder = Request.Builder()
        .url(url)
        .method(method, body)
    headers.forEach { (name, value) -> builder.header(name, value) }
    return builder.build()
}

private fun buildResponse(
    code: Int = 200,
    message: String = "OK",
    body: ResponseBody = "".toResponseBody(),
    headers: Map<String, String> = emptyMap(),
    url: String = URL,
    method: String = "GET",
    sentRequestAtMillis: Long = 0L,
    receivedResponseAtMillis: Long = 0L,
): Response {
    val builder = Response.Builder()
        .request(buildRequest(url = url, method = method))
        .protocol(Protocol.HTTP_1_1)
        .code(code)
        .message(message)
        .body(body)
        .sentRequestAtMillis(sentRequestAtMillis)
        .receivedResponseAtMillis(receivedResponseAtMillis)
    headers.forEach { (name, value) -> builder.header(name, value) }
    return builder.build()
}

@Suppress("SameParameterValue")
private fun gzip(content: String): ByteArray {
    val buffer = Buffer()
    GzipSink(buffer).buffer().use { it.writeUtf8(content) }
    return buffer.readByteArray()
}

private class OneShotRequestBody : RequestBody() {
    override fun contentType(): MediaType = TEXT_TYPE.toMediaType()
    override fun isOneShot(): Boolean = true
    override fun writeTo(sink: BufferedSink) {
        sink.writeUtf8("one-shot")
    }
}

private class DuplexRequestBody : RequestBody() {
    override fun contentType(): MediaType? = null
    override fun isDuplex(): Boolean = true
    override fun writeTo(sink: BufferedSink) = Unit
}

class HttpUtilsTest {

    // ---------------------------------------------------------------------
    // Request.toLog
    // ---------------------------------------------------------------------

    @Test
    fun `request toLog with NONE level logs only request line`() {
        // Arrange
        val request = buildRequest()

        // Act
        val result = request.toLog(HttpLogLevel.NONE)

        // Assert
        assertEquals("--> GET $URL\n", result)
    }

    @Test
    fun `request toLog with BASIC level logs request body size`() {
        // Arrange
        val request = buildRequest(
            method = "POST",
            body = "hello".toRequestBody(TEXT_TYPE.toMediaType()),
        )

        // Act
        val result = request.toLog(HttpLogLevel.BASIC)

        // Assert
        assertEquals("--> POST $URL (5-byte body)\n", result)
    }

    @Test
    fun `request toLog with HEADERS level logs request headers`() {
        // Arrange
        val request = buildRequest(
            method = "POST",
            body = """{"name":"value"}""".toRequestBody(JSON_TYPE.toMediaType()),
            headers = mapOf("X-Trace" to "abc"),
        )

        // Act
        val result = request.toLog(HttpLogLevel.HEADERS)

        // Assert
        assertContains(result, "X-Trace: abc")
    }

    @Test
    fun `request toLog with HEADERS level logs body content type and length`() {
        // Arrange
        val request = buildRequest(
            method = "POST",
            body = """{"name":"value"}""".toRequestBody(JSON_TYPE.toMediaType()),
        )

        // Act
        val result = request.toLog(HttpLogLevel.HEADERS)

        // Assert
        assertContains(result, "Content-Type: application/json; charset=utf-8\nContent-Length: 16\n")
    }

    @Test
    fun `request toLog with HEADERS level does not duplicate explicit content type`() {
        // Arrange
        val request = buildRequest(
            method = "POST",
            body = """{"name":"value"}""".toRequestBody(JSON_TYPE.toMediaType()),
            headers = mapOf("Content-Type" to JSON_TYPE),
        )

        // Act
        val result = request.toLog(HttpLogLevel.HEADERS)

        // Assert
        assertEquals(1, result.lines().count { it.startsWith("Content-Type:") })
    }

    @Test
    fun `request toLog with HEADERS level does not log body content`() {
        // Arrange
        val request = buildRequest(
            method = "POST",
            body = """{"name":"value"}""".toRequestBody(JSON_TYPE.toMediaType()),
        )

        // Act
        val result = request.toLog(HttpLogLevel.HEADERS)

        // Assert
        assertFalse(result.contains("\"name\""))
    }

    @Test
    fun `request toLog with BODY level logs prettified json body`() {
        // Arrange
        val request = buildRequest(
            method = "POST",
            body = """{"name":"value"}""".toRequestBody(JSON_TYPE.toMediaType()),
        )

        // Act
        val result = request.toLog(HttpLogLevel.BODY)

        // Assert
        assertContains(result, "\"name\": \"value\"")
    }

    @Test
    fun `request toLog with BODY level logs request end marker`() {
        // Arrange
        val request = buildRequest(
            method = "POST",
            body = """{"name":"value"}""".toRequestBody(JSON_TYPE.toMediaType()),
        )

        // Act
        val result = request.toLog(HttpLogLevel.BODY)

        // Assert
        assertContains(result, "--> END POST (16-byte body)")
    }

    @Test
    fun `request toLog with BODY level omits body with unknown encoding`() {
        // Arrange
        val request = buildRequest(
            method = "POST",
            body = "hello".toRequestBody(TEXT_TYPE.toMediaType()),
            headers = mapOf("Content-Encoding" to "br"),
        )

        // Act
        val result = request.toLog(HttpLogLevel.BODY)

        // Assert
        assertContains(result, "--> END POST (encoded body omitted)")
    }

    @Test
    fun `request toLog with BODY level omits one shot body`() {
        // Arrange
        val request = buildRequest(method = "POST", body = OneShotRequestBody())

        // Act
        val result = request.toLog(HttpLogLevel.BODY)

        // Assert
        assertContains(result, "--> END POST (one-shot body omitted)")
    }

    @Test
    fun `request toLog with BODY level omits duplex body`() {
        // Arrange
        val request = buildRequest(method = "POST", body = DuplexRequestBody())

        // Act
        val result = request.toLog(HttpLogLevel.BODY)

        // Assert
        assertContains(result, "--> END POST (duplex request body omitted)")
    }

    @Test
    fun `request toLog with BODY level omits binary body`() {
        // Arrange
        val request = buildRequest(
            method = "POST",
            body = ByteArray(4).toRequestBody(TEXT_TYPE.toMediaType()),
        )

        // Act
        val result = request.toLog(HttpLogLevel.BODY)

        // Assert
        assertContains(result, "--> END POST (binary 4-byte body omitted)")
    }

    @Test
    fun `request toLog with BODY level summarizes gzip body without content`() {
        // Arrange
        val request = buildRequest(
            method = "POST",
            body = gzip("hello").toRequestBody(TEXT_TYPE.toMediaType()),
            headers = mapOf("Content-Encoding" to "gzip"),
        )

        // Act
        val result = request.toLog(HttpLogLevel.BODY)

        // Assert
        assertTrue(Regex("""--> END POST \(5-byte, \d+-gzipped-byte body\)""").containsMatchIn(result))
    }

    @Test
    fun `request toLog with BODY level does not log gzip body content`() {
        // Arrange
        val request = buildRequest(
            method = "POST",
            body = gzip("hello").toRequestBody(TEXT_TYPE.toMediaType()),
            headers = mapOf("Content-Encoding" to "gzip"),
        )

        // Act
        val result = request.toLog(HttpLogLevel.BODY)

        // Assert
        assertFalse(result.contains("hello"))
    }

    // ---------------------------------------------------------------------
    // Response.toLog
    // ---------------------------------------------------------------------

    @Test
    fun `response toLog with BASIC level logs status line and body size`() {
        // Arrange
        val response = buildResponse(body = "hello".toResponseBody())

        // Act
        val result = response.toLog(HttpLogLevel.BASIC, bufferedBody = null)

        // Assert
        assertContains(result, "<-- 200 OK $URL (0 ms, 5-byte body)")
    }

    @Test
    fun `response toLog with HEADERS level logs response headers`() {
        // Arrange
        val response = buildResponse(headers = mapOf("X-Trace" to "abc"))

        // Act
        val result = response.toLog(HttpLogLevel.HEADERS, bufferedBody = null)

        // Assert
        assertContains(result, "X-Trace: abc")
    }

    @Test
    fun `response toLog with HEADERS level does not log body content`() {
        // Arrange
        val response = buildResponse(body = """{"a":1}""".toResponseBody(JSON_TYPE.toMediaType()))

        // Act
        val result = response.toLog(HttpLogLevel.HEADERS, bufferedBody = null)

        // Assert
        assertFalse(result.contains("\"a\""))
    }

    @Test
    fun `response toLog with BODY level logs prettified json body`() {
        // Arrange
        val response = buildResponse(body = """{"a":1}""".toResponseBody(JSON_TYPE.toMediaType()))
        val bufferedBody = Buffer().writeUtf8("""{"a":1}""")

        // Act
        val result = response.toLog(HttpLogLevel.BODY, bufferedBody = bufferedBody)

        // Assert
        assertContains(result, "\"a\": 1")
    }

    @Test
    fun `response toLog with BODY level logs response end marker with size`() {
        // Arrange
        val response = buildResponse(body = """{"a":1}""".toResponseBody(JSON_TYPE.toMediaType()))
        val bufferedBody = Buffer().writeUtf8("""{"a":1}""")

        // Act
        val result = response.toLog(HttpLogLevel.BODY, bufferedBody = bufferedBody)

        // Assert
        assertContains(result, "<-- END HTTP (0 ms, 7-byte body)")
    }

    @Test
    fun `response toLog uses explicitly provided duration`() {
        // Arrange
        val response = buildResponse(body = "hello".toResponseBody())

        // Act
        val result = response.toLog(HttpLogLevel.BASIC, bufferedBody = null, durationMs = 42L)

        // Assert
        assertContains(result, "(42 ms")
    }

    @Test
    fun `response toLog computes duration from response timestamps`() {
        // Arrange
        val response = buildResponse(
            body = "hello".toResponseBody(),
            sentRequestAtMillis = 1_000L,
            receivedResponseAtMillis = 1_500L,
        )

        // Act
        val result = response.toLog(HttpLogLevel.BASIC, bufferedBody = null)

        // Assert
        assertContains(result, "(500 ms")
    }

    @Test
    fun `response toLog with BODY level omits body when buffered body is null`() {
        // Arrange
        val response = buildResponse(body = """{"a":1}""".toResponseBody(JSON_TYPE.toMediaType()))

        // Act
        val result = response.toLog(HttpLogLevel.BODY, bufferedBody = null)

        // Assert
        assertFalse(result.contains("\"a\""))
    }

    @Test
    fun `response toLog omits body for no content response`() {
        // Arrange
        val response = buildResponse(code = 204, message = "No Content")

        // Act
        val result = response.toLog(HttpLogLevel.BODY, bufferedBody = Buffer())

        // Assert
        assertTrue(result.trimEnd().endsWith("<-- END HTTP"))
    }

    @Test
    fun `response toLog omits body for head request`() {
        // Arrange
        val response = buildResponse(method = "HEAD")

        // Act
        val result = response.toLog(HttpLogLevel.BODY, bufferedBody = Buffer())

        // Assert
        assertTrue(result.trimEnd().endsWith("<-- END HTTP"))
    }

    @Test
    fun `response toLog omits body with unknown content encoding`() {
        // Arrange
        val response = buildResponse(
            body = "hello".toResponseBody(),
            headers = mapOf("Content-Encoding" to "br"),
        )

        // Act
        val result = response.toLog(HttpLogLevel.BODY, bufferedBody = Buffer().writeUtf8("hello"))

        // Assert
        assertContains(result, "<-- END HTTP (encoded body omitted)")
    }

    @Test
    fun `response toLog omits streaming body`() {
        // Arrange
        val response = buildResponse(body = "data".toResponseBody("text/event-stream".toMediaType()))

        // Act
        val result = response.toLog(HttpLogLevel.BODY, bufferedBody = Buffer().writeUtf8("data"))

        // Assert
        assertContains(result, "<-- END HTTP (streaming)")
    }

    @Test
    fun `response toLog omits binary body`() {
        // Arrange
        val response = buildResponse(body = ByteArray(4).toResponseBody())

        // Act
        val result = response.toLog(HttpLogLevel.BODY, bufferedBody = Buffer().write(ByteArray(4)))

        // Assert
        assertContains(result, "<-- END HTTP (0 ms, binary 4-byte body omitted)")
    }

    @Test
    fun `response toLog omits too big body by content length`() {
        // Arrange
        val content = "x".repeat(MAX_LOGGED_BODY_SIZE + 1)
        val response = buildResponse(body = content.toResponseBody(TEXT_TYPE.toMediaType()))

        // Act
        val result = response.toLog(HttpLogLevel.BODY, bufferedBody = Buffer().writeUtf8(content))

        // Assert
        assertContains(result, "***Too big response body omitted***")
    }

    @Test
    fun `response toLog omits too big body by buffered size`() {
        // Arrange
        val response = buildResponse(body = "hello".toResponseBody())
        val bufferedBody = Buffer().writeUtf8("x".repeat(MAX_LOGGED_BODY_SIZE + 1))

        // Act
        val result = response.toLog(HttpLogLevel.BODY, bufferedBody = bufferedBody)

        // Assert
        assertContains(result, "***Too big response body omitted***")
    }

    // ---------------------------------------------------------------------
    // Response.getBufferedBody
    // ---------------------------------------------------------------------

    @Test
    fun `getBufferedBody returns response body content`() {
        // Arrange
        val response = buildResponse(body = "hello".toResponseBody())

        // Act
        val result = response.getBufferedBody()

        // Assert
        assertEquals("hello", result.readUtf8())
    }

    @Test
    fun `getBufferedBody decompresses gzip encoded body`() {
        // Arrange
        val response = buildResponse(
            body = gzip("hello").toResponseBody(),
            headers = mapOf("Content-Encoding" to "gzip"),
        )

        // Act
        val result = response.getBufferedBody()

        // Assert
        assertEquals("hello", result.readUtf8())
    }

    // ---------------------------------------------------------------------
    // Buffer.safeToString
    // ---------------------------------------------------------------------

    @Test
    fun `safeToString returns utf8 content`() {
        // Arrange
        val buffer = Buffer().writeUtf8("hello")

        // Act
        val result = buffer.safeToString()

        // Assert
        assertEquals("hello", result)
    }

    @Test
    fun `safeToString returns empty string for binary content`() {
        // Arrange
        val buffer = Buffer().write(ByteArray(4))

        // Act
        val result = buffer.safeToString()

        // Assert
        assertEquals("", result)
    }
}
