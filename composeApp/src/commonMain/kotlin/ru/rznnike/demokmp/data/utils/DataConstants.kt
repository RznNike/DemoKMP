package ru.rznnike.demokmp.data.utils

import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.domain.utils.OperatingSystem

object DataConstants {
    val ROOT_DIR = when {
        BuildKonfig.DEBUG -> "workDirectory"
        OperatingSystem.isWindows -> ".."
        else -> "../.."
    }
    val APP_DATA_DIR = "${ROOT_DIR}/data"

    val NETWORK_CACHE_PATH = "$APP_DATA_DIR/networkCache"
    val PREFERENCES_PATH = "$APP_DATA_DIR/preferences/settings.preferences_pb"
    val DB_PATH = "$APP_DATA_DIR/database/demokmp_main.db"

    const val HEADER_TEST = "Test"
}