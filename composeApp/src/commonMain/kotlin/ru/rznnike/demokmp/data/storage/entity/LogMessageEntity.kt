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
    val message: String
)

fun LogMessageEntity.toLogMessage() = LogMessage(
    type = type,
    level = level,
    timestamp = timestamp,
    tag = tag,
    message = message
)

fun LogMessage.toLogMessageEntity() = LogMessageEntity(
    type = type,
    level = level,
    timestamp = timestamp,
    tag = tag,
    message = message
)