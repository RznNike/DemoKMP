package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.data.preference.PreferencesManager
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.PreferencesGateway
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme

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

    override suspend fun getLanguage(): Language = withContext(dispatcherProvider.io) {
        preferencesManager.language.get()
    }

    override suspend fun setLanguage(newValue: Language) = withContext(dispatcherProvider.io) {
        preferencesManager.language.set(newValue)
    }

    override suspend fun getTheme(): Theme = withContext(dispatcherProvider.io) {
        preferencesManager.theme.get()
    }

    override suspend fun setTheme(newValue: Theme) = withContext(dispatcherProvider.io) {
        preferencesManager.theme.set(newValue)
    }
}