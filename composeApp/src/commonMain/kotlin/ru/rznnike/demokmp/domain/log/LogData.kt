package ru.rznnike.demokmp.domain.log

import kotlinx.coroutines.flow.Flow

data class LogData(
    val log: List<LogMessage>,
    val eventsFlow: Flow<LogEvent>
)