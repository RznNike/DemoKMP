package ru.rznnike.demokmp.data.preference

import com.russhwolf.settings.Settings
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme

class PreferencesManager(
    private val settings: Settings
) {
    val testCounter = Preference.IntPreference(settings, "TEST_COUNTER")
    val language = Preference.LanguagePreference(settings, "LANGUAGE", Language.default)
    val theme = Preference.ThemePreference(settings, "THEME", Theme.default)

    @Suppress("unused")
    fun clearAll() = settings.clear()
}