package ru.rznnike.demokmp.data.network.interceptor

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.Response
import okhttp3.internal.http.promisesBody
import okio.Buffer
import okio.GzipSource
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.domain.log.Logger
import ru.rznnike.demokmp.domain.log.NetworkRequestState
import java.io.EOFException
import java.io.IOException
import java.net.SocketException
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

private const val MAX_RESPONSE_BODY_SIZE_TO_LOG = 100 * 1024 // 100 KB

@OptIn(ExperimentalSerializationApi::class)
class HttpLoggingInterceptor : Interceptor {
    private val formatter = Json {
        prettyPrint = true
        prettyPrintIndent = "    "
    }

    @Suppress("KotlinConstantConditions")
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val level = if (BuildKonfig.DEBUG) Level.BODY else Level.BASIC
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
                    var body = buffer.readString(charset)

                    if (request.url.toString().endsWith("authorization/sign-in")) {
                        body = body.replace("\"password\" *: *\".*\"".toRegex(), "\"password\":\"***\"")
                    }

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
        val logRequestUuid = Logger.networkRequest(logBuilder.toString())
        logBuilder.clear()

        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            val state = when {
                (e is IOException) && (e.message == "Canceled") -> NetworkRequestState.TIMEOUT
                (e is SocketException) && (e.message == "Socket closed") -> NetworkRequestState.CANCELLED
                else -> NetworkRequestState.ERROR
            }
            Logger.networkResponse(
                requestUuid = logRequestUuid,
                message = "<-- HTTP FAILED: $e",
                state = state
            )
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

                if (!buffer.isProbablyUtf8()) {
                    logBuilder.appendLine()
                    logBuilder.appendLine("<-- END HTTP (${totalMs}ms, binary ${buffer.size}-byte body omitted)")
                    return response
                }

                when {
                    contentLength > MAX_RESPONSE_BODY_SIZE_TO_LOG -> {
                        logBuilder.appendLine()
                        logBuilder.appendLine("***Too big response body omitted***")
                    }
                    contentLength != 0L -> {
                        logBuilder.appendLine()
                        val charset: Charset = responseBody.contentType().charsetOrUtf8()
                        val body = buffer.clone().readString(charset)
                        val formattedBody = try {
                            formatter.encodeToString(formatter.parseToJsonElement(body))
                        } catch (_: Exception) {
                            body
                        }
                        logBuilder.appendLine(formattedBody)
                    }
                }

                logBuilder.append("<-- END HTTP (${totalMs}ms, ${buffer.size}-byte")
                if (gzippedLength != null) logBuilder.append(", $gzippedLength-gzipped-byte")
                logBuilder.appendLine(" body)")
            }
        }
        Logger.networkResponse(
            requestUuid = logRequestUuid,
            message = logBuilder.toString(),
            state = if (response.isSuccessful) NetworkRequestState.SUCCESS else NetworkRequestState.ERROR
        )

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
