package ru.rznnike.demokmp.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import ru.rznnike.demokmp.domain.model.common.Language

class PreferencesManager(
    private val dataStore: DataStore<Preferences>
) {
    val testCounter = Preference.IntPreference(dataStore, "TEST_COUNTER")
    val language = Preference.LanguagePreference(dataStore, "LANGUAGE", Language.default)

    suspend fun clearAll() = dataStore.edit { it.clear() }
}