package ru.rznnike.demokmp.data.utils

import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.domain.utils.OperatingSystem

object DataConstants {
    val ROOT_DIR = if (BuildKonfig.RUN_FROM_IDE) "workDirectory" else "."
    private val APP_DATA_DIR = "$ROOT_DIR/data"

    val NETWORK_CACHE_PATH = "$APP_DATA_DIR/networkCache"
    val PREFERENCES_FOLDER_PATH = "$APP_DATA_DIR/preferences"
    val PREFERENCES_PATH = "$PREFERENCES_FOLDER_PATH/settings.properties"
    val DB_PATH = "$APP_DATA_DIR/database/demokmp_main.db"

    const val LAUNCHER_CONFIGURATION_NAME = "launcher_configuration.ini"
    val LAUNCHER_CONFIGURATION_PATH = "$ROOT_DIR/$LAUNCHER_CONFIGURATION_NAME"

    val LOGS_PATH = "$ROOT_DIR/logs"
    const val LOG_FILE_NAME_TEMPLATE = "log_%s.txt"

    val RUN_SCRIPT_NAME = if (OperatingSystem.isWindows) "run.vbs" else "run.sh"

    const val HEADER_TEST = "Test"

    val TEST_PDF_PATH = "$ROOT_DIR/sample.pdf"
}