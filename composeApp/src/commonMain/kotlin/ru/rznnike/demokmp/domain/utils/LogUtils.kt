package ru.rznnike.demokmp.domain.utils

fun logger(message: String) = println(getLogMessageWithTime(message))

fun logger(exception: Throwable, message: String = "") {
    println(getLogMessageWithTime(message))
    println((exception.stackTraceToString()))
}

private fun getLogMessageWithTime(message: String) =
    "${currentTimeMillis().toDate(GlobalConstants.DATE_PATTERN_TIME_MS)} | $message"