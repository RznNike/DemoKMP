package ru.rznnike.demokmp.data.utils

import okhttp3.Headers
import okhttp3.MediaType
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.BufferedSource
import okio.GzipSource
import ru.rznnike.demokmp.data.utils.json.prettifyJson
import java.io.EOFException
import java.nio.charset.Charset

private const val MAX_RESPONSE_BODY_SIZE_TO_LOG = 100 * 1024 // 100 KB

fun Request.toLog(level: HttpLogLevel): String {
    val logBuilder = StringBuilder()
    val logBody = level == HttpLogLevel.BODY
    val logHeaders = logBody || level == HttpLogLevel.HEADERS

    val requestBody = body

    var requestStartMessage =
        ("--> $method $url")
    if ((!logHeaders) && (requestBody != null)) {
        requestStartMessage += " (${requestBody.contentLength()}-byte body)"
    }
    logBuilder.appendLine(requestStartMessage)

    if (logHeaders) {
        if (requestBody != null) {
            requestBody.contentType()?.let {
                if (headers["Content-Type"] == null) {
                    logBuilder.appendLine("Content-Type: $it")
                }
            }
            if ((requestBody.contentLength() != -1L) && (headers["Content-Length"] == null)) {
                logBuilder.appendLine("Content-Length: ${requestBody.contentLength()}")
            }
        }

        headers.forEach { (name, value) ->
            logBuilder.appendLine("$name: $value")
        }

        when {
            (!logBody) || (requestBody == null) -> logBuilder.appendLine("--> END $method")
            bodyHasUnknownEncoding(headers) -> logBuilder.appendLine("--> END $method (encoded body omitted)")
            requestBody.isDuplex() -> logBuilder.appendLine("--> END $method (duplex request body omitted)")
            requestBody.isOneShot() -> logBuilder.appendLine("--> END $method (one-shot body omitted)")
            else -> {
                var buffer = Buffer()
                requestBody.writeTo(buffer)

                var gzippedLength: Long? = null
                if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                    gzippedLength = buffer.size
                    GzipSource(buffer).use { gzippedResponseBody ->
                        buffer = Buffer()
                        buffer.writeAll(gzippedResponseBody)
                    }
                }

                val charset: Charset = requestBody.contentType().charsetOrUtf8()

                logBuilder.appendLine()
                when {
                    !buffer.isProbablyUtf8() -> {
                        logBuilder.appendLine(
                            "--> END $method (binary ${requestBody.contentLength()}-byte body omitted)",
                        )
                    }
                    gzippedLength != null -> {
                        logBuilder.appendLine("--> END $method (${buffer.size}-byte, $gzippedLength-gzipped-byte body)")
                    }
                    else -> {
                        logBuilder.appendLine(buffer.readString(charset).prettifyJson())
                        logBuilder.appendLine("--> END $method (${requestBody.contentLength()}-byte body)")
                    }
                }
            }
        }
    }

    return logBuilder.toString()
}

fun Response.toLog(level: HttpLogLevel, bufferedBody: Buffer?, durationMs: Long? = null): String {
    val finalDurationMs = durationMs ?: (receivedResponseAtMillis - sentRequestAtMillis)
    val logBuilder = StringBuilder()
    val logBody = level == HttpLogLevel.BODY
    val logHeaders = logBody || level == HttpLogLevel.HEADERS

    val contentLength = body.contentLength()
    val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
    logBuilder.append("<-- $code")
    if (message.isNotEmpty()) logBuilder.append(" $message")
    logBuilder.append(" ${request.url} ($finalDurationMs ms")
    if (!logHeaders) logBuilder.append(", $bodySize body")
    logBuilder.appendLine(")")

    if (logHeaders) {
        val headers = headers
        headers.forEach { (name, value) ->
            logBuilder.appendLine("$name: $value")
        }

        when {
            (!logBody) || (!promisesBody() || (bufferedBody == null)) -> logBuilder.appendLine("<-- END HTTP")
            bodyHasUnknownEncoding(headers) -> logBuilder.appendLine("<-- END HTTP (encoded body omitted)")
            bodyIsStreaming(body.contentType()) -> logBuilder.appendLine("<-- END HTTP (streaming)")
            else -> {
                if (!bufferedBody.isProbablyUtf8()) {
                    logBuilder.appendLine()
                    logBuilder.appendLine("<-- END HTTP ($finalDurationMs ms, binary ${bufferedBody.size}-byte body omitted)")
                    return logBuilder.toString()
                }

                logBuilder.appendLine()
                when {
                    contentLength > MAX_RESPONSE_BODY_SIZE_TO_LOG -> {
                        logBuilder.appendLine("***Too big response body omitted***")
                    }
                    contentLength != 0L -> {
                        val charset: Charset = body.contentType().charsetOrUtf8()
                        val body = bufferedBody.clone().readString(charset)
                        if (bufferedBody.size > MAX_RESPONSE_BODY_SIZE_TO_LOG) {
                            logBuilder.appendLine("***Too big response body omitted***")
                        } else {
                            logBuilder.appendLine(body.prettifyJson())
                        }
                    }
                }

                logBuilder.append("<-- END HTTP ($finalDurationMs ms, ${bufferedBody.size}-byte")
                logBuilder.appendLine(" body)")
            }
        }
    }

    return logBuilder.toString()
}

fun Response.getBufferedBody(): Buffer {
    val source = body.source()
    source.request(Long.MAX_VALUE) // Buffer the entire body
    var buffer = source.buffer
    if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
        GzipSource(buffer.clone()).use { gzippedResponseBody ->
            buffer = Buffer()
            buffer.writeAll(gzippedResponseBody)
        }
    }
    return buffer
}

fun Buffer.safeToString(): String = if (buffer.isProbablyUtf8()) {
    clone().readString(Charsets.UTF_8)
} else ""

private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
    val contentEncoding = headers["Content-Encoding"] ?: return false
    return (!contentEncoding.equals("identity", ignoreCase = true))
            && (!contentEncoding.equals("gzip", ignoreCase = true))
}

private fun bodyIsStreaming(contentType: MediaType?): Boolean =
    contentType != null && contentType.type == "text" && contentType.subtype == "event-stream"

private fun BufferedSource.isProbablyUtf8(): Boolean = try {
    val peek = peek()
    for (i in 0 until 16) {
        if (peek.exhausted()) {
            break
        }
        val codePoint = peek.readUtf8CodePoint()
        if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
            return false
        }
    }
    return true
} catch (_: EOFException) {
    return false // Truncated UTF-8 sequence.
}

private fun MediaType?.charsetOrUtf8() = this?.charset() ?: Charsets.UTF_8

enum class HttpLogLevel {
    NONE,
    BASIC,
    HEADERS,
    BODY
}