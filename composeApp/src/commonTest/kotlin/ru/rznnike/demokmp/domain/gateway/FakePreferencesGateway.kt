package ru.rznnike.demokmp.domain.gateway

import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme
import ru.rznnike.demokmp.domain.model.common.UiScale
import ru.rznnike.demokmp.domain.model.print.PrintSettings

/**
 * Test double for [PreferencesGateway].
 *
 * Every written value is recorded in the matching `set…Values` list, so tests can assert what was
 * forwarded to the gateway. [errorToThrow] makes the next call throw, so the `UseCaseResult.Error`
 * branch is reachable.
 */
class FakePreferencesGateway : PreferencesGateway {
    var errorToThrow: Exception? = null

    var testCounter: Int = 0
    var language: Language = Language.default
    var theme: Theme = Theme.default
    var printSettings: PrintSettings = PrintSettings()
    var uiScale: UiScale = UiScale.default

    val setTestCounterValues = mutableListOf<Int>()
    val setLanguageValues = mutableListOf<Language>()
    val setThemeValues = mutableListOf<Theme>()
    val setPrintSettingsValues = mutableListOf<PrintSettings>()
    val setUiScaleValues = mutableListOf<UiScale>()

    override suspend fun getTestCounter(): Int {
        errorToThrow?.let { throw it }
        return testCounter
    }

    override suspend fun setTestCounter(newValue: Int) {
        errorToThrow?.let { throw it }
        testCounter = newValue
        setTestCounterValues += newValue
    }

    override suspend fun getLanguage(): Language {
        errorToThrow?.let { throw it }
        return language
    }

    override suspend fun setLanguage(newValue: Language) {
        errorToThrow?.let { throw it }
        language = newValue
        setLanguageValues += newValue
    }

    override suspend fun getTheme(): Theme {
        errorToThrow?.let { throw it }
        return theme
    }

    override suspend fun setTheme(newValue: Theme) {
        errorToThrow?.let { throw it }
        theme = newValue
        setThemeValues += newValue
    }

    override suspend fun getPrintSettings(): PrintSettings {
        errorToThrow?.let { throw it }
        return printSettings
    }

    override suspend fun setPrintSettings(newValue: PrintSettings) {
        errorToThrow?.let { throw it }
        printSettings = newValue
        setPrintSettingsValues += newValue
    }

    override suspend fun getUiScale(): UiScale {
        errorToThrow?.let { throw it }
        return uiScale
    }

    override suspend fun setUiScale(newValue: UiScale) {
        errorToThrow?.let { throw it }
        uiScale = newValue
        setUiScaleValues += newValue
    }
}
