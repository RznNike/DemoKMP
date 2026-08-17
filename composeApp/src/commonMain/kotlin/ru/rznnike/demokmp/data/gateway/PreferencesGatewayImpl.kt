package ru.rznnike.demokmp.data.gateway

import ru.rznnike.demokmp.data.preference.PreferencesManager
import ru.rznnike.demokmp.domain.gateway.PreferencesGateway
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme
import ru.rznnike.demokmp.domain.model.common.UiScale
import ru.rznnike.demokmp.domain.model.print.PrintSettings

class PreferencesGatewayImpl(
    private val preferencesManager: PreferencesManager
) : PreferencesGateway {
    override suspend fun getTestCounter() = preferencesManager.testCounter.get()

    override suspend fun setTestCounter(newValue: Int) = preferencesManager.testCounter.set(newValue)

    override suspend fun getLanguage() = preferencesManager.language.get()

    override suspend fun setLanguage(newValue: Language) = preferencesManager.language.set(newValue)

    override suspend fun getTheme() = preferencesManager.theme.get()

    override suspend fun setTheme(newValue: Theme) = preferencesManager.theme.set(newValue)

    override suspend fun getPrintSettings() = PrintSettings(
        printerName = preferencesManager.printerName.get(),
        twoSidedPrint = preferencesManager.twoSidedPrint.get()
    )

    override suspend fun setPrintSettings(newValue: PrintSettings) {
        preferencesManager.printerName.set(newValue.printerName)
        preferencesManager.twoSidedPrint.set(newValue.twoSidedPrint)
    }

    override suspend fun getUiScale() = preferencesManager.uiScale.get()

    override suspend fun setUiScale(newValue: UiScale) = preferencesManager.uiScale.set(newValue)
}