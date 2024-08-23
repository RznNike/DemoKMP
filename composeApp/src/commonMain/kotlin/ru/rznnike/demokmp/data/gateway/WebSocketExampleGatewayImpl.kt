package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.data.network.AppWebSocketManager
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.WebSocketExampleGateway

class WebSocketExampleGatewayImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val manager: AppWebSocketManager
) : WebSocketExampleGateway {
    override suspend fun openSession(onMessage: (String) -> Unit) = withContext(dispatcherProvider.io) {
        manager.openSession(onMessage)
    }

    override suspend fun closeSession() = withContext(dispatcherProvider.io) {
        manager.closeSession()
    }

    override suspend fun sendMessage(message: String) = withContext(dispatcherProvider.io) {
        manager.sendMessage(message)
    }
}