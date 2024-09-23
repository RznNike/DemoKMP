package ru.rznnike.demokmp.domain.gateway

import ru.rznnike.demokmp.domain.model.websocket.WebSocketMessage
import ru.rznnike.demokmp.domain.model.websocket.WebSocketSessionData

interface WebSocketExampleGateway {
    suspend fun getSession(): WebSocketSessionData<WebSocketMessage>

    suspend fun closeSession()

    suspend fun sendMessage(message: WebSocketMessage)
}