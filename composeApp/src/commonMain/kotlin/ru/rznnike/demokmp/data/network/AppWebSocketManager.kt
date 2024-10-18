package ru.rznnike.demokmp.data.network

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.data.network.model.WebSocketMessageModel
import ru.rznnike.demokmp.data.preference.PreferencesManager
import ru.rznnike.demokmp.data.utils.defaultJson
import ru.rznnike.demokmp.domain.model.websocket.WebSocketConnectionState
import ru.rznnike.demokmp.domain.model.websocket.WebSocketSessionData
import ru.rznnike.demokmp.domain.utils.logger
import java.io.IOException

private const val LOG_TAG = "AppWebSocketManager"

class AppWebSocketManager(
    private val client: HttpClient,
    private val preferencesManager: PreferencesManager
) {
    private var session: WebSocketSession? = null
    private var connectionState = MutableStateFlow(WebSocketConnectionState.DISCONNECTED)
    private var sessionData: WebSocketSessionData<WebSocketMessageModel>? = null
    private val jsonParser = defaultJson()

    suspend fun getSession(): WebSocketSessionData<WebSocketMessageModel> {
        return sessionData ?: let {
            connectionState = MutableStateFlow(WebSocketConnectionState.DISCONNECTED)
//            val url = preferencesManager.wsServerUrl.get()
            val url = BuildKonfig.API_WEBSOCKETS
            val messagesFlow = flow {
                if (connectionState.value == WebSocketConnectionState.CLOSED) return@flow

                printLog("Trying to open a session: $url")
                connectionState.value = WebSocketConnectionState.CONNECTING
                client.webSocketSession(url).let { newSession ->
                    connectionState.value = WebSocketConnectionState.CONNECTED
                    printLog("Session opened")
                    session = newSession

                    emitAll(
                        newSession.incoming.receiveAsFlow().mapNotNull { frame ->
                            printLog("Message received with type ${frame.frameType}:")
                            var message: WebSocketMessageModel? = null
                            when (frame) {
                                is Frame.Text -> {
                                    val json = frame.readText()
                                    printLog(json)
                                    try {
                                        message = jsonParser.decodeFromString<WebSocketMessageModel>(json)
                                    } catch (exception: SerializationException) {
                                        printLog(exception, "Message NOT mapped")
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
                printLog("Connection error")
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
            printLog("Session closed")
        }
        connectionState.value = WebSocketConnectionState.CLOSED
        session = null
        sessionData = null
    }

    suspend fun sendMessage(message: WebSocketMessageModel) {
        session?.let { session ->
            val json = jsonParser.encodeToString(message)
            session.send(json)
            printLog("Message sent:\n$json")
        }
    }

    private fun printLog(message: String) {
        logger("$LOG_TAG | $message")
    }

    @Suppress("SameParameterValue")
    private fun printLog(exception: Throwable, message: String) {
        logger(exception, "$LOG_TAG | $message")
    }
}