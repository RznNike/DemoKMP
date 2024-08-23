package ru.rznnike.demokmp.data.network

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.receiveAsFlow
import ru.rznnike.demokmp.domain.utils.logger

class AppWebSocketManager(
    private val client: HttpClient,
    private val url: String
) {
    private var session: WebSocketSession? = null

    suspend fun openSession(onMessage: (String) -> Unit) {
        session = client.webSocketSession(url)
        logger("${this.javaClass.simpleName} | Session opened: $url")
        session?.incoming?.receiveAsFlow()?.collect {
            val message = (it as? Frame.Text)?.readText() ?: ""
            logger("${this.javaClass.simpleName} | Message received:\n$message")
            onMessage(message)
        }
    }

    suspend fun closeSession() {
        session?.close(CloseReason(CloseReason.Codes.NORMAL, "Normal close request"))
        session = null
        logger("${this.javaClass.simpleName} | Session closed: $url")
    }

    suspend fun sendMessage(message: String) {
        session?.send(message)
        logger("${this.javaClass.simpleName} | Message sent:\n$message")
    }
}