package ru.rznnike.demokmp.domain.log.extension

import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.formatLogMessage
import ru.rznnike.demokmp.domain.utils.*
import java.io.File
import java.time.LocalDate

class FileLoggerExtension : LoggerExtension() {
    private val outputLock = Semaphore(1)
    private var logFile: File? = null
    private var logFileDate: LocalDate? = null

    override fun addMessage(
        tag: String,
        message: String,
        level: LogLevel,
        type: LogType,
        callback: suspend (LogMessage) -> Unit
    ) {
        val logMessage = LogMessage(
            type = type,
            level = level,
            timestamp = clock.millis(),
            tag = tag,
            message = message
        )

        coroutineScope.launch {
            outputLock.withPermit {
                try {
                    val currentDate = clock.millis().toLocalDate()
                    if (logFileDate != currentDate) {
                        logFileDate = currentDate
                        logFile = null
                    }

                    if (logFile == null) {
                        File(DataConstants.LOGS_PATH).mkdirs()
                        val logFileName = "${currentDate.millis().toDateString(GlobalConstants.DATE_PATTERN_FILE_NAME_DAY)}.txt"
                        logFile = File("${DataConstants.LOGS_PATH}/$logFileName")
                    }
                    logFile?.appendText(formatLogMessage(logMessage))
                    logFile?.appendText("\n")
                } catch (_: Exception) { }
                callback(logMessage)
            }
        }
    }
}