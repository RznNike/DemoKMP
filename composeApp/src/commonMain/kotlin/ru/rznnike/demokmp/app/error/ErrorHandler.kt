package ru.rznnike.demokmp.app.error

import demokmp.composeapp.generated.resources.Res
import demokmp.composeapp.generated.resources.test_error
import demokmp.composeapp.generated.resources.unknown_error
import org.jetbrains.compose.resources.getString
import ru.rznnike.demokmp.domain.utils.logger
import java.io.FileNotFoundException
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
            is FileNotFoundException -> Res.string.test_error
            else -> Res.string.unknown_error
        }
        return getString(messageRes)
    }
}
