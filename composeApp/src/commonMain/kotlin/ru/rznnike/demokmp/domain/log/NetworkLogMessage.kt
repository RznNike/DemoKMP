package ru.rznnike.demokmp.domain.log

import kotlinx.serialization.Serializable
import ru.rznnike.demokmp.data.utils.json.UUIDSerializable
import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.toDateString

@Serializable
data class NetworkLogMessage(
    val id: Long = 0,
    val uuid: UUIDSerializable,
    val request: LogMessage,
    val response: LogMessage? = null,
    val state: NetworkRequestState = NetworkRequestState.SENT,
    val isCurrentSession: Boolean = true
) {
    fun getFullText(): String {
        val stringBuilder = StringBuilder()
            .appendLine(request.timestamp.toDateString(GlobalConstants.DATE_PATTERN_TIME_MS))
            .append(request.getFormattedMessage())
        response?.let { response ->
            stringBuilder
                .appendLine()
                .appendLine()
                .appendLine(response.timestamp.toDateString(GlobalConstants.DATE_PATTERN_TIME_MS))
                .append(response.getFormattedMessage())
        }

        return stringBuilder.toString()
    }
}