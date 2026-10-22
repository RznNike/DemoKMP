package ru.rznnike.demokmp.data.network.interceptor

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.Request
import ru.rznnike.demokmp.data.utils.HttpLogLevel
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.Logger
import ru.rznnike.demokmp.domain.log.NetworkRequestState
import ru.rznnike.demokmp.domain.log.extension.LoggerExtension
import java.io.IOException
import java.net.SocketException
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val LOG_URL = "https://example.com/api"
private const val RESPONSE_BODY = "boom"

private val fixedClock = Clock.fixed(Instant.ofEpochMilli(1_700_000_000_000L), ZoneOffset.UTC)

private data class RecordedNetworkRequest(
    val uuid: UUID,
    val message: String,
    val curl: String
)

private data class RecordedNetworkResponse(
    val requestUuid: UUID,
    val message: String,
    val state: NetworkRequestState
)

/**
 * Captures the network log entries the logger hands over to an extension.
 */
private class RecordingLoggerExtension : LoggerExtension(isDebugEnabled = true) {
    val networkRequests = mutableListOf<RecordedNetworkRequest>()
    val networkResponses = mutableListOf<RecordedNetworkResponse>()

    override suspend fun addMessage(
        tag: String,
        message: String,
        level: LogLevel,
        type: LogType,
        callback: suspend (LogMessage) -> Unit
    ) = Unit

    override suspend fun networkRequest(tag: String, uuid: UUID, message: String, curl: String) {
        networkRequests += RecordedNetworkRequest(uuid = uuid, message = message, curl = curl)
    }

    override suspend fun networkResponse(
        tag: String,
        requestUuid: UUID,
        message: String,
        state: NetworkRequestState
    ) {
        networkResponses += RecordedNetworkResponse(
            requestUuid = requestUuid,
            message = message,
            state = state
        )
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class HttpLoggingInterceptorTest {
    private fun TestScope.initLogger(extension: RecordingLoggerExtension) {
        Logger.init(
            clock = fixedClock,
            coroutineDispatcher = UnconfinedTestDispatcher(testScheduler),
            extensions = listOf(extension)
        )
    }

    private fun clientWith(
        interceptor: HttpLoggingInterceptor,
        stub: StubResponseInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(interceptor)
        .addInterceptor(stub)
        .build()

    private fun clientWithFailing(interceptor: HttpLoggingInterceptor, failure: Exception): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .addInterceptor { throw failure }
            .build()

    private fun buildRequest(): Request = Request.Builder().url(LOG_URL).build()

    @AfterTest
    fun tearDown() {
        // the logger is a global singleton: leave it uninitialized for the other test classes
        Logger.reset()
    }

    @Test
    fun `intercept logs the request line`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extension)
        val client = clientWith(HttpLoggingInterceptor(), StubResponseInterceptor())

        // Act
        client.newCall(buildRequest()).execute().close()
        advanceUntilIdle()

        // Assert
        assertTrue(extension.networkRequests.single().message.contains("--> GET $LOG_URL"))
    }

    @Test
    fun `intercept logs the curl command of the request`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extension)
        val client = clientWith(HttpLoggingInterceptor(), StubResponseInterceptor())

        // Act
        client.newCall(buildRequest()).execute().close()
        advanceUntilIdle()

