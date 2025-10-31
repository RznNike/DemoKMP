package ru.rznnike.demokmp.data.storage.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.NetworkLogMessage
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

fun LogNetworkMessageEntity.toLogNetworkMessage(currentSessionId: Long): NetworkLogMessage {
    val isCurrentSession = currentSessionId == sessionId
    return NetworkLogMessage(
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

fun NetworkLogMessage.toLogNetworkMessageEntity(currentSessionId: Long) = LogNetworkMessageEntity(
    id = id,
    uuid = uuid,
    request = request,
    response = response,
    state = state,
    sessionId = currentSessionId
)