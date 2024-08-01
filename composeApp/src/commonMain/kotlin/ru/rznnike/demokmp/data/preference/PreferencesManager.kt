package ru.rznnike.demokmp.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

class PreferencesManager(
    private val dataStore: DataStore<Preferences>
) {
    val testCounter = Preference.IntPreference(dataStore, "TEST_COUNTER")
    val testCounter2 = Preference.IntPreference(dataStore, "TEST_COUNTER2", -1)

    suspend fun clearAll() = dataStore.edit { it.clear() }
}