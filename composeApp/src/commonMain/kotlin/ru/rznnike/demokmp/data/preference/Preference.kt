package ru.rznnike.demokmp.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import kotlinx.coroutines.flow.first

sealed class Preference<Type>(
    private val dataStore: DataStore<Preferences>,
    private val key: String,
    private val defaultValue: Type
) {
    abstract val typedKey: Preferences.Key<Type>

    suspend fun get(): Type = dataStore.data.first()[typedKey] ?: defaultValue

    suspend fun set(newValue: Type) = dataStore.edit { it[typedKey] = newValue }

    class IntPreference(
        dataStore: DataStore<Preferences>,
        key: String,
        defaultValue: Int = 0
    ) : Preference<Int>(dataStore, key, defaultValue) {
        override val typedKey = intPreferencesKey(key)
    }
}