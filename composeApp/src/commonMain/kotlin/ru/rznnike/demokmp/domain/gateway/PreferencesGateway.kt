package ru.rznnike.demokmp.domain.gateway

import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme
import ru.rznnike.demokmp.domain.model.print.PrintSettings

interface PreferencesGateway {
    suspend fun getTestCounter(): Int

    suspend fun setTestCounter(newValue: Int)

    suspend fun getLanguage(): Language

    suspend fun setLanguage(newValue: Language)

    suspend fun getTheme(): Theme

    suspend fun setTheme(newValue: Theme)

    suspend fun getPrintSettings(): PrintSettings

    suspend fun setPrintSettings(newValue: PrintSettings)
}