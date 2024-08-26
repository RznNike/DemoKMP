package ru.rznnike.demokmp.app.error

import demokmp.composeapp.generated.resources.Res
import demokmp.composeapp.generated.resources.no_internet_connection
import demokmp.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.getString
import ru.rznnike.demokmp.domain.utils.logger
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
            else -> Res.string.unknown_error
        }
        return getString(messageRes)
    }
}
