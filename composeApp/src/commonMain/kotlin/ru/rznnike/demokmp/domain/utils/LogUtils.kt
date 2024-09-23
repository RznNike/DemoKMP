package ru.rznnike.demokmp.domain.utils

import ru.rznnike.demokmp.BuildKonfig

fun logger(message: String) {
    if (BuildKonfig.DEBUG) {
        println(getLogMessageWithTime(message))
    }
}

fun logger(exception: Throwable, message: String = "") {
    if (BuildKonfig.DEBUG) {
        println(getLogMessageWithTime(message))
        println((exception.stackTraceToString()))
    }
}

private fun getLogMessageWithTime(message: String) =
    "${currentTimeMillis().toDate(GlobalConstants.DATE_PATTERN_TIME_MS)} | $message"