package ru.rznnike.demokmp.domain.log.extension

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import ru.rznnike.demokmp.domain.log.*
import ru.rznnike.demokmp.domain.utils.OperatingSystem
import java.util.*

class MemoryCacheLoggerExtension : LoggerExtension() {
    private val outputLock = Semaphore(1)
    private val log: MutableList<LogMessage> = mutableListOf()
    private val logUpdatesFlow = MutableSharedFlow<LogMessage>()
    private val networkLog: MutableList<LogNetworkMessage> = mutableListOf()
    private val networkLogUpdatesFlow = MutableSharedFlow<LogNetworkMessage>()

    override fun networkRequest(tag: String, uuid: UUID, message: String) {
        val request = addMessage(
            tag = tag,
            message = message,
            level = LogLevel.INFO,
            type = LogType.NETWORK
        )
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
    }

    override fun networkResponse(tag: String, requestUuid: UUID, message: String, state: NetworkRequestState) {
        val response = addMessage(
            tag = tag,
            message = message,
            level = LogLevel.INFO,
            type = LogType.NETWORK
        )
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

    override fun addMessage(
        tag: String,
        message: String,
        level: LogLevel,
        type: LogType
    ): LogMessage {
        val logMessage = LogMessage(
            type = type,
            level = level,
            timestamp = clock.millis(),
            tag = tag,
            message = message
        )

        coroutineScope.launch {
            outputLock.withPermit {
                if (OperatingSystem.isDesktop) {
                    log.add(logMessage)
                    logUpdatesFlow.emit(logMessage)
                }
            }
        }

        return logMessage
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
}