package ru.rznnike.demokmp.domain.log

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.utils.*
import java.io.File
import java.time.Clock
import java.time.LocalDate
import java.util.*

class Logger private constructor(
    private val tag: String
) {
    fun d(message: String) {
        if (BuildKonfig.DEBUG) {
            addMessage(message, LogLevel.DEBUG)
        }
    }

    fun i(message: String) = addMessage(message, LogLevel.INFO)

    fun w(message: String) = addMessage(message, LogLevel.WARNING)

    fun e(message: String) = addMessage(message, LogLevel.ERROR)

    fun e(exception: Throwable, message: String = "") {
        val formattedMessage = "$message\n${exception.stackTraceToString()}"
        addMessage(formattedMessage, LogLevel.ERROR)
    }

    fun networkRequest(message: String): UUID {
        val uuid = UUID.randomUUID()
        val request = addMessage(message, LogLevel.INFO, LogType.NETWORK)
        if (OperatingSystem.isDesktop) {
            val logNetworkMessage = LogNetworkMessage(
                uuid = uuid,
                request = request
            )
            networkLog.add(logNetworkMessage)
            coroutineScope.launch {
                networkLogUpdatesFlow.emit(logNetworkMessage)
            }
        }
        return uuid
    }

    fun networkResponse(
        requestUuid: UUID,
        message: String,
        state: NetworkRequestState
    ) {
        val response = addMessage(message, LogLevel.INFO, LogType.NETWORK)
        if (OperatingSystem.isDesktop) {
            networkLog.firstOrNull { it.uuid == requestUuid }?.let { logNetworkMessage ->
                val updatedMessage = logNetworkMessage.copy(
                    response = response,
                    state = if (state == NetworkRequestState.SENT) NetworkRequestState.SUCCESS else state
                )
                val index = networkLog.lastIndexOf(logNetworkMessage)
                networkLog[index] = updatedMessage
                coroutineScope.launch {
                    networkLogUpdatesFlow.emit(updatedMessage)
                }
            }
        }
    }

    suspend fun subscribeToLog(
        initCallback: (List<LogMessage>) -> Unit,
        updateCallback: (LogMessage) -> Unit
    ) {
        initCallback(log)
        logUpdatesFlow.collect { message ->
            updateCallback(message)
        }
    }

    suspend fun subscribeToNetworkLog(
        initCallback: (List<LogNetworkMessage>) -> Unit,
        updateCallback: (LogNetworkMessage) -> Unit
    ) {
        initCallback(networkLog)
        networkLogUpdatesFlow.collect { message ->
            updateCallback(message)
        }
    }

    fun clearLog() {
        log.clear()
    }

    fun clearNetworkLog() {
        networkLog.clear()
    }

    private fun addMessage(
        message: String,
        level: LogLevel,
        type: LogType = LogType.DEFAULT
    ): LogMessage {
        val logMessage = LogMessage(
            type = type,
            level = level,
            timestamp = currentTimeMillis(),
            tag = tag,
            message = message
        )

        val formattedMessage = "%s | %s%s | %s".format(
            logMessage.timestamp.toDateString(GlobalConstants.DATE_PATTERN_TIME_MS),
            logMessage.level.label,
            if (logMessage.tag.isNotBlank()) " | ${logMessage.tag}" else "",
            logMessage.message
        )
        if (logMessage.level == LogLevel.ERROR) {
            System.err.println(formattedMessage)
        } else {
            println(formattedMessage)
        }

        if (OperatingSystem.isDesktop) {
            log.add(logMessage)
            coroutineScope.launch {
                logUpdatesFlow.emit(logMessage)
            }
            writeToFile(formattedMessage)
        }

        return logMessage
    }

    private fun writeToFile(formattedMessage: String) {
        coroutineScope.launch {
            synchronized(Companion) {
                try {
                    val currentDate = clock.millis().toLocalDate()
                    if (logFileDate != currentDate) {
                        logFileDate = currentDate
                        logFile = null
                    }

                    if (logFile == null) {
                        File(DataConstants.LOGS_PATH).mkdirs()
                        val logFileName = "${currentDate.millis().toDateString(GlobalConstants.DATE_PATTERN_FILE_NAME_DAY)}.txt"
                        logFile = File("${DataConstants.LOGS_PATH}/$logFileName")
                    }
                    logFile?.appendText(formattedMessage)
                    logFile?.appendText("\n")
                } catch (_: Exception) { }
            }
        }
    }

    companion object {
        private var initClock: Clock? = null
        private val clock: Clock by lazy { initClock ?: Clock.systemUTC() }
        private var initCoroutineScope: CoroutineScope? = null
        private val coroutineScope: CoroutineScope by lazy { initCoroutineScope ?: CoroutineScope(Dispatchers.IO) }

        private val defaultLogger = Logger("")

        private val log: MutableList<LogMessage> = mutableListOf()
        private val logUpdatesFlow = MutableSharedFlow<LogMessage>()

        private val networkLog: MutableList<LogNetworkMessage> = mutableListOf()
        private val networkLogUpdatesFlow = MutableSharedFlow<LogNetworkMessage>()

        private var logFile: File? = null
        private var logFileDate: LocalDate? = null

        fun init(
            clock: Clock = Clock.systemUTC(),
            coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
        ) {
            initClock = clock
            initCoroutineScope = coroutineScope
        }

        fun withTag(tag: String): Logger {
            return Logger(tag)
        }

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

        suspend fun subscribeToLog(
            initCallback: (List<LogMessage>) -> Unit,
            updateCallback: (LogMessage) -> Unit
        ) = defaultLogger.subscribeToLog(
            initCallback = initCallback,
            updateCallback = updateCallback
        )

        suspend fun subscribeToNetworkLog(
            initCallback: (List<LogNetworkMessage>) -> Unit,
            updateCallback: (LogNetworkMessage) -> Unit
        ) = defaultLogger.subscribeToNetworkLog(
            initCallback = initCallback,
            updateCallback = updateCallback
        )

        fun clearLog() = defaultLogger.clearLog()

        fun clearNetworkLog() = defaultLogger.clearNetworkLog()
    }
}