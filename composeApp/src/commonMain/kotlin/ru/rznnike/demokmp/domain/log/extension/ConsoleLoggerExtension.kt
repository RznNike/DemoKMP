package ru.rznnike.demokmp.domain.log.extension

import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.printLog

class ConsoleLoggerExtension : LoggerExtension() {
    private val outputLock = Semaphore(1)

    override fun addMessage(
        tag: String,
        message: String,
        level: LogLevel,
        type: LogType,
        callback: suspend (LogMessage) -> Unit
    ) {
        val logMessage = LogMessage(
            type = type,
            level = level,
            timestamp = clock.millis(),
            tag = tag,
            message = message
        )

        coroutineScope.launch {
            outputLock.withPermit {
                printLog(logMessage)
                callback(logMessage)
            }
        }
    }
}