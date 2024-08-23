package ru.rznnike.demokmp.domain.gateway

interface WebSocketExampleGateway {
    suspend fun openSession(onMessage: (String) -> Unit)

    suspend fun closeSession()

    suspend fun sendMessage(message: String)
}