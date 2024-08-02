package ru.rznnike.demokmp.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

class PreferencesManager(
    private val dataStore: DataStore<Preferences>
) {
    val testCounter = Preference.IntPreference(dataStore, "TEST_COUNTER")

    suspend fun clearAll() = dataStore.edit { it.clear() }
}