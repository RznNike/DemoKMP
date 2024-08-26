package ru.rznnike.demokmp.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import ru.rznnike.demokmp.domain.model.wsexample.WebSocketMessage

@Serializable
data class WebSocketMessageModel(
    @SerialName("text")
    val text: String
)

fun WebSocketMessage.toWebSocketMessageModel() = WebSocketMessageModel(
    text = text
)

fun WebSocketMessageModel.toWebSocketMessage() = WebSocketMessage(
    text = text,
    isIncoming = true
)