package ru.rznnike.demokmp.app.error

import io.ktor.serialization.JsonConvertException
import kotlinx.coroutines.test.runTest
import org.jetbrains.compose.resources.StringResource
import ru.rznnike.demokmp.data.network.error.CustomServerException
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.error_io
import ru.rznnike.demokmp.generated.resources.error_json_convert
import ru.rznnike.demokmp.generated.resources.error_no_connection_to_server
import ru.rznnike.demokmp.generated.resources.error_on_server
import ru.rznnike.demokmp.generated.resources.error_security
import ru.rznnike.demokmp.generated.resources.error_unsupported_operation
import ru.rznnike.demokmp.generated.resources.unknown_error
import java.io.IOException
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals

class ErrorHandlerImplTest {
    private class FakeStringRetriever {
        val calls = mutableListOf<StringResource>()
        fun create(): suspend (StringResource) -> String = { res ->
            calls.add(res)
            when (res) {
                Res.string.error_no_connection_to_server -> "NO_CONNECTION"
                Res.string.error_on_server -> "SERVER_ERROR"
                Res.string.error_json_convert -> "JSON_CONVERT"
                Res.string.error_io -> "IO_ERROR"
                Res.string.error_security -> "SECURITY_ERROR"
                Res.string.error_unsupported_operation -> "UNSUPPORTED"
                Res.string.unknown_error -> "UNKNOWN"
                else -> "UNKNOWN"
            }
        }
    }

    @Test
    fun `CancellationException produces no callback`() = runTest {
        val fake = FakeStringRetriever()
        val handler = ErrorHandlerImpl(fake.create())
        var called = false
        handler.proceed(CancellationException("cancelled")) { called = true }
        assertEquals(false, called)
        assertEquals(0, fake.calls.size)
    }

    @Test
    fun `InterruptedIOException uses connection error message without logging path`() = runTest {
        val fake = FakeStringRetriever()
        val handler = ErrorHandlerImpl(fake.create())
        var result: String? = null
        handler.proceed(InterruptedIOException("interrupted")) { result = it }
        assertEquals("NO_CONNECTION", result)
        assertEquals(listOf(Res.string.error_no_connection_to_server), fake.calls)
    }

    @Test
    fun `CustomServerException serverSide true maps to server error`() = runTest {
        val fake = FakeStringRetriever()
        val handler = ErrorHandlerImpl(fake.create())
        var result: String? = null
        handler.proceed(CustomServerException(500)) { result = it }
        assertEquals("SERVER_ERROR", result)
        assertEquals(listOf(Res.string.error_on_server), fake.calls)
    }

    @Test
    fun `CustomServerException serverSide false maps to unknown error`() = runTest {
        val fake = FakeStringRetriever()
        val handler = ErrorHandlerImpl(fake.create())
        var result: String? = null
        handler.proceed(CustomServerException(400)) { result = it }
        assertEquals("UNKNOWN", result)
        assertEquals(listOf(Res.string.unknown_error), fake.calls)
    }

    @Test
    fun `UnknownHostException maps to no connection`() = runTest {
        val fake = FakeStringRetriever()
        val handler = ErrorHandlerImpl(fake.create())
        var result: String? = null
        handler.proceed(UnknownHostException("host")) { result = it }
        assertEquals("NO_CONNECTION", result)
        assertEquals(listOf(Res.string.error_no_connection_to_server), fake.calls)
    }

    @Test
    fun `ConnectException maps to no connection`() = runTest {
        val fake = FakeStringRetriever()
        val handler = ErrorHandlerImpl(fake.create())
        var result: String? = null
        handler.proceed(ConnectException("refused")) { result = it }
        assertEquals("NO_CONNECTION", result)
    }

    @Test
    fun `SocketException maps to no connection`() = runTest {
        val fake = FakeStringRetriever()
        val handler = ErrorHandlerImpl(fake.create())
        var result: String? = null
        handler.proceed(SocketException("reset")) { result = it }
        assertEquals("NO_CONNECTION", result)
    }

    @Test
    fun `SSLHandshakeException maps to no connection`() = runTest {
        val fake = FakeStringRetriever()
        val handler = ErrorHandlerImpl(fake.create())
        var result: String? = null
        handler.proceed(SSLHandshakeException("ssl")) { result = it }
        assertEquals("NO_CONNECTION", result)
    }

    @Test
    fun `JsonConvertException maps to json convert error`() = runTest {
        val fake = FakeStringRetriever()
        val handler = ErrorHandlerImpl(fake.create())
        var result: String? = null
        handler.proceed(JsonConvertException("bad json", null)) { result = it }
        assertEquals("JSON_CONVERT", result)
        assertEquals(listOf(Res.string.error_json_convert), fake.calls)
    }

    @Test
    fun `UnsupportedOperationException maps to unsupported error`() = runTest {
        val fake = FakeStringRetriever()
        val handler = ErrorHandlerImpl(fake.create())
        var result: String? = null
        handler.proceed(UnsupportedOperationException("nope")) { result = it }
        assertEquals("UNSUPPORTED", result)
        assertEquals(listOf(Res.string.error_unsupported_operation), fake.calls)
    }

    @Test
    fun `IOException maps to io error`() = runTest {
        val fake = FakeStringRetriever()
        val handler = ErrorHandlerImpl(fake.create())
        var result: String? = null
        handler.proceed(IOException("io")) { result = it }
        assertEquals("IO_ERROR", result)
        assertEquals(listOf(Res.string.error_io), fake.calls)
    }

    @Test
    fun `SecurityException maps to security error`() = runTest {
        val fake = FakeStringRetriever()
        val handler = ErrorHandlerImpl(fake.create())
        var result: String? = null
        handler.proceed(SecurityException("denied")) { result = it }
        assertEquals("SECURITY_ERROR", result)
        assertEquals(listOf(Res.string.error_security), fake.calls)
    }

    @Test
    fun `unknown exception with message uses error message directly`() = runTest {
        val fake = FakeStringRetriever()
        val handler = ErrorHandlerImpl(fake.create())
        var result: String? = null
        handler.proceed(IllegalStateException("boom")) { result = it }
        assertEquals("boom", result)
        assertEquals(0, fake.calls.size)
    }

    @Test
    fun `unknown exception without message falls back to unknown error`() = runTest {
        val fake = FakeStringRetriever()
        val handler = ErrorHandlerImpl(fake.create())
        var result: String? = null
        handler.proceed(IllegalStateException()) { result = it }
        assertEquals("UNKNOWN", result)
        assertEquals(listOf(Res.string.unknown_error), fake.calls)
    }

    @Test
    fun `CustomServerException takes precedence over logging path`() = runTest {
        val fake = FakeStringRetriever()
        val handler = ErrorHandlerImpl(fake.create())
        var result: String? = null
        handler.proceed(CustomServerException(503)) { result = it }
        assertEquals("SERVER_ERROR", result)
    }
}
