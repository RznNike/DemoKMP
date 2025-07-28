package ru.rznnike.demokmp.domain.gateway

import kotlinx.coroutines.flow.Flow
import ru.rznnike.demokmp.domain.log.LogData
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogNetworkMessage
import ru.rznnike.demokmp.domain.log.NetworkLogData
import ru.rznnike.demokmp.domain.log.extension.DatabaseLoggerExtension
import java.util.UUID

interface LogGateway {
    suspend fun addLogMessageToDB(message: LogMessage)

    suspend fun addLogNetworkMessageToDB(message: LogNetworkMessage)

    suspend fun getLogNetworkMessage(uuid: UUID): LogNetworkMessage?

    suspend fun getLogNetworkMessageAsFlow(uuid: UUID): Flow<LogNetworkMessage?>

    suspend fun getLog(): LogData

    suspend fun getNewLog(lastId: Long): List<LogMessage>

    suspend fun getNetworkLog(): NetworkLogData

    suspend fun getNewNetworkLog(lastId: Long): List<LogNetworkMessage>

    suspend fun clearLog()

    suspend fun clearNetworkLog()

    suspend fun deleteOldLogs(logsRetentionMode: DatabaseLoggerExtension.LogsRetentionMode)
}