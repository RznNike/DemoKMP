package ru.rznnike.demokmp.domain.gateway

import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogNetworkMessage
import java.util.UUID

interface LogGateway {
    suspend fun addLogMessageToDB(message: LogMessage)

    suspend fun addLogNetworkMessageToDB(message: LogNetworkMessage)

    suspend fun getLogNetworkMessage(uuid: UUID): LogNetworkMessage?
}