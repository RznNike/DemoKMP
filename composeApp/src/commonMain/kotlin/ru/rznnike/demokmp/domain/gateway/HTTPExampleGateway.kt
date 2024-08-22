package ru.rznnike.demokmp.domain.gateway

interface HTTPExampleGateway {
    suspend fun getRandomImageLinks(count: Int): List<String>
}