package ru.rznnike.demokmp.domain.log

import android.util.Log

actual fun formatLogMessage(message: LogMessage) = message.message

actual fun printLog(message: LogMessage) {
    val tag = message.tag.ifBlank { null }
    when (message.level) {
        LogLevel.DEBUG -> Log.d(tag, message.message)
        LogLevel.INFO -> Log.i(tag, message.message)
        LogLevel.WARNING -> Log.w(tag, message.message)
        LogLevel.ERROR -> Log.e(tag, message.message)
    }
}