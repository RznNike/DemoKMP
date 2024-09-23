package ru.rznnike.demokmp.domain.model.websocket

data class WebSocketMessage(
    val text: String,
    val isIncoming: Boolean
)
