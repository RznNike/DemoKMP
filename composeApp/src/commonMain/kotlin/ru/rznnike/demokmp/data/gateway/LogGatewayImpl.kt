package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import okio.buffer
import okio.sink
import ru.rznnike.demokmp.data.storage.dao.LogMessageDao
import ru.rznnike.demokmp.data.storage.dao.LogNetworkMessageDao
import ru.rznnike.demokmp.data.storage.entity.toLogMessage
import ru.rznnike.demokmp.data.storage.entity.toLogMessageEntity
import ru.rznnike.demokmp.data.storage.entity.toLogNetworkMessage
import ru.rznnike.demokmp.data.storage.entity.toLogNetworkMessageEntity
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.LogGateway
import ru.rznnike.demokmp.domain.log.*
import ru.rznnike.demokmp.domain.log.extension.DatabaseLoggerExtension.LogsRetentionMode
import java.io.File
import java.time.Clock
import java.util.*
import kotlin.use

class LogGatewayImpl(
    private val clock: Clock,
    private val dispatcherProvider: DispatcherProvider,
    private val logMessageDao: LogMessageDao,
    private val logNetworkMessageDao: LogNetworkMessageDao
) : LogGateway {
    private val currentSessionId = clock.millis()

    private val logEventsFlow = MutableSharedFlow<LogEvent>()
    private val networkLogEventsFlow = MutableSharedFlow<LogEvent>()

    override suspend fun addLogMessageToDB(message: LogMessage) = withContext(dispatcherProvider.io) {
        logMessageDao.add(message.toLogMessageEntity(currentSessionId))
        logEventsFlow.emit(LogEvent.NewMessage)
    }

    override suspend fun addLogNetworkMessageToDB(message: NetworkLogMessage) = withContext(dispatcherProvider.io) {
        logNetworkMessageDao.add(message.toLogNetworkMessageEntity(currentSessionId))
        networkLogEventsFlow.emit(LogEvent.NewNetworkMessage(message))
    }

    override suspend fun getLogNetworkMessage(uuid: UUID): NetworkLogMessage? = withContext(dispatcherProvider.io) {
        logNetworkMessageDao.get(uuid)?.toLogNetworkMessage(currentSessionId)
    }

    override suspend fun getLogNetworkMessageAsFlow(uuid: UUID): Flow<NetworkLogMessage?> = withContext(dispatcherProvider.io) {
        logNetworkMessageDao.getAsFlow(uuid).map { it?.toLogNetworkMessage(currentSessionId) }
    }

    override suspend fun getLog(): LogData = withContext(dispatcherProvider.io) {
        LogData(
            log = logMessageDao.getAll().map { it.toLogMessage(currentSessionId) },
            eventsFlow = logEventsFlow.asSharedFlow()
        )
    }

    override suspend fun getNewLog(lastId: Long): List<LogMessage> = withContext(dispatcherProvider.io) {
        logMessageDao.getNew(lastId = lastId).map { it.toLogMessage(currentSessionId)}
    }

    override suspend fun getNetworkLog(): NetworkLogData = withContext(dispatcherProvider.io) {
        NetworkLogData(
            log = logNetworkMessageDao.getAll().map { it.toLogNetworkMessage(currentSessionId) },
            eventsFlow = networkLogEventsFlow.asSharedFlow()
        )
    }

    override suspend fun getNewNetworkLog(lastId: Long): List<NetworkLogMessage> = withContext(dispatcherProvider.io) {
        logNetworkMessageDao.getNew(lastId = lastId).map { it.toLogNetworkMessage(currentSessionId)}
    }

    override suspend fun clearLog() = withContext(dispatcherProvider.io) {
        logMessageDao.deleteAll()
        logEventsFlow.emit(LogEvent.Cleanup)
    }

    override suspend fun clearNetworkLog() = withContext(dispatcherProvider.io) {
        logNetworkMessageDao.deleteAll()
        networkLogEventsFlow.emit(LogEvent.Cleanup)
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
        logEventsFlow.emit(LogEvent.Cleanup)
        networkLogEventsFlow.emit(LogEvent.Cleanup)
    }

    override suspend fun saveLogToFile(file: File): Unit = withContext(dispatcherProvider.io) {
        val log = logMessageDao.getAll().map { it.toLogMessage(currentSessionId) }
        file.sink().buffer().use { writer ->
            log.filter { it.type != LogType.SESSION_START }
                .forEach { message ->
                    val text = formatLogMessage(message) + "\n"
                    writer.writeUtf8(text)
                }
        }
    }

    override suspend fun saveNetworkLogMessageToFile(file: File, message: NetworkLogMessage): Unit = withContext(dispatcherProvider.io) {
        file.writeText(message.getFullText())
    }
}