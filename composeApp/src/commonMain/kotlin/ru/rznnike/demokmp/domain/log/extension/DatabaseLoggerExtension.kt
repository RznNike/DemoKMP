package ru.rznnike.demokmp.domain.log.extension

import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.rznnike.demokmp.domain.interactor.log.AddLogMessageToDBUseCase
import ru.rznnike.demokmp.domain.interactor.log.AddLogNetworkMessageToDBUseCase
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType

class DatabaseLoggerExtension : LoggerExtension(), KoinComponent {
    private val addLogMessageToDBUseCase: AddLogMessageToDBUseCase by inject()
    private val addLogNetworkMessageToDBUseCase: AddLogNetworkMessageToDBUseCase by inject()

    private val outputLock = Semaphore(1)

//    override fun networkRequest(tag: String, uuid: UUID, message: String) {
//        val request = addMessage(
//            tag = tag,
//            message = message,
//            level = LogLevel.INFO,
//            type = LogType.NETWORK
//        )
//        if (OperatingSystem.isDesktop) {
//            val logNetworkMessage = LogNetworkMessage(
//                uuid = uuid,
//                request = request
//            )
//            networkLog.add(logNetworkMessage)
//            coroutineScope.launch {
//                networkLogUpdatesFlow.emit(logNetworkMessage)
//            }
//        }
//    }

//    override fun networkResponse(tag: String, requestUuid: UUID, message: String, state: NetworkRequestState) {
//        val response = addMessage(
//            tag = tag,
//            message = message,
//            level = LogLevel.INFO,
//            type = LogType.NETWORK
//        )
//        if (OperatingSystem.isDesktop) {
//            networkLog.firstOrNull { it.uuid == requestUuid }?.let { logNetworkMessage ->
//                val updatedMessage = logNetworkMessage.copy(
//                    response = response,
//                    state = if (state == NetworkRequestState.SENT) NetworkRequestState.SUCCESS else state
//                )
//                val index = networkLog.lastIndexOf(logNetworkMessage)
//                networkLog[index] = updatedMessage
//                coroutineScope.launch {
//                    networkLogUpdatesFlow.emit(updatedMessage)
//                }
//            }
//        }
//    }

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
                addLogMessageToDBUseCase(logMessage)
            }
        }

        return logMessage
    }
}