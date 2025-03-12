package ru.rznnike.demokmp.domain.log

import java.util.*

data class LogNetworkMessage(
    val uuid: UUID,
    val request: LogMessage,
    val response: LogMessage? = null,
    val state: NetworkRequestState = NetworkRequestState.SENT
)