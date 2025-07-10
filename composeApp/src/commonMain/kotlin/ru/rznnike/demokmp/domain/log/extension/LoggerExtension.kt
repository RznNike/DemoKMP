package ru.rznnike.demokmp.domain.log.extension

import kotlinx.coroutines.CoroutineScope
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.NetworkRequestState
import java.time.Clock
import java.util.*

abstract class LoggerExtension {
    protected lateinit var clock: Clock
    protected lateinit var coroutineScope: CoroutineScope

    fun init(clock: Clock, coroutineScope: CoroutineScope) {
        this.clock = clock
        this.coroutineScope = coroutineScope
    }

    open fun d(tag: String, message: String) {
        if (BuildKonfig.DEBUG) {
            addMessage(
                tag = tag,
                message = message,
                level = LogLevel.DEBUG
            )
        }
    }

    open fun i(tag: String, message: String) {
        addMessage(
            tag = tag,
            message = message,
            level = LogLevel.INFO
        )
    }

    open fun w(tag: String, message: String) {
        addMessage(
            tag = tag,
            message = message,
            level = LogLevel.WARNING
        )
    }

    open fun e(tag: String, message: String) {
        addMessage(
            tag = tag,
            message = message,
            level = LogLevel.ERROR
        )
    }

    open fun e(tag: String, exception: Throwable, message: String) {
        val formattedMessage = "$message\n${exception.stackTraceToString()}"
        addMessage(
            tag = tag,
            message = formattedMessage,
            level = LogLevel.ERROR
        )
    }

    open fun networkRequest(tag: String, uuid: UUID, message: String) {
        addMessage(
            tag = tag,
            message = message,
            level = LogLevel.INFO,
            type = LogType.NETWORK
        )
    }

    open fun networkResponse(
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

    abstract fun addMessage(
        tag: String,
        message: String,
        level: LogLevel,
        type: LogType = LogType.DEFAULT
    ): LogMessage
}