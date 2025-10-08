package ru.rznnike.demokmp.app.error

import io.ktor.serialization.JsonConvertException
import org.jetbrains.compose.resources.getString
import ru.rznnike.demokmp.data.network.error.CustomServerException
import ru.rznnike.demokmp.domain.log.Logger
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

class ErrorHandler {
    private val logger = Logger.withTag("ErrorHandler")

    suspend fun proceed(error: Throwable, callback: suspend (String) -> Unit) {
        when (error) {
            is CancellationException -> Unit
            is InterruptedIOException,
            is CustomServerException -> callback(getUserMessage(error))
            else -> {
                logger.e(error)
                callback(getUserMessage(error))
            }
        }
    }

    private suspend fun getUserMessage(error: Throwable) = when (error) {
        is UnknownHostException,
        is ConnectException,
        is InterruptedIOException,
        is SocketException,
        is SSLHandshakeException -> getString(Res.string.error_no_connection_to_server)
        is CustomServerException -> getString(if (error.isServerSide) Res.string.error_on_server else Res.string.unknown_error)
        is JsonConvertException -> getString(Res.string.error_json_convert)
        is UnsupportedOperationException -> getString(Res.string.error_unsupported_operation)
        is IOException -> getString(Res.string.error_io)
        is SecurityException -> getString(Res.string.error_security)
        else -> error.message ?: getString(Res.string.unknown_error)
    }
}
