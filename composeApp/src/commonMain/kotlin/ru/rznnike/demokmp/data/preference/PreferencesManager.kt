package ru.rznnike.demokmp.data.preference

import com.russhwolf.settings.Settings
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme
import ru.rznnike.demokmp.domain.model.print.TwoSidedPrint

class PreferencesManager(
    private val settings: Settings
) {
    val testCounter = Preference.IntPreference(settings, "TEST_COUNTER")
    val language = Preference.LanguagePreference(settings, "LANGUAGE", Language.default)
    val theme = Preference.ThemePreference(settings, "THEME", Theme.default)
    val printerName = Preference.StringPreference(settings, "printerName")
    val twoSidedPrint = Preference.TwoSidedPrintPreference(settings, "twoSidedPrint", TwoSidedPrint.default)

    @Suppress("unused")
    fun clearAll() = settings.clear()
}