package ru.rznnike.demokmp.data.network

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.rznnike.demokmp.data.network.model.WebSocketMessageModel
import ru.rznnike.demokmp.data.network.websocket.WebSocketConnectionState
import ru.rznnike.demokmp.data.network.websocket.WebSocketData
import ru.rznnike.demokmp.domain.utils.logger
import java.io.IOException

private const val LOG_TAG = "AppWebSocketManager"

class AppWebSocketManager(
    private val client: HttpClient,
    private val url: String
) {
    private var session: WebSocketSession? = null
    private var connectionState = MutableStateFlow(WebSocketConnectionState.DISCONNECTED)

    fun open(): WebSocketData<WebSocketMessageModel> {
        connectionState.value = WebSocketConnectionState.DISCONNECTED
        val messagesFlow = flow {
            logger("$LOG_TAG | Trying to open a session: $url")
            if (connectionState.value == WebSocketConnectionState.CLOSED) {
                close()
                return@flow
            }

            client.webSocketSession(url).let { newSession ->
                logger("$LOG_TAG | Session opened")
                session = newSession
                connectionState.value = WebSocketConnectionState.CONNECTED

                emitAll(
                    newSession.incoming.receiveAsFlow().mapNotNull {
                        val json = (it as? Frame.Text)?.readText() ?: ""
                        logger("$LOG_TAG | Message received:\n$json")
                        return@mapNotNull try {
                            Json.decodeFromString<WebSocketMessageModel>(json)
                        } catch (exception: Exception) {
                            logger("$LOG_TAG | Message NOT mapped")
                            null
                        }
                    }
                )
            }
        }.retry { error ->
            logger("$LOG_TAG | Connection error")
            (error is IOException).also {
                connectionState.value = WebSocketConnectionState.DISCONNECTED
                session = null
                delay(10_000)
            }
        }
        return WebSocketData(
            messages = messagesFlow,
            connectionState = connectionState.asStateFlow()
        )
    }

    suspend fun close() {
        session?.let { session ->
            session.close(CloseReason(CloseReason.Codes.NORMAL, "Normal close request"))
            logger("$LOG_TAG | Session closed: $url")
        }
        session = null
        connectionState.value = WebSocketConnectionState.CLOSED
    }

    suspend fun sendMessage(message: WebSocketMessageModel) {
        session?.let { session ->
            val json = Json.encodeToString(message)
            session.send(json)
            logger("$LOG_TAG | Message sent:\n$json")
        }
    }
}