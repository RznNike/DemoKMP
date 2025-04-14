package ru.rznnike.demokmp.domain.log

import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.toDateString

actual fun formatLogMessage(message: LogMessage) =
    "%s | %s%s | %s".format(
        message.timestamp.toDateString(GlobalConstants.DATE_PATTERN_TIME_MS),
        message.level.label,
        if (message.tag.isNotBlank()) " | ${message.tag}" else "",
        message.message
    )

actual fun printLog(message: LogMessage) {
    val formattedMessage = formatLogMessage(message)
    if (message.level == LogLevel.ERROR) {
        System.err.println(formattedMessage)
    } else {
        println(formattedMessage)
    }
}