package ru.rznnike.demokmp.data.utils

import ru.rznnike.demokmp.BuildKonfig

object DataConstants {
    @Suppress("KotlinConstantConditions")
    val ROOT_DIR = when {
        BuildKonfig.DEBUG -> "workDirectory"
        BuildKonfig.OS == "windows" -> ".."
        else -> "../.."
    }
    val APP_DATA_DIR = "${ROOT_DIR}/data"

    val NETWORK_CACHE_PATH = "$APP_DATA_DIR/networkCache"
    val PREFERENCES_PATH = "$APP_DATA_DIR/preferences/settings.preferences_pb"
    val DB_PATH = "$APP_DATA_DIR/database/demokmp_main.db"
}