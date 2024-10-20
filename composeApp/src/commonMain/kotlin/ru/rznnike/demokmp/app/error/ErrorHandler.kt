package ru.rznnike.demokmp.app.error

import org.jetbrains.compose.resources.getString
import ru.rznnike.demokmp.data.network.error.CustomServerException
import ru.rznnike.demokmp.domain.utils.logger
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.error_on_server
import ru.rznnike.demokmp.generated.resources.no_internet_connection
import ru.rznnike.demokmp.generated.resources.unknown_error
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

class ErrorHandler {
    suspend fun proceed(error: Throwable, callback: (String) -> Unit) {
        logger(error)
        when {
            error is CancellationException -> Unit
            else -> callback(getUserMessage(error))
        }
    }

    private suspend fun getUserMessage(error: Throwable): String {
        val messageRes = when (error) {
            is UnknownHostException -> Res.string.no_internet_connection
            is CustomServerException -> if (error.isServerSide) Res.string.error_on_server else Res.string.unknown_error
            else -> Res.string.unknown_error
        }
        return getString(messageRes)
    }
}
