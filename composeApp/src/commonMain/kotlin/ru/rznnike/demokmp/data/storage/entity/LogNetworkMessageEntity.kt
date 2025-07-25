package ru.rznnike.demokmp.data.storage.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogNetworkMessage
import ru.rznnike.demokmp.domain.log.NetworkRequestState
import java.util.*

@Entity
data class LogNetworkMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(index = true)
    val uuid: UUID,
    @Embedded(prefix = "request_")
    val request: LogMessage,
    @Embedded(prefix = "response_")
    val response: LogMessage? = null,
    val state: NetworkRequestState = NetworkRequestState.SENT,
    val sessionId: Long
)

fun LogNetworkMessageEntity.toLogNetworkMessage(currentSessionId: Long): LogNetworkMessage {
    val isCurrentSession = currentSessionId == sessionId
    return LogNetworkMessage(
        id = id,
        uuid = uuid,
        request = request.copy(
            isCurrentSession = isCurrentSession
        ),
        response = response?.copy(
            isCurrentSession = isCurrentSession
        ),
        state = state,
        isCurrentSession = currentSessionId == sessionId
    )
}

fun LogNetworkMessage.toLogNetworkMessageEntity(currentSessionId: Long) = LogNetworkMessageEntity(
    id = id,
    uuid = uuid,
    request = request,
    response = response,
    state = state,
    sessionId = currentSessionId
)