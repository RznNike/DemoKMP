package ru.rznnike.demokmp.app.di

import com.russhwolf.settings.PropertiesSettings
import com.russhwolf.settings.Settings
import org.koin.dsl.module
import ru.rznnike.demokmp.data.preference.PreferencesManager
import ru.rznnike.demokmp.data.utils.DataConstants
import java.io.File
import java.util.*

internal actual fun getPreferenceModule() = module {
    single { createSettings() }
    single { PreferencesManager(get()) }
}

private fun createSettings(): Settings {
    val propertiesFile = File(DataConstants.PREFERENCES_PATH)
    val properties = Properties().also { properties ->
        if (propertiesFile.exists()) {
            try {
                propertiesFile.inputStream().use {
                    properties.load(it)
                }
            } catch (_: Exception) {}
        } else {
            File(DataConstants.PREFERENCES_FOLDER_PATH).mkdirs()
        }
    }
    return PropertiesSettings(properties) { props ->
        propertiesFile.outputStream().use {
            props.store(it, null)
        }
    }
}