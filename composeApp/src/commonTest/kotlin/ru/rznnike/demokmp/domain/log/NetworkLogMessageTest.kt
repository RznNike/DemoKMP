package ru.rznnike.demokmp.domain.log

import ru.rznnike.demokmp.domain.utils.GlobalConstants
import ru.rznnike.demokmp.domain.utils.toDateString
import java.util.*
import kotlin.test.Test
import kotlin.test.assertEquals

private const val REQUEST_MILLIS = 1_700_000_000_000L
private const val RESPONSE_MILLIS = 1_700_000_001_000L

private fun Long.timeString(): String = toDateString(GlobalConstants.DATE_PATTERN_TIME_MS)

private fun logMessage(timestamp: Long, text: String) = LogMessage(
    type = LogType.NETWORK,
    level = LogLevel.INFO,
    timestamp = timestamp,
    tag = "Tag",
    message = text
)

private fun networkMessage(request: LogMessage, response: LogMessage? = null) = NetworkLogMessage(
    uuid = UUID.randomUUID(),
    request = request,
    response = response
)

class NetworkLogMessageTest {
    @Test
    fun `getFullText formats a request without a response`() {
        // Arrange
        val request = logMessage(REQUEST_MILLIS, "request")

        // Act
        val result = networkMessage(request).getFullText()

        // Assert
        assertEquals("${REQUEST_MILLIS.timeString()}\nrequest", result)
    }

    @Test
    fun `getFullText separates request and response with a blank line`() {
        // Arrange
        val request = logMessage(REQUEST_MILLIS, "request")
        val response = logMessage(RESPONSE_MILLIS, "response")

        // Act
        val result = networkMessage(request, response).getFullText()

        // Assert
        assertEquals(
            "${REQUEST_MILLIS.timeString()}\nrequest\n\n${RESPONSE_MILLIS.timeString()}\nresponse",
            result
        )
    }

    @Test
    fun `getFullText normalizes tabs in both messages`() {
        // Arrange
        val request = logMessage(REQUEST_MILLIS, "re\tquest")
        val response = logMessage(RESPONSE_MILLIS, "res\tponse")

        // Act
        val result = networkMessage(request, response).getFullText()

        // Assert
        assertEquals(
            "${REQUEST_MILLIS.timeString()}\nre    quest\n\n${RESPONSE_MILLIS.timeString()}\nres    ponse",
            result
        )
    }
}
