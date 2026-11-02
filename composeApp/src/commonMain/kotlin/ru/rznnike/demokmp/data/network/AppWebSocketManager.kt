package ru.rznnike.demokmp.data.network

import ru.rznnike.demokmp.data.network.model.WebSocketMessageModel
import ru.rznnike.demokmp.domain.model.websocket.WebSocketSessionData

interface AppWebSocketManager {
    fun getSession(): WebSocketSessionData<WebSocketMessageModel>

    suspend fun closeSession()

    suspend fun sendMessage(message: WebSocketMessageModel)
}
