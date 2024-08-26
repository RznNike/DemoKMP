package ru.rznnike.demokmp.data.network.websocket

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class WebSocketData<T>(
    val messages: Flow<T>,
    val connectionState: StateFlow<WebSocketConnectionState>
)