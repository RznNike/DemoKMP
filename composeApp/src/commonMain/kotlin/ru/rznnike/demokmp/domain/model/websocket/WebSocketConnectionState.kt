package ru.rznnike.demokmp.domain.model.websocket

enum class WebSocketConnectionState {
    CONNECTING,
    CONNECTED,
    DISCONNECTED,
    CLOSED
}