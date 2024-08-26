package ru.rznnike.demokmp.data.network

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.rznnike.demokmp.data.network.model.WebSocketMessageModel
import ru.rznnike.demokmp.domain.utils.logger

class AppWebSocketManager(
    private val client: HttpClient,
    private val url: String
) {
    private var session: WebSocketSession? = null

    suspend fun openSession(): Flow<WebSocketMessageModel> {
        return client.webSocketSession(url).let { newSession ->
            logger("${this.javaClass.simpleName} | Session opened: $url")
            session = newSession
            newSession.incoming.receiveAsFlow().mapNotNull {
                val json = (it as? Frame.Text)?.readText() ?: ""
                logger("${this.javaClass.simpleName} | Message received:\n$json")
                return@mapNotNull try {
                    Json.decodeFromString<WebSocketMessageModel>(json)
                } catch (exception: Exception) {
                    logger("${this.javaClass.simpleName} | Message NOT mapped")
                    null
                }
            }
        }
    }

    suspend fun closeSession() {
        session?.close(CloseReason(CloseReason.Codes.NORMAL, "Normal close request"))
        session = null
        logger("${this.javaClass.simpleName} | Session closed: $url")
    }

    suspend fun sendMessage(message: WebSocketMessageModel) {
        val json = Json.encodeToString(message)
        session?.send(json)
        logger("${this.javaClass.simpleName} | Message sent:\n$json")
    }
}