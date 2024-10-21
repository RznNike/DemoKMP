package ru.rznnike.demokmp.data.network.interceptor

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import ru.rznnike.demokmp.domain.utils.logger
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalSerializationApi::class)
class HttpLoggingInterceptor(
    private val level: Level = Level.BODY
) : Interceptor {
    private val formatter = Json {
        prettyPrint = true
        prettyPrintIndent = "    "
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val logBuilder = StringBuilder()

        val request = chain.request()
        if (level == Level.NONE) {
            return chain.proceed(request)
        }

        val logBody = level == Level.BODY
        val logHeaders = logBody || level == Level.HEADERS

        val requestBody = request.body

        val connection = chain.connection()
        var requestStartMessage =
            ("--> ${request.method} ${request.url}${if (connection != null) " " + connection.protocol() else ""}")
        if (!logHeaders && requestBody != null) {
            requestStartMessage += " (${requestBody.contentLength()}-byte body)"
        }
        logBuilder.appendLine(requestStartMessage)

        if (logHeaders) {
            val headers = request.headers

            if (requestBody != null) {
                // Request body headers are only present when installed as a network interceptor. When not
                // already present, force them to be included (if available) so their values are known.
                requestBody.contentType()?.let {
                    if (headers["Content-Type"] == null) {
                        logBuilder.appendLine("Content-Type: $it")
                    }
                }
                if (requestBody.contentLength() != -1L) {
                    if (headers["Content-Length"] == null) {
                        logBuilder.appendLine("Content-Length: ${requestBody.contentLength()}")
                    }
                }
            }

            for (i in 0 until headers.size) {
                logHeader(logBuilder, headers, i)
            }

            if (!logBody || requestBody == null) {
                logBuilder.appendLine("--> END ${request.method}")
            } else if (bodyHasUnknownEncoding(request.headers)) {
                logBuilder.appendLine("--> END ${request.method} (encoded body omitted)")
            } else if (requestBody.isDuplex()) {
                logBuilder.appendLine("--> END ${request.method} (duplex request body omitted)")
            } else if (requestBody.isOneShot()) {
                logBuilder.appendLine("--> END ${request.method} (one-shot body omitted)")
            } else {
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
                if (!buffer.isProbablyUtf8()) {
                    logBuilder.appendLine(
                        "--> END ${request.method} (binary ${requestBody.contentLength()}-byte body omitted)",
                    )
                } else if (gzippedLength != null) {
                    logBuilder.appendLine("--> END ${request.method} (${buffer.size}-byte, $gzippedLength-gzipped-byte body)")
                } else {
                    val body = buffer.readString(charset)
                    val formattedBody = try {
                        formatter.encodeToString(formatter.parseToJsonElement(body))
                    } catch (_: Exception) {
                        body
                    }
                    logBuilder.appendLine(formattedBody)

                    logBuilder.appendLine("--> END ${request.method} (${requestBody.contentLength()}-byte body)")
                }
            }
        }
        logger(logBuilder.toString())
        logBuilder.clear()

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            logBuilder.appendLine("<-- HTTP FAILED: $e")
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body!!
        val contentLength = responseBody.contentLength()
        val bodySize = if (contentLength != -1L) "$contentLength-byte" else "unknown-length"
        logBuilder.append("<-- ${response.code}")
        if (response.message.isNotEmpty()) logBuilder.append(" ${response.message}")
        logBuilder.append(" ${response.request.url} (${tookMs}ms")
        if (!logHeaders) logBuilder.append(", $bodySize body")
        logBuilder.appendLine(")")

        if (logHeaders) {
            val headers = response.headers
            for (i in 0 until headers.size) {
                logHeader(logBuilder, headers, i)
            }

            if (!logBody || !response.promisesBody()) {
                logBuilder.appendLine("<-- END HTTP")
            } else if (bodyHasUnknownEncoding(response.headers)) {
                logBuilder.appendLine("<-- END HTTP (encoded body omitted)")
            } else if (bodyIsStreaming(response)) {
                logBuilder.appendLine("<-- END HTTP (streaming)")
            } else {
                val source = responseBody.source()
                source.request(Long.MAX_VALUE) // Buffer the entire body.

                val totalMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

                var buffer = source.buffer

                var gzippedLength: Long? = null
                if ("gzip".equals(headers["Content-Encoding"], ignoreCase = true)) {
                    gzippedLength = buffer.size
                    GzipSource(buffer.clone()).use { gzippedResponseBody ->
                        buffer = Buffer()
                        buffer.writeAll(gzippedResponseBody)
                    }
                }

                val charset: Charset = responseBody.contentType().charsetOrUtf8()

                if (!buffer.isProbablyUtf8()) {
                    logBuilder.appendLine()
                    logBuilder.appendLine("<-- END HTTP (${totalMs}ms, binary ${buffer.size}-byte body omitted)")
                    return response
                }

                if (contentLength != 0L) {
                    logBuilder.appendLine()
                    val body = buffer.clone().readString(charset)
                    val formattedBody = try {
                        formatter.encodeToString(formatter.parseToJsonElement(body))
                    } catch (_: Exception) {
                        body
                    }
                    logBuilder.appendLine(formattedBody)
                }

                logBuilder.append("<-- END HTTP (${totalMs}ms, ${buffer.size}-byte")
                if (gzippedLength != null) logBuilder.append(", $gzippedLength-gzipped-byte")
                logBuilder.appendLine(" body)")
            }
        }
        logger(logBuilder.toString())

        return response
    }

    private fun logHeader(
        logBuilder: StringBuilder,
        headers: Headers,
        i: Int,
    ) {
        logBuilder.appendLine(headers.name(i) + ": " + headers.value(i))
    }

    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }

    private fun bodyIsStreaming(response: Response): Boolean {
        val contentType = response.body?.contentType()
        return contentType != null && contentType.type == "text" && contentType.subtype == "event-stream"
    }

    enum class Level {
        NONE,
        BASIC,
        HEADERS,
        BODY
    }
}

fun Buffer.isProbablyUtf8(): Boolean {
    try {
        val prefix = Buffer()
        val byteCount = size.coerceAtMost(64)
        copyTo(prefix, 0, byteCount)
        for (i in 0 until 16) {
            if (prefix.exhausted()) {
                break
            }
            val codePoint = prefix.readUtf8CodePoint()
            if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                return false
            }
        }
        return true
    } catch (_: EOFException) {
        return false // Truncated UTF-8 sequence.
    }
}

internal fun MediaType?.charsetOrUtf8(): Charset {
    return this?.charset() ?: Charsets.UTF_8
}
