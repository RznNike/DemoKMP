package ru.rznnike.demokmp.data.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType

@Entity
data class LogMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val type: LogType,
    val level: LogLevel,
    val timestamp: Long,
    val tag: String,
    val message: String,
    val sessionId: Long
)

fun LogMessageEntity.toLogMessage(currentSessionId: Long) = LogMessage(
    type = type,
    level = level,
    timestamp = timestamp,
    tag = tag,
    message = message,
    isCurrentSession = currentSessionId == sessionId
)

fun LogMessage.toLogMessageEntity(currentSessionId: Long) = LogMessageEntity(
    type = type,
    level = level,
    timestamp = timestamp,
    tag = tag,
    message = message,
    sessionId = currentSessionId
)