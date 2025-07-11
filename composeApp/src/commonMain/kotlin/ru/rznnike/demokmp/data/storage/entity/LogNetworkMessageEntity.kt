package ru.rznnike.demokmp.data.storage.entity

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogNetworkMessage
import ru.rznnike.demokmp.domain.log.NetworkRequestState
import java.util.*

@Entity
data class LogNetworkMessageEntity(
    @PrimaryKey
    val uuid: UUID,
    @Embedded(prefix = "request_")
    val request: LogMessage,
    @Embedded(prefix = "response_")
    val response: LogMessage? = null,
    val state: NetworkRequestState = NetworkRequestState.SENT
)

fun LogNetworkMessageEntity.toLogNetworkMessage() = LogNetworkMessage(
    uuid = uuid,
    request = request,
    response = response,
    state = state
)

fun LogNetworkMessage.toLogNetworkMessageEntity() = LogNetworkMessageEntity(
    uuid = uuid,
    request = request,
    response = response,
    state = state
)