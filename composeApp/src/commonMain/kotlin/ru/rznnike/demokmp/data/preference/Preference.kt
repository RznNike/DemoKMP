package ru.rznnike.demokmp.data.preference

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme
import ru.rznnike.demokmp.domain.utils.toDoubleOrNullSmart

sealed class Preference<Type>(
    private val settings: Settings,
    private val key: String,
    private val defaultValue: Type
) {
    protected abstract fun Type.serialize(): String

    protected abstract fun String.deserialize(): Type?

    fun get(): Type = settings.getStringOrNull(key)?.deserialize() ?: defaultValue

    fun set(newValue: Type) {
        settings[key] = newValue.serialize()
    }

    class StringPreference(
        settings: Settings,
        key: String,
        defaultValue: String = ""
    ) : Preference<String>(settings, key, defaultValue) {
        override fun String.serialize() = this

        override fun String.deserialize() = this
    }

    class IntPreference(
        settings: Settings,
        key: String,
        defaultValue: Int = 0
    ) : Preference<Int>(settings, key, defaultValue) {
        override fun Int.serialize() = toString()

        override fun String.deserialize() = toIntOrNull()
    }

    class LongPreference(
        settings: Settings,
        key: String,
        defaultValue: Long = 0
    ) : Preference<Long>(settings, key, defaultValue) {
        override fun Long.serialize() = toString()

        override fun String.deserialize() = toLongOrNull()
    }

    class DoublePreference(
        settings: Settings,
        key: String,
        defaultValue: Double = 0.0
    ) : Preference<Double>(settings, key, defaultValue) {
        override fun Double.serialize() = toString()

        override fun String.deserialize() = toDoubleOrNullSmart()
    }

    class BooleanPreference(
        settings: Settings,
        key: String,
        defaultValue: Boolean = false
    ) : Preference<Boolean>(settings, key, defaultValue) {
        override fun Boolean.serialize() = toString()

        override fun String.deserialize() = toBooleanStrictOrNull()
    }

    class LanguagePreference(
        settings: Settings,
        key: String,
        defaultValue: Language
    ) : Preference<Language>(settings, key, defaultValue) {
        override fun Language.serialize() = tag

        override fun String.deserialize() = Language[this]
    }

    class ThemePreference(
        settings: Settings,
        key: String,
        defaultValue: Theme
    ) : Preference<Theme>(settings, key, defaultValue) {
        override fun Theme.serialize() = id.toString()

        override fun String.deserialize() = Theme[toIntOrNull() ?: 0]
    }
}