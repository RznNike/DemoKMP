package ru.rznnike.demokmp.domain.log

import kotlinx.coroutines.flow.Flow

data class NetworkLogData(
    val log: List<LogNetworkMessage>,
    val eventsFlow: Flow<LogEvent>
)