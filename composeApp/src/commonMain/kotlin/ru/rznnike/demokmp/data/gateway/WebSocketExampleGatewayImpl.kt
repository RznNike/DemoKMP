package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.data.network.AppWebSocketManager
import ru.rznnike.demokmp.data.network.model.toWebSocketMessage
import ru.rznnike.demokmp.data.network.model.toWebSocketMessageModel
import ru.rznnike.demokmp.data.network.websocket.WebSocketData
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.WebSocketExampleGateway
import ru.rznnike.demokmp.domain.model.wsexample.WebSocketMessage

class WebSocketExampleGatewayImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val manager: AppWebSocketManager
) : WebSocketExampleGateway {
    override suspend fun openAppWS(): WebSocketData<WebSocketMessage> = withContext(dispatcherProvider.io) {
        manager.open().let { data ->
            WebSocketData(
                messages = data.messages.map { it.toWebSocketMessage() },
                connectionState = data.connectionState
            )
        }
    }

    override suspend fun closeAppWS() = withContext(dispatcherProvider.io) {
        manager.close()
    }

    override suspend fun sendAppWSMessage(message: WebSocketMessage) = withContext(dispatcherProvider.io) {
        manager.sendMessage(message.toWebSocketMessageModel())
    }
}