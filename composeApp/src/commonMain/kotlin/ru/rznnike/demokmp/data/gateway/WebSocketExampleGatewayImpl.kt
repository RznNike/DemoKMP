package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.data.network.AppWebSocketManager
import ru.rznnike.demokmp.data.network.model.toWebSocketMessage
import ru.rznnike.demokmp.data.network.model.toWebSocketMessageModel
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.WebSocketExampleGateway
import ru.rznnike.demokmp.domain.model.websocket.WebSocketMessage
import ru.rznnike.demokmp.domain.model.websocket.WebSocketSessionData

class WebSocketExampleGatewayImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val manager: AppWebSocketManager
) : WebSocketExampleGateway {
    override suspend fun getSession(): WebSocketSessionData<WebSocketMessage> = withContext(dispatcherProvider.io) {
        manager.getSession().let { data ->
            WebSocketSessionData(
                url = data.url,
                messages = data.messages.map { it.toWebSocketMessage() },
                connectionState = data.connectionState
            )
        }
    }

    override suspend fun closeSession() = withContext(dispatcherProvider.io) {
        manager.closeSession()
    }

    override suspend fun sendMessage(message: WebSocketMessage) = withContext(dispatcherProvider.io) {
        manager.sendMessage(message.toWebSocketMessageModel())
    }
}