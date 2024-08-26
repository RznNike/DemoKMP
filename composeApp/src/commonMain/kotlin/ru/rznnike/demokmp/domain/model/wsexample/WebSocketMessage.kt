package ru.rznnike.demokmp.domain.model.wsexample

data class WebSocketMessage(
    val text: String,
    val isIncoming: Boolean
)
