package ru.rznnike.demokmp.data.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.utils.toDoubleOrNullSmart

sealed class Preference<Type>(
    private val dataStore: DataStore<Preferences>,
    key: String,
    private val defaultValue: Type
) {
    private val typedKey = stringPreferencesKey(key)

    protected abstract fun Type.serialize(): String

    protected abstract fun String.deserialize(): Type?

    suspend fun get(): Type = dataStore.data.first()[typedKey]?.deserialize() ?: defaultValue

    suspend fun set(newValue: Type) {
        dataStore.edit { it[typedKey] = newValue.serialize() }
    }

    class StringPreference(
        dataStore: DataStore<Preferences>,
        key: String,
        defaultValue: String = ""
    ) : Preference<String>(dataStore, key, defaultValue) {
        override fun String.serialize() = this

        override fun String.deserialize() = this
    }

    class IntPreference(
        dataStore: DataStore<Preferences>,
        key: String,
        defaultValue: Int = 0
    ) : Preference<Int>(dataStore, key, defaultValue) {
        override fun Int.serialize() = toString()

        override fun String.deserialize() = toIntOrNull()
    }

    class LongPreference(
        dataStore: DataStore<Preferences>,
        key: String,
        defaultValue: Long = 0
    ) : Preference<Long>(dataStore, key, defaultValue) {
        override fun Long.serialize() = toString()

        override fun String.deserialize() = toLongOrNull()
    }

    class DoublePreference(
        dataStore: DataStore<Preferences>,
        key: String,
        defaultValue: Double = 0.0
    ) : Preference<Double>(dataStore, key, defaultValue) {
        override fun Double.serialize() = toString()

        override fun String.deserialize() = toDoubleOrNullSmart()
    }

    class BooleanPreference(
        dataStore: DataStore<Preferences>,
        key: String,
        defaultValue: Boolean = false
    ) : Preference<Boolean>(dataStore, key, defaultValue) {
        override fun Boolean.serialize() = toString()

        override fun String.deserialize() = toBooleanStrictOrNull()
    }

    class LanguagePreference(
        dataStore: DataStore<Preferences>,
        key: String,
        defaultValue: Language
    ) : Preference<Language>(dataStore, key, defaultValue) {
        override fun Language.serialize() = tag

        override fun String.deserialize() = Language[this]
    }
}