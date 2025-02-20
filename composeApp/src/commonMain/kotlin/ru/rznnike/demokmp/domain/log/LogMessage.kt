package ru.rznnike.demokmp.domain.log

data class LogMessage(
    val type: LogType,
    val level: LogLevel,
    val timestamp: Long,
    val tag: String,
    val message: String
) {
    fun getFormattedMessage() = message.trim().replace("\t", "    ")
}