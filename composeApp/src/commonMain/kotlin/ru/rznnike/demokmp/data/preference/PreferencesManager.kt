package ru.rznnike.demokmp.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme

class PreferencesManager(
    private val dataStore: DataStore<Preferences>
) {
    val testCounter = Preference.IntPreference(dataStore, "TEST_COUNTER")
    val language = Preference.LanguagePreference(dataStore, "LANGUAGE", Language.default)
    val theme = Preference.ThemePreference(dataStore, "THEME", Theme.default)

    suspend fun clearAll() = dataStore.edit { it.clear() }
}