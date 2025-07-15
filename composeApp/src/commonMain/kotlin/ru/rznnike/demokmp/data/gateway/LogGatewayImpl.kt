package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.data.storage.dao.LogMessageDao
import ru.rznnike.demokmp.data.storage.dao.LogNetworkMessageDao
import ru.rznnike.demokmp.data.storage.entity.toLogMessage
import ru.rznnike.demokmp.data.storage.entity.toLogMessageEntity
import ru.rznnike.demokmp.data.storage.entity.toLogNetworkMessage
import ru.rznnike.demokmp.data.storage.entity.toLogNetworkMessageEntity
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.LogGateway
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogNetworkMessage
import ru.rznnike.demokmp.domain.log.extension.DatabaseLoggerExtension.LogsRetentionMode
import java.time.Clock
import java.util.*

class LogGatewayImpl(
    private val clock: Clock,
    private val dispatcherProvider: DispatcherProvider,
    private val logMessageDao: LogMessageDao,
    private val logNetworkMessageDao: LogNetworkMessageDao
) : LogGateway {
    private val currentSessionId = clock.millis()

    override suspend fun addLogMessageToDB(message: LogMessage) = withContext(dispatcherProvider.io) {
        logMessageDao.add(message.toLogMessageEntity(currentSessionId))
    }

    override suspend fun addLogNetworkMessageToDB(message: LogNetworkMessage) = withContext(dispatcherProvider.io) {
        logNetworkMessageDao.add(message.toLogNetworkMessageEntity(currentSessionId))
    }

    override suspend fun getLogNetworkMessage(uuid: UUID): LogNetworkMessage? = withContext(dispatcherProvider.io) {
        logNetworkMessageDao.get(uuid)?.toLogNetworkMessage(currentSessionId)
    }

    override suspend fun getLogNetworkMessageAsFlow(uuid: UUID): Flow<LogNetworkMessage?> = withContext(dispatcherProvider.io) {
        logNetworkMessageDao.getAsFlow(uuid).map { it?.toLogNetworkMessage(currentSessionId) }
    }

    override suspend fun getLog(): Flow<List<LogMessage>> = withContext(dispatcherProvider.io) {
        logMessageDao.getAll().map { list ->
            list.map { it.toLogMessage(currentSessionId) }
        }
    }

    override suspend fun getNetworkLog(): Flow<List<LogNetworkMessage>> = withContext(dispatcherProvider.io) {
        logNetworkMessageDao.getAll().map { list ->
            list.map { it.toLogNetworkMessage(currentSessionId) }
        }
    }

    override suspend fun clearLog() = withContext(dispatcherProvider.io) {
        logMessageDao.deleteAll()
    }

    override suspend fun clearNetworkLog() = withContext(dispatcherProvider.io) {
        logNetworkMessageDao.deleteAll()
    }

    override suspend fun deleteOldLogs(logsRetentionMode: LogsRetentionMode): Unit = withContext(dispatcherProvider.io) {
        when (logsRetentionMode) {
            LogsRetentionMode.All -> Unit
            is LogsRetentionMode.LastNSessions -> {
                val sessionsCount = logsRetentionMode.sessionsCount.coerceAtLeast(1)
                logMessageDao.getSessionIds()
                    .sortedDescending()
                    .getOrNull(sessionsCount - 1)
                    ?.let { borderSessionId ->
                        logMessageDao.deleteOldBySessionId(borderSessionId = borderSessionId)
                        logNetworkMessageDao.deleteOldBySessionId(borderSessionId = borderSessionId)
                    }
            }
            is LogsRetentionMode.LastNMessages -> {
                logMessageDao.getNthId(offset = logsRetentionMode.messagesCount)?.let { borderId ->
                    logMessageDao.deleteOldById(borderId = borderId)
                }
                logNetworkMessageDao.getNthId(offset = logsRetentionMode.networkMessagesCount)?.let { borderId ->
                    logNetworkMessageDao.deleteOldById(borderId = borderId)
                }
            }
            is LogsRetentionMode.TimePeriod -> {
                val borderTimestamp = clock.millis() - logsRetentionMode.logsRetentionMs
                logMessageDao.deleteOldByTimestamp(borderTimestamp = borderTimestamp)
                logNetworkMessageDao.deleteOldByTimestamp(borderTimestamp = borderTimestamp)
            }
        }
    }
}