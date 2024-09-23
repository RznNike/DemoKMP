package ru.rznnike.demokmp.domain.model.websocket

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

data class WebSocketSessionData<T>(
    val url: String,
    val messages: Flow<T>,
    val connectionState: StateFlow<WebSocketConnectionState>
)