package ru.rznnike.demokmp.domain.gateway

import kotlinx.coroutines.flow.Flow
import ru.rznnike.demokmp.domain.log.LogData
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.NetworkLogMessage
import ru.rznnike.demokmp.domain.log.NetworkLogData
import ru.rznnike.demokmp.domain.log.extension.DatabaseLoggerExtension
import java.io.File
import java.util.UUID

interface LogGateway {
    suspend fun addLogMessageToDB(message: LogMessage)

    suspend fun addLogNetworkMessageToDB(message: NetworkLogMessage)

    suspend fun getLogNetworkMessage(uuid: UUID): NetworkLogMessage?

    suspend fun getLogNetworkMessageAsFlow(uuid: UUID): Flow<NetworkLogMessage?>

    suspend fun getLog(): LogData

    suspend fun getNewLog(lastId: Long): List<LogMessage>

    suspend fun getNetworkLog(): NetworkLogData

    suspend fun getNewNetworkLog(lastId: Long): List<NetworkLogMessage>

    suspend fun clearLog()

    suspend fun clearNetworkLog()

    suspend fun deleteOldLogs(logsRetentionMode: DatabaseLoggerExtension.LogsRetentionMode)

    suspend fun saveLogToFile(file: File)

    suspend fun saveNetworkLogMessageToFile(file: File, message: NetworkLogMessage)
}