        // Assert
        val curl = extension.networkRequests.single().curl
        assertTrue(curl.startsWith("curl "))
        assertTrue(curl.contains(LOG_URL))
    }

    @Test
    fun `intercept logs the response status line`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extension)
        val client = clientWith(HttpLoggingInterceptor(), StubResponseInterceptor(code = 200))

        // Act
        client.newCall(buildRequest()).execute().close()
        advanceUntilIdle()

        // Assert
        assertTrue(extension.networkResponses.single().message.contains("<-- 200"))
    }

    @Test
    fun `intercept logs a successful response with the success state`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extension)
        val client = clientWith(HttpLoggingInterceptor(), StubResponseInterceptor())

        // Act
        client.newCall(buildRequest()).execute().close()
        advanceUntilIdle()

        // Assert
        assertEquals(NetworkRequestState.SUCCESS, extension.networkResponses.single().state)
    }

    @Test
    fun `intercept links the response log to the request uuid`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extension)
        val client = clientWith(HttpLoggingInterceptor(), StubResponseInterceptor())

        // Act
        client.newCall(buildRequest()).execute().close()
        advanceUntilIdle()

        // Assert
        assertEquals(
            extension.networkRequests.single().uuid,
            extension.networkResponses.single().requestUuid
        )
    }

    @Test
    fun `intercept logs a failed response with the error state`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extension)
        val client = clientWith(HttpLoggingInterceptor(), StubResponseInterceptor(code = 500))

        // Act
        client.newCall(buildRequest()).execute().close()
        advanceUntilIdle()

        // Assert
        assertEquals(NetworkRequestState.ERROR, extension.networkResponses.single().state)
    }

    @Test
    fun `intercept uses the error log level only for failed responses`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extension)
        val interceptor = HttpLoggingInterceptor(
            logLevel = HttpLogLevel.BASIC,
            errorLogLevel = HttpLogLevel.BODY
        )
        val client = clientWith(interceptor, StubResponseInterceptor(code = 500, body = RESPONSE_BODY))

        // Act
        client.newCall(buildRequest()).execute().close()
        advanceUntilIdle()

        // Assert
        assertTrue(extension.networkResponses.single().message.contains(RESPONSE_BODY))
    }

    @Test
    fun `intercept does not log the body of a successful response on basic level`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extension)
        val interceptor = HttpLoggingInterceptor(
            logLevel = HttpLogLevel.BASIC,
            errorLogLevel = HttpLogLevel.BODY
        )
        val client = clientWith(interceptor, StubResponseInterceptor(code = 200, body = RESPONSE_BODY))

        // Act
        client.newCall(buildRequest()).execute().close()
        advanceUntilIdle()

        // Assert
        assertFalse(extension.networkResponses.single().message.contains(RESPONSE_BODY))
    }

    @Test
    fun `intercept logs nothing when the log level is none`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extension)
        val client = clientWith(
            HttpLoggingInterceptor(logLevel = HttpLogLevel.NONE),
            StubResponseInterceptor()
        )

        // Act
        val code = client.newCall(buildRequest()).execute().use { it.code }
        advanceUntilIdle()

        // Assert
        assertTrue(extension.networkRequests.isEmpty())
        assertTrue(extension.networkResponses.isEmpty())
        assertEquals(200, code)
    }

    @Test
    fun `intercept logs the failure and rethrows the exception`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extension)
        val client = clientWithFailing(HttpLoggingInterceptor(), IOException("boom"))

        // Act
        val error = assertFailsWith<IOException> {
            client.newCall(buildRequest()).execute()
        }
        advanceUntilIdle()

        // Assert
        assertEquals("boom", error.message)
        assertTrue(extension.networkResponses.single().message.contains("HTTP FAILED"))
    }

    @Test
    fun `intercept logs a canceled request with the timeout state`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extension)
        val client = clientWithFailing(HttpLoggingInterceptor(), IOException("Canceled"))

        // Act
        assertFailsWith<IOException> {
            client.newCall(buildRequest()).execute()
        }
        advanceUntilIdle()

        // Assert
        assertEquals(NetworkRequestState.TIMEOUT, extension.networkResponses.single().state)
    }

    @Test
    fun `intercept logs a closed socket with the cancelled state`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extension)
        val client = clientWithFailing(HttpLoggingInterceptor(), SocketException("Socket closed"))

        // Act
        assertFailsWith<SocketException> {
            client.newCall(buildRequest()).execute()
        }
        advanceUntilIdle()

        // Assert
        assertEquals(NetworkRequestState.CANCELLED, extension.networkResponses.single().state)
    }

    @Test
    fun `intercept keeps the response body readable after logging it`() = runTest {
        // Arrange
        val extension = RecordingLoggerExtension()
        initLogger(extension)
        val client = clientWith(
            HttpLoggingInterceptor(logLevel = HttpLogLevel.BODY),
            StubResponseInterceptor(code = 200, body = RESPONSE_BODY)
        )

        // Act
        val body = client.newCall(buildRequest()).execute().use { it.body.string() }
        advanceUntilIdle()

        // Assert
        assertEquals(RESPONSE_BODY, body)
    }
}
