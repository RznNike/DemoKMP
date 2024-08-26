package ru.rznnike.demokmp.domain.gateway

import ru.rznnike.demokmp.data.network.websocket.WebSocketData
import ru.rznnike.demokmp.domain.model.wsexample.WebSocketMessage

interface WebSocketExampleGateway {
    suspend fun openAppWS(): WebSocketData<WebSocketMessage>

    suspend fun closeAppWS()

    suspend fun sendAppWSMessage(message: WebSocketMessage)
}