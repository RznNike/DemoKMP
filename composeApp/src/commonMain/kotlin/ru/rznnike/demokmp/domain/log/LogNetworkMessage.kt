package ru.rznnike.demokmp.domain.log

import kotlinx.serialization.Serializable
import ru.rznnike.demokmp.domain.utils.UUIDSerializer
import java.util.*

@Serializable
data class LogNetworkMessage(
    @Serializable(with = UUIDSerializer::class)
    val uuid: UUID,
    val request: LogMessage,
    val response: LogMessage? = null,
    val state: NetworkRequestState = NetworkRequestState.SENT,
    val isCurrentSession: Boolean = true
)