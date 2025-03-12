package ru.rznnike.demokmp.domain.gateway

interface AppGateway {
    suspend fun checkIfAppIsAlreadyRunning(): Boolean

    suspend fun closeAppSingleInstanceSocket()
}