package ru.rznnike.demokmp.data.preference

import com.russhwolf.settings.PropertiesSettings
import com.russhwolf.settings.Settings
import ru.rznnike.demokmp.data.utils.DataConstants
import java.io.File
import java.util.*

private val propertiesFile = File(DataConstants.PREFERENCES_PATH)

private val properties = Properties().also { properties ->
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

actual fun createSettings(): Settings = PropertiesSettings(properties) { properties ->
    propertiesFile.outputStream().use {
        properties.store(it, null)
    }
}