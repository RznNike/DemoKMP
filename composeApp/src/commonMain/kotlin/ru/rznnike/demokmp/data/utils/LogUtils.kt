package ru.rznnike.demokmp.data.utils

fun logT(message: String) = println(getLogMessageWithTime(message))

private fun getLogMessageWithTime(message: String): String {
    return "${System.currentTimeMillis()} $message" // TODO format data
}