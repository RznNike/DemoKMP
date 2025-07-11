package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.data.storage.dao.LogMessageDao
import ru.rznnike.demokmp.data.storage.dao.LogNetworkMessageDao
import ru.rznnike.demokmp.data.storage.entity.toLogMessageEntity
import ru.rznnike.demokmp.data.storage.entity.toLogNetworkMessageEntity
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.LogGateway
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogNetworkMessage

class LogGatewayImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val logMessageDao: LogMessageDao,
    private val logNetworkMessageDao: LogNetworkMessageDao
) : LogGateway {
    override suspend fun addLogMessageToDB(message: LogMessage) = withContext(dispatcherProvider.io) {
        logMessageDao.add(message.toLogMessageEntity())
    }

    override suspend fun addLogNetworkMessageToDB(message: LogNetworkMessage) = withContext(dispatcherProvider.io) {
        logNetworkMessageDao.add(message.toLogNetworkMessageEntity())
    }
}