package ru.rznnike.demokmp.domain.log.extension

import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.rznnike.demokmp.domain.interactor.log.AddLogMessageToDBUseCase
import ru.rznnike.demokmp.domain.interactor.log.AddLogNetworkMessageToDBUseCase
import ru.rznnike.demokmp.domain.interactor.log.GetLogNetworkMessageUseCase
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogNetworkMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.NetworkRequestState
import java.util.UUID

class DatabaseLoggerExtension : LoggerExtension(), KoinComponent {
    private val addLogMessageToDBUseCase: AddLogMessageToDBUseCase by inject()
    private val addLogNetworkMessageToDBUseCase: AddLogNetworkMessageToDBUseCase by inject()
    private val getLogNetworkMessageUseCase: GetLogNetworkMessageUseCase by inject()

    private val outputLock = Semaphore(1)

    override fun networkRequest(tag: String, uuid: UUID, message: String) {
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
            addLogNetworkMessageToDBUseCase(logNetworkMessage)
        }
    }

    override fun networkResponse(tag: String, requestUuid: UUID, message: String, state: NetworkRequestState) {
        addMessage(
            tag = tag,
            message = message,
            level = LogLevel.INFO,
            type = LogType.NETWORK
        ) { response ->
            getLogNetworkMessageUseCase(requestUuid).process(
                { result ->
                    withContext(coroutineScope.coroutineContext) {
                        result.message?.let { logNetworkMessage ->
                            val updatedMessage = logNetworkMessage.copy(
                                response = response,
                                state = if (state == NetworkRequestState.SENT) NetworkRequestState.SUCCESS else state
                            )
                            addLogNetworkMessageToDBUseCase(updatedMessage)
                        }
                    }
                }
            )
        }
    }

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
                addLogMessageToDBUseCase(logMessage)
                callback(logMessage)
            }
        }
    }
}