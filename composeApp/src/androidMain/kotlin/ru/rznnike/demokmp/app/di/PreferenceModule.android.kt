package ru.rznnike.demokmp.app.di

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import ru.rznnike.demokmp.data.preference.PreferencesManager

internal actual fun getPreferenceModule() = module {
    factory { PreferenceManager.getDefaultSharedPreferences(androidApplication()) }
    single { createSettings(get()) }
    single { PreferencesManager(get()) }
}

private fun createSettings(preferences: SharedPreferences): Settings {
    return SharedPreferencesSettings(preferences)
}