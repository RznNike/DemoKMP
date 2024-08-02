package ru.rznnike.demokmp.domain.gateway

interface PreferencesGateway {
    suspend fun getTestCounter(): Int

    suspend fun setTestCounter(newValue: Int)
}