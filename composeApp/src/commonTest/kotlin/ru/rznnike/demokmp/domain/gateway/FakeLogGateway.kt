package ru.rznnike.demokmp.domain.gateway

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import ru.rznnike.demokmp.domain.log.LogData
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.NetworkLogData
import ru.rznnike.demokmp.domain.log.NetworkLogMessage
import ru.rznnike.demokmp.domain.log.extension.DatabaseLoggerExtension.LogsRetentionMode
import java.io.File
import java.util.UUID

/**
 * Test double for [LogGateway].
 *
 * Mutations are recorded in collections, so tests can assert what was forwarded to the gateway.
 * Readable state is exposed as a plain `var`, so tests can arrange a response without a stub framework.
 * [errorToThrow] makes the next call throw, so the `UseCaseResult.Error` branch is reachable.
 */
class FakeLogGateway : LogGateway {
    var errorToThrow: Exception? = null

    val requestedMessageUuids = mutableListOf<UUID>()
    var networkMessage: NetworkLogMessage? = null

    val flowRequestedUuids = mutableListOf<UUID>()
    var networkMessageFlow: Flow<NetworkLogMessage?> = emptyFlow()

    val savedNetworkMessages = mutableListOf<Pair<File, NetworkLogMessage>>()

    val addedMessages = mutableListOf<LogMessage>()
    val addedNetworkMessages = mutableListOf<NetworkLogMessage>()

    var logData: LogData = LogData(log = emptyList(), eventsFlow = emptyFlow())
    var networkLogData: NetworkLogData = NetworkLogData(log = emptyList(), eventsFlow = emptyFlow())

    val requestedLastLogIds = mutableListOf<Long>()
    val requestedLastNetworkLogIds = mutableListOf<Long>()
    var newLogMessages: List<LogMessage> = emptyList()
    var newNetworkLogMessages: List<NetworkLogMessage> = emptyList()

    var clearLogCalls = 0
    var clearNetworkLogCalls = 0
    val deletedLogsRetentionModes = mutableListOf<LogsRetentionMode>()
    val savedLogFiles = mutableListOf<File>()

    override suspend fun getLogNetworkMessage(uuid: UUID): NetworkLogMessage? {
        errorToThrow?.let { throw it }
        requestedMessageUuids += uuid
        return networkMessage
    }

    override suspend fun getLogNetworkMessageAsFlow(uuid: UUID): Flow<NetworkLogMessage?> {
        errorToThrow?.let { throw it }
        flowRequestedUuids += uuid
        return networkMessageFlow
    }

    override suspend fun saveNetworkLogMessageToFile(file: File, message: NetworkLogMessage) {
        errorToThrow?.let { throw it }
        savedNetworkMessages += file to message
    }

    override suspend fun addLogMessageToDB(message: LogMessage) {
        errorToThrow?.let { throw it }
        addedMessages += message
    }

    override suspend fun addLogNetworkMessageToDB(message: NetworkLogMessage) {
        errorToThrow?.let { throw it }
        addedNetworkMessages += message
    }

    override suspend fun getLog(): LogData {
        errorToThrow?.let { throw it }
        return logData
    }

    override suspend fun getNewLog(lastId: Long): List<LogMessage> {
        errorToThrow?.let { throw it }
        requestedLastLogIds += lastId
        return newLogMessages
    }

    override suspend fun getNetworkLog(): NetworkLogData {
        errorToThrow?.let { throw it }
        return networkLogData
    }

    override suspend fun getNewNetworkLog(lastId: Long): List<NetworkLogMessage> {
        errorToThrow?.let { throw it }
        requestedLastNetworkLogIds += lastId
        return newNetworkLogMessages
    }

    override suspend fun clearLog() {
        errorToThrow?.let { throw it }
        clearLogCalls++
    }

    override suspend fun clearNetworkLog() {
        errorToThrow?.let { throw it }
        clearNetworkLogCalls++
    }

    override suspend fun deleteOldLogs(logsRetentionMode: LogsRetentionMode) {
        errorToThrow?.let { throw it }
        deletedLogsRetentionModes += logsRetentionMode
    }

    override suspend fun saveLogToFile(file: File) {
        errorToThrow?.let { throw it }
        savedLogFiles += file
    }
}
