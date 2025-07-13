package ru.rznnike.demokmp.domain.gateway

import kotlinx.coroutines.flow.Flow
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogNetworkMessage
import java.util.UUID

interface LogGateway {
    suspend fun addLogMessageToDB(message: LogMessage)

    suspend fun addLogNetworkMessageToDB(message: LogNetworkMessage)

    suspend fun getLogNetworkMessage(uuid: UUID): LogNetworkMessage?

    suspend fun getLogNetworkMessageAsFlow(uuid: UUID): Flow<LogNetworkMessage?>

    suspend fun getLog(): Flow<List<LogMessage>>

    suspend fun getNetworkLog(): Flow<List<LogNetworkMessage>>

    suspend fun clearLog()

    suspend fun clearNetworkLog()
}