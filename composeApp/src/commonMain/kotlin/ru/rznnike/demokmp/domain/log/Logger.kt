package ru.rznnike.demokmp.domain.log

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import ru.rznnike.demokmp.domain.log.extension.LoggerExtension
import java.time.Clock
import java.util.*

class Logger private constructor(
    private val tag: String
) {
    fun d(message: String) = withExtensions {
        d(tag = tag, message = message)
    }

    fun i(message: String) = withExtensions {
        i(tag = tag, message = message)
    }

    fun w(message: String) = withExtensions {
        w(tag = tag, message = message)
    }

    fun e(message: String) = withExtensions {
        e(tag = tag, message = message)
    }

    fun e(exception: Throwable, message: String = "") = withExtensions {
        e(tag = tag, exception = exception, message = message)
    }

    fun networkRequest(message: String): UUID {
        val uuid = UUID.randomUUID()
        withExtensions {
            networkRequest(
                tag = tag,
                uuid = uuid,
                message = message
            )
        }
        return uuid
    }

    fun networkResponse(
        requestUuid: UUID,
        message: String,
        state: NetworkRequestState
    ) = withExtensions {
        networkResponse(
            tag = tag,
            requestUuid = requestUuid,
            message = message,
            state = state
        )
    }

    private fun withExtensions(action: suspend LoggerExtension.() -> Unit) {
        if (isInitialized) {
            CoroutineScope(coroutineDispatcher).launch {
                outputLock.withPermit {
                    extensions.forEach {
                        try {
                            action(it)
                        } catch (exception: Exception) {
                            printLoggerError("Error in logger extension ${it::class.java.name}:\n${exception.stackTraceToString()}")
                        }
                    }
                }
            }
        } else {
            printLoggerError("Logger is not initialized!")
        }
    }

    companion object {
        private var clock: Clock = Clock.systemUTC()
        private lateinit var coroutineDispatcher: CoroutineDispatcher
        private var isInitialized = false
        private var extensions: MutableList<LoggerExtension> = mutableListOf()

        private val outputLock = Semaphore(1)

        private val defaultLogger = Logger("")

        fun init(
            clock: Clock = Clock.systemUTC(),
            coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
            extensions: List<LoggerExtension>
        ) {
            this.clock = clock
            this.coroutineDispatcher = coroutineDispatcher
            this.extensions = extensions.toMutableList()
            extensions.forEach {
                try {
                    it.init(
                        clock = clock,
                        coroutineDispatcher = coroutineDispatcher
                    )
                } catch (exception: Exception) {
                    printLoggerError("Error in logger extension init ${it::class.java.name}:\n${exception.stackTraceToString()}")
                }
            }
            isInitialized = true
        }

        fun addExtension(extension: LoggerExtension) {
            try {
                extension.init(
                    clock = clock,
                    coroutineDispatcher = coroutineDispatcher
                )
                extensions.add(extension)
            } catch (exception: Exception) {
                printLoggerError("Error in logger extension init ${extension::class.java.name}:\n${exception.stackTraceToString()}")
            }
        }

        fun withTag(tag: String) = Logger(tag)

        fun d(message: String) = defaultLogger.d(message)

        fun i(message: String) = defaultLogger.i(message)

        fun w(message: String) = defaultLogger.w(message)

        fun e(message: String) = defaultLogger.e(message)

        fun e(exception: Throwable, message: String = "") = defaultLogger.e(exception, message)

        fun networkRequest(message: String) = defaultLogger.networkRequest(message)

        fun networkResponse(
            requestUuid: UUID,
            message: String,
            state: NetworkRequestState
        ) = defaultLogger.networkResponse(
            requestUuid = requestUuid,
            message = message,
            state = state
        )

        private fun printLoggerError(message: String) {
            printLog(
                LogMessage(
                    type = LogType.DEFAULT,
                    level = LogLevel.ERROR,
                    timestamp = clock.millis(),
                    tag = "Logger",
                    message = message
                )
            )
        }
    }
}

expect fun formatLogMessage(message: LogMessage): String

expect fun printLog(message: LogMessage)