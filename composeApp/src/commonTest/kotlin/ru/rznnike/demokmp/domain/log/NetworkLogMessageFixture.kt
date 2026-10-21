package ru.rznnike.demokmp.domain.log

import java.util.UUID

internal fun testNetworkLogMessage(
    message: String = "request",
    timestamp: Long = 0L
) = NetworkLogMessage(
    uuid = UUID.randomUUID(),
    request = LogMessage(
        type = LogType.NETWORK,
        level = LogLevel.INFO,
        timestamp = timestamp,
        tag = "Tag",
        message = message
    )
)
