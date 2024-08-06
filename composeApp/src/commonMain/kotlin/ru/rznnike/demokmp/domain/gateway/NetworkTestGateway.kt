package ru.rznnike.demokmp.domain.gateway

interface NetworkTestGateway {
    suspend fun getRandomImageLink(): String
}