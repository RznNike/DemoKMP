package ru.rznnike.demokmp.domain.log.extension

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import ru.rznnike.demokmp.domain.interactor.log.AddLogMessageToDBUseCase
import ru.rznnike.demokmp.domain.interactor.log.AddLogNetworkMessageToDBUseCase
import ru.rznnike.demokmp.domain.interactor.log.GetLogNetworkMessageUseCase
import ru.rznnike.demokmp.domain.log.*
import java.time.Clock
import java.util.*

class DatabaseLoggerExtension(
    stopAfterOneError: Boolean = false
) : LoggerExtension(stopAfterOneError = stopAfterOneError), KoinComponent {
    private val addLogMessageToDBUseCase: AddLogMessageToDBUseCase by inject()
    private val addLogNetworkMessageToDBUseCase: AddLogNetworkMessageToDBUseCase by inject()
    private val getLogNetworkMessageUseCase: GetLogNetworkMessageUseCase by inject()

    private val outputLock = Semaphore(1)

    override fun init(clock: Clock, coroutineDispatcher: CoroutineDispatcher) {
        super.init(clock, coroutineDispatcher)
        runBlocking {
            addMessage(
                tag = "",
                message = "",
                level = LogLevel.INFO,
                type = LogType.SESSION_START
            ) { serviceMessage ->
                val logNetworkMessage = LogNetworkMessage(
                    uuid = UUID.randomUUID(),
                    request = serviceMessage
                )
                addLogNetworkMessageToDBUseCase(logNetworkMessage)
            }
        }
    }

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
            addLogNetworkMessageToDBUseCase(logNetworkMessage)
        }
    }

    override suspend fun networkResponse(tag: String, requestUuid: UUID, message: String, state: NetworkRequestState) {
        addMessage(
            tag = tag,
            message = message,
            level = LogLevel.INFO,
            type = LogType.NETWORK
        ) { response ->
            getLogNetworkMessageUseCase(requestUuid).process(
                { result ->
                    withContext(coroutineDispatcher) {
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

    override suspend fun addMessage(
        tag: String,
        message: String,
        level: LogLevel,
        type: LogType,
        callback: suspend (LogMessage) -> Unit
    ) {
        if ((!stopAfterOneError) || (!isErrorDetected)) {
            val logMessage = LogMessage(
                type = type,
                level = level,
                timestamp = clock.millis(),
                tag = tag,
                message = message
            )

            try {
                withContext(coroutineDispatcher) {
                    outputLock.withPermit {
                        addLogMessageToDBUseCase(logMessage)
                        callback(logMessage)
                    }
                }
            } catch (exception: Exception) {
                isErrorDetected = true
                throw exception
            }
        }
    }
}