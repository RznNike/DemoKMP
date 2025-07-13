package ru.rznnike.demokmp.domain.log.extension

import kotlinx.coroutines.CoroutineDispatcher
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.NetworkRequestState
import java.time.Clock
import java.util.*

abstract class LoggerExtension {
    protected lateinit var clock: Clock
    protected lateinit var coroutineDispatcher: CoroutineDispatcher

    fun init(clock: Clock, coroutineDispatcher: CoroutineDispatcher) {
        this.clock = clock
        this.coroutineDispatcher = coroutineDispatcher
    }

    open suspend fun d(tag: String, message: String) {
        if (BuildKonfig.DEBUG) {
            addMessage(
                tag = tag,
                message = message,
                level = LogLevel.DEBUG
            )
        }
    }

    open suspend fun i(tag: String, message: String) {
        addMessage(
            tag = tag,
            message = message,
            level = LogLevel.INFO
        )
    }

    open suspend fun w(tag: String, message: String) {
        addMessage(
            tag = tag,
            message = message,
            level = LogLevel.WARNING
        )
    }

    open suspend fun e(tag: String, message: String) {
        addMessage(
            tag = tag,
            message = message,
            level = LogLevel.ERROR
        )
    }

    open suspend fun e(tag: String, exception: Throwable, message: String) {
        val formattedMessage = "$message\n${exception.stackTraceToString()}"
        addMessage(
            tag = tag,
            message = formattedMessage,
            level = LogLevel.ERROR
        )
    }

    open suspend fun networkRequest(tag: String, uuid: UUID, message: String) {
        addMessage(
            tag = tag,
            message = message,
            level = LogLevel.INFO,
            type = LogType.NETWORK
        )
    }

    open suspend fun networkResponse(
        tag: String,
        requestUuid: UUID,
        message: String,
        state: NetworkRequestState
    ) {
        addMessage(
            tag = tag,
            message = message,
            level = LogLevel.INFO,
            type = LogType.NETWORK
        )
    }

    abstract suspend fun addMessage(
        tag: String,
        message: String,
        level: LogLevel,
        type: LogType = LogType.DEFAULT,
        callback: suspend (LogMessage) -> Unit = { }
    )
}