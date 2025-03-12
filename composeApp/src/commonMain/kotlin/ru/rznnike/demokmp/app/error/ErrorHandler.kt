package ru.rznnike.demokmp.app.error

import org.jetbrains.compose.resources.getString
import ru.rznnike.demokmp.data.network.error.CustomServerException
import ru.rznnike.demokmp.domain.log.Logger
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.error_no_connection_to_server
import ru.rznnike.demokmp.generated.resources.error_on_server
import ru.rznnike.demokmp.generated.resources.unknown_error
import java.io.InterruptedIOException
import java.net.ConnectException
import java.net.UnknownHostException
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
        is InterruptedIOException -> getString(Res.string.error_no_connection_to_server)
        is CustomServerException -> getString(if (error.isServerSide) Res.string.error_on_server else Res.string.unknown_error)
        else -> error.message ?: getString(Res.string.unknown_error)
    }
}
