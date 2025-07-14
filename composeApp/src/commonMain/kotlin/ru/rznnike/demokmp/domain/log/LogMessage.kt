package ru.rznnike.demokmp.domain.log

import kotlinx.serialization.Serializable

@Serializable
data class LogMessage(
    val type: LogType,
    val level: LogLevel,
    val timestamp: Long,
    val tag: String,
    val message: String,
    val isCurrentSession: Boolean = true
) {
    fun getFormattedMessage() = message.trim().replace("\t", "    ")
}