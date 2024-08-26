package ru.rznnike.demokmp.domain.gateway

import kotlinx.coroutines.flow.Flow
import ru.rznnike.demokmp.domain.model.wsexample.WebSocketMessage

interface WebSocketExampleGateway {
    suspend fun openSession(): Flow<WebSocketMessage>

    suspend fun closeSession()

    suspend fun sendMessage(message: WebSocketMessage)
}