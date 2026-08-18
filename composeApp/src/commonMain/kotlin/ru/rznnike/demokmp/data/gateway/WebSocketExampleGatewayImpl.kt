package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.flow.map
import ru.rznnike.demokmp.data.network.AppWebSocketManager
import ru.rznnike.demokmp.data.network.model.toWebSocketMessage
import ru.rznnike.demokmp.data.network.model.toWebSocketMessageModel
import ru.rznnike.demokmp.domain.gateway.WebSocketExampleGateway
import ru.rznnike.demokmp.domain.model.websocket.WebSocketMessage
import ru.rznnike.demokmp.domain.model.websocket.WebSocketSessionData

class WebSocketExampleGatewayImpl(
    private val manager: AppWebSocketManager
) : WebSocketExampleGateway {
    override suspend fun getSession(): WebSocketSessionData<WebSocketMessage> {
        return manager.getSession().let { data ->
            WebSocketSessionData(
                url = data.url,
                messages = data.messages.map { it.toWebSocketMessage() },
                connectionState = data.connectionState
            )
        }
    }

    override suspend fun closeSession() {
        manager.closeSession()
    }

    override suspend fun sendMessage(message: WebSocketMessage) {
        manager.sendMessage(message.toWebSocketMessageModel())
    }
}