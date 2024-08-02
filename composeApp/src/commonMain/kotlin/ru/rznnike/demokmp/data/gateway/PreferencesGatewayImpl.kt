package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.data.preference.PreferencesManager
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.PreferencesGateway

class PreferencesGatewayImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val preferencesManager: PreferencesManager
) : PreferencesGateway {
    override suspend fun getTestCounter(): Int = withContext(dispatcherProvider.io) {
        preferencesManager.testCounter.get()
    }

    override suspend fun setTestCounter(newValue: Int) = withContext(dispatcherProvider.io) {
        preferencesManager.testCounter.set(newValue)
    }
}