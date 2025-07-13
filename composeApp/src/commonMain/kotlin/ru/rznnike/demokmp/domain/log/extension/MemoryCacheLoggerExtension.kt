package ru.rznnike.demokmp.domain.log.extension

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.domain.log.*
import java.util.*

class MemoryCacheLoggerExtension : LoggerExtension() {
    private val outputLock = Semaphore(1)
    private val log: MutableList<LogMessage> = mutableListOf()
    private val logUpdatesFlow = MutableSharedFlow<LogMessage>()
    private val networkLog: MutableList<LogNetworkMessage> = mutableListOf()
    private val networkLogUpdatesFlow = MutableSharedFlow<LogNetworkMessage>()

    override suspend fun networkRequest(tag: String, uuid: UUID, message: String) {
        addMessage(
            tag = tag,
            message = message,
            level = LogLevel.INFO,
            type = LogType.NETWORK
        ) { request ->
            val logNetworkMessage = LogNetworkMessage(
                uuid = uuid,
                request = request
            )
            networkLog.add(logNetworkMessage)
            networkLogUpdatesFlow.emit(logNetworkMessage)
        }
    }

    override suspend fun networkResponse(tag: String, requestUuid: UUID, message: String, state: NetworkRequestState) {
        addMessage(
            tag = tag,
            message = message,
            level = LogLevel.INFO,
            type = LogType.NETWORK
        ) { response ->
            networkLog.firstOrNull { it.uuid == requestUuid }?.let { logNetworkMessage ->
                val updatedMessage = logNetworkMessage.copy(
                    response = response,
                    state = if (state == NetworkRequestState.SENT) NetworkRequestState.SUCCESS else state
                )
                val index = networkLog.lastIndexOf(logNetworkMessage)
                networkLog[index] = updatedMessage
                networkLogUpdatesFlow.emit(updatedMessage)
            }
        }
    }

    override suspend fun addMessage(
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

        withContext(coroutineDispatcher) {
            outputLock.withPermit {
                log.add(logMessage)
                logUpdatesFlow.emit(logMessage)
                callback(logMessage)
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
}