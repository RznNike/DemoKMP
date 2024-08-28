package ru.rznnike.demokmp.domain.gateway

import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme

interface PreferencesGateway {
    suspend fun getTestCounter(): Int

    suspend fun setTestCounter(newValue: Int)

    suspend fun getLanguage(): Language

    suspend fun setLanguage(newValue: Language)

    suspend fun getTheme(): Theme

    suspend fun setTheme(newValue: Theme)
}