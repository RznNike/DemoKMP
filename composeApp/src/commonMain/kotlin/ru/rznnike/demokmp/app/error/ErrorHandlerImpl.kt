package ru.rznnike.demokmp.app.error

import io.ktor.serialization.*
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import ru.rznnike.demokmp.data.network.error.CustomServerException
import ru.rznnike.demokmp.domain.log.Logger
import ru.rznnike.demokmp.generated.resources.*
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.SocketException
import java.net.UnknownHostException
import javax.net.ssl.SSLHandshakeException

class ErrorHandlerImpl(
    private val stringRetriever: suspend (StringResource) -> String = ::getString
) : ErrorHandler {
    private val logger = Logger.withTag("ErrorHandler")

    override suspend fun proceed(error: Throwable, callback: suspend (String) -> Unit) {
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
        is SSLHandshakeException -> stringRetriever(Res.string.error_no_connection_to_server)
        is CustomServerException -> stringRetriever(if (error.isServerSide) Res.string.error_on_server else Res.string.unknown_error)
        is JsonConvertException -> stringRetriever(Res.string.error_json_convert)
        is UnsupportedOperationException -> stringRetriever(Res.string.error_unsupported_operation)
        is IOException -> stringRetriever(Res.string.error_io)
        is SecurityException -> stringRetriever(Res.string.error_security)
        else -> error.message ?: stringRetriever(Res.string.unknown_error)
    }
}