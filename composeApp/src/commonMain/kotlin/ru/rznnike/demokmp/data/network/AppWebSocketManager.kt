package ru.rznnike.demokmp.data.network

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.serialization.SerializationException
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.data.network.model.WebSocketMessageModel
import ru.rznnike.demokmp.data.preference.PreferencesManager
import ru.rznnike.demokmp.data.utils.defaultJson
import ru.rznnike.demokmp.domain.log.Logger
import ru.rznnike.demokmp.domain.model.websocket.WebSocketConnectionState
import ru.rznnike.demokmp.domain.model.websocket.WebSocketSessionData
import java.io.IOException

class AppWebSocketManager(
    private val client: HttpClient,
    private val preferencesManager: PreferencesManager
) {
    private val logger = Logger.withTag("AppWebSocket")
    private var session: WebSocketSession? = null
    private var connectionState = MutableStateFlow(WebSocketConnectionState.DISCONNECTED)
    private var sessionData: WebSocketSessionData<WebSocketMessageModel>? = null
    private val jsonParser = defaultJson()

    fun getSession(): WebSocketSessionData<WebSocketMessageModel> {
        return sessionData ?: let {
            connectionState = MutableStateFlow(WebSocketConnectionState.DISCONNECTED)
//            val url = preferencesManager.wsServerUrl.get()
            val url = BuildKonfig.API_WEBSOCKETS
            val messagesFlow = flow {
                if (connectionState.value == WebSocketConnectionState.CLOSED) return@flow

                logger.i("Trying to open a session: $url")
                connectionState.value = WebSocketConnectionState.CONNECTING
                client.webSocketSession(url).let { newSession ->
                    connectionState.value = WebSocketConnectionState.CONNECTED
                    logger.i("Session opened")
                    session = newSession

                    emitAll(
                        newSession.incoming.receiveAsFlow().mapNotNull { frame ->
                            logger.i("Message received with type ${frame.frameType}:")
                            var message: WebSocketMessageModel? = null
                            when (frame) {
                                is Frame.Text -> {
                                    val json = frame.readText()
                                    logger.i(json)
                                    try {
                                        message = jsonParser.decodeFromString<WebSocketMessageModel>(json)
                                    } catch (exception: SerializationException) {
                                        logger.e(exception, "Message NOT mapped")
                                    }
                                }
                                is Frame.Close -> closeSession()
                                is Frame.Binary -> Unit
                                is Frame.Ping -> Unit
                                is Frame.Pong -> Unit
                            }
                            return@mapNotNull message
                        }
                    )
                }
            }.retry { error ->
                logger.w("Connection error")
                (error is IOException).also {
                    connectionState.value = WebSocketConnectionState.DISCONNECTED
                    session = null
                    delay(10_000)
                }
            }
            WebSocketSessionData(
                url = url,
                messages = messagesFlow,
                connectionState = connectionState.asStateFlow()
            )
        }
    }

    suspend fun closeSession() {
        session?.let { session ->
            session.close(CloseReason(CloseReason.Codes.NORMAL, "Normal close request"))
            logger.i("Session closed")
        }
        connectionState.value = WebSocketConnectionState.CLOSED
        session = null
        sessionData = null
    }

    suspend fun sendMessage(message: WebSocketMessageModel) {
        session?.let { session ->
            val json = jsonParser.encodeToString(message)
            session.send(json)
            logger.i("Message sent:\n$json")
        }
    }
}