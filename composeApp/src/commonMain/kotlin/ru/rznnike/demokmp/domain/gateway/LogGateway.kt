package ru.rznnike.demokmp.domain.gateway

import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogNetworkMessage

interface LogGateway {
    suspend fun addLogMessageToDB(message: LogMessage)

    suspend fun addLogNetworkMessageToDB(message: LogNetworkMessage)
}