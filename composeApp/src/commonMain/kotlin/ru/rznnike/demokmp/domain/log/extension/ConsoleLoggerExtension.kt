package ru.rznnike.demokmp.domain.log.extension

import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.printLog

class ConsoleLoggerExtension(
    stopAfterOneError: Boolean = false
) : LoggerExtension(stopAfterOneError = stopAfterOneError) {
    private val outputLock = Semaphore(1)

    override suspend fun addMessage(
        tag: String,
        message: String,
        level: LogLevel,
        type: LogType,
        callback: suspend (LogMessage) -> Unit
    ) {
        if ((!stopAfterOneError) || (!isErrorDetected)) {
            val logMessage = LogMessage(
                type = type,
                level = level,
                timestamp = clock.millis(),
                tag = tag,
                message = message
            )

            try {
                withContext(coroutineDispatcher) {
                    outputLock.withPermit {
                        printLog(logMessage)
                        callback(logMessage)
                    }
                }
            } catch (exception: Exception) {
                isErrorDetected = true
                throw exception
            }
        }
    }
}