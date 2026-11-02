package ru.rznnike.demokmp.domain.log

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import ru.rznnike.demokmp.domain.log.extension.LoggerExtension
import java.time.Clock
import java.util.*
import kotlin.concurrent.Volatile

class Logger private constructor(
    private val tag: String
) {
    fun d(message: String) = withExtensions { timestamp ->
        d(tag = tag, message = message, timestamp = timestamp)
    }

    fun i(message: String) = withExtensions { timestamp ->
        i(tag = tag, message = message, timestamp = timestamp)
    }

    fun w(message: String) = withExtensions { timestamp ->
        w(tag = tag, message = message, timestamp = timestamp)
    }

    fun e(message: String) = withExtensions { timestamp ->
        e(tag = tag, message = message, timestamp = timestamp)
    }

    fun e(exception: Throwable, message: String = "") = withExtensions { timestamp ->
        e(tag = tag, exception = exception, message = message, timestamp = timestamp)
    }

    fun networkRequest(
        message: String,
        curl: String
    ): UUID {
        val uuid = UUID.randomUUID()
        withExtensions { timestamp ->
            networkRequest(
                tag = tag,
                uuid = uuid,
                message = message,
                curl = curl,
                timestamp = timestamp
            )
        }
        return uuid
    }

    fun networkResponse(
        requestUuid: UUID,
        message: String,
        state: NetworkRequestState
    ) = withExtensions { timestamp ->
        networkResponse(
            tag = tag,
            requestUuid = requestUuid,
            message = message,
            state = state,
            timestamp = timestamp
        )
    }

    private fun withExtensions(
        action: suspend LoggerExtension.(timestamp: Long) -> Unit
    ) {
        if (!isInitialized) {
            printLoggerError("Logger is not initialized!")
            return
        }

        val timestamp = clock.millis()
        val result = queue.trySend {
            extensions.forEach { extension ->
                try {
                    extension.action(timestamp)
                } catch (exception: Exception) {
                    printLoggerError("Error in logger extension ${extension::class.java.name}:\n${exception.stackTraceToString()}")
                }
            }
        }
        if (result.isFailure) {
            printLoggerError("Logger queue is closed, message dropped!")
        }
    }

    companion object {
        private var clock: Clock = Clock.systemUTC()
        private lateinit var coroutineDispatcher: CoroutineDispatcher

        @Volatile
        private var isInitialized = false
        private var extensions: MutableList<LoggerExtension> = mutableListOf()

        @Volatile
        private var queue: Channel<suspend () -> Unit> = Channel(capacity = Channel.UNLIMITED)
        private var consumerJob: Job? = null

        private val defaultLogger = Logger("")

        fun init(
            clock: Clock = Clock.systemUTC(),
            coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
            extensions: List<LoggerExtension>
        ) {
            stopConsumer()

            this.clock = clock
            this.coroutineDispatcher = coroutineDispatcher
            this.extensions = extensions.toMutableList()
            extensions.forEach { extension ->
                try {
                    extension.init(
                        clock = clock,
                        coroutineDispatcher = coroutineDispatcher
                    )
                } catch (exception: Exception) {
                    printLoggerError("Error in logger extension init ${extension::class.java.name}:\n${exception.stackTraceToString()}")
                }
            }

            queue = Channel(Channel.UNLIMITED)
            consumerJob = CoroutineScope(SupervisorJob() + coroutineDispatcher).launch {
                for (task in queue) {
                    try {
                        task()
                    } catch (cancellation: CancellationException) {
                        throw cancellation
                    } catch (exception: Exception) {
                        printLoggerError("Error while processing log message:\n${exception.stackTraceToString()}")
                    }
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

        fun reset() {
            isInitialized = false
            stopConsumer()
            extensions = mutableListOf()
        }

        private fun stopConsumer() {
            queue.close()
            consumerJob?.cancel()
            consumerJob = null
        }

        fun withTag(tag: String) = Logger(tag)

        fun d(message: String) = defaultLogger.d(message)

        fun i(message: String) = defaultLogger.i(message)

        fun w(message: String) = defaultLogger.w(message)

        fun e(message: String) = defaultLogger.e(message)

        fun e(exception: Throwable, message: String = "") = defaultLogger.e(exception, message)

        fun networkRequest(
            message: String,
            curl: String
        ) = defaultLogger.networkRequest(
            message = message,
            curl = curl
        )

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