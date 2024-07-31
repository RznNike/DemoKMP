package ru.rznnike.demokmp.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first

class PreferencesManager(
    private val preferences: DataStore<Preferences>
) {
    private val testCounterKey = intPreferencesKey("TEST_COUNTER")

    suspend fun getTestCounter() = preferences.data.first()[testCounterKey] ?: 0
    suspend fun setTestCounter(testCounter: Int) = preferences.edit { it[testCounterKey] = testCounter }
}