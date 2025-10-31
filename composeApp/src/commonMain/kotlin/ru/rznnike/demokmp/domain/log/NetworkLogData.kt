package ru.rznnike.demokmp.domain.log

import kotlinx.coroutines.flow.Flow

data class NetworkLogData(
    val log: List<NetworkLogMessage>,
    val eventsFlow: Flow<LogEvent>
)