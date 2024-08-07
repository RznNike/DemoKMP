package ru.rznnike.demokmp.domain.gateway

interface NetworkExampleGateway {
    suspend fun getRandomImageLinks(count: Int): List<String>
}