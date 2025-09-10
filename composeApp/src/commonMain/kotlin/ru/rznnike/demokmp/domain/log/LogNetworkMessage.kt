package ru.rznnike.demokmp.domain.log

import kotlinx.serialization.Serializable
import ru.rznnike.demokmp.data.utils.json.UUIDSerializable

@Serializable
data class LogNetworkMessage(
    val id: Long = 0,
    val uuid: UUIDSerializable,
    val request: LogMessage,
    val response: LogMessage? = null,
    val state: NetworkRequestState = NetworkRequestState.SENT,
    val isCurrentSession: Boolean = true
)