package ru.rznnike.demokmp.data.storage.entity

import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import ru.rznnike.demokmp.domain.log.NetworkLogMessage
import ru.rznnike.demokmp.domain.log.NetworkRequestState
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val CURRENT_SESSION_ID = 1_000L
private const val OTHER_SESSION_ID = 2_000L

class LogNetworkMessageEntityTest {
    private val uuid: UUID = UUID.fromString("6b3c2a1e-0f4d-4c7b-9a2e-1d3f5b7c9e01")

    private fun logMessage(
        id: Long = 1,
        type: LogType = LogType.NETWORK,
        level: LogLevel = LogLevel.INFO,
        timestamp: Long = 1_234_567L,
        tag: String = "Tag",
        message: String = "message",
        isCurrentSession: Boolean = true
    ) = LogMessage(
        id = id,
        type = type,
        level = level,
        timestamp = timestamp,
        tag = tag,
        message = message,
        isCurrentSession = isCurrentSession
    )

    private fun entity(
        id: Long = 42,
        uuid: UUID = this.uuid,
        request: LogMessage = logMessage(),
        response: LogMessage? = null,
        curl: String = "curl https://example.com",
        state: NetworkRequestState = NetworkRequestState.SENT,
        sessionId: Long = CURRENT_SESSION_ID
    ) = LogNetworkMessageEntity(
        id = id,
        uuid = uuid,
        request = request,
        response = response,
        curl = curl,
        state = state,
        sessionId = sessionId
    )

    private fun domainMessage(
        id: Long = 42,
        uuid: UUID = this.uuid,
        request: LogMessage = logMessage(),
        response: LogMessage? = null,
        curl: String = "curl https://example.com",
        state: NetworkRequestState = NetworkRequestState.SENT,
        isCurrentSession: Boolean = true
    ) = NetworkLogMessage(
        id = id,
        uuid = uuid,
        request = request,
        response = response,
        curl = curl,
        state = state,
        isCurrentSession = isCurrentSession
    )

    @Test
    fun `toLogNetworkMessage maps every field`() {
        // Arrange
        val entity = entity(
            id = 7,
            request = logMessage(id = 1, message = "request body"),
            response = logMessage(id = 2, message = "response body"),
            curl = "curl https://example.com/api",
            state = NetworkRequestState.SUCCESS
        )

        // Act
        val result = entity.toLogNetworkMessage(CURRENT_SESSION_ID)

        // Assert
        assertEquals(
            domainMessage(
                id = 7,
                request = logMessage(id = 1, message = "request body"),
                response = logMessage(id = 2, message = "response body"),
                curl = "curl https://example.com/api",
                state = NetworkRequestState.SUCCESS
            ),
            result
        )
    }

    @Test
    fun `toLogNetworkMessage marks a message of the current session as current`() {
        // Arrange
        val entity = entity(sessionId = CURRENT_SESSION_ID)

        // Act
        val result = entity.toLogNetworkMessage(CURRENT_SESSION_ID)

        // Assert
        assertTrue(result.isCurrentSession)
    }

    @Test
    fun `toLogNetworkMessage marks a message of another session as not current`() {
        // Arrange
        val entity = entity(sessionId = OTHER_SESSION_ID)

        // Act
        val result = entity.toLogNetworkMessage(CURRENT_SESSION_ID)

        // Assert
        assertFalse(result.isCurrentSession)
    }

    @Test
    fun `toLogNetworkMessage propagates the current session flag to the request`() {
        // Arrange
        val entity = entity(
            request = logMessage(message = "request body"),
            sessionId = OTHER_SESSION_ID
        )

        // Act
        val result = entity.toLogNetworkMessage(CURRENT_SESSION_ID)

        // Assert
        assertEquals(
            logMessage(message = "request body", isCurrentSession = false),
            result.request
        )
    }

    @Test
    fun `toLogNetworkMessage propagates the current session flag to the response`() {
        // Arrange
        val entity = entity(
            request = logMessage(message = "request body"),
            response = logMessage(id = 2, message = "response body"),
            sessionId = OTHER_SESSION_ID
        )

        // Act
        val result = entity.toLogNetworkMessage(CURRENT_SESSION_ID)

        // Assert
        assertEquals(
            logMessage(id = 2, message = "response body", isCurrentSession = false),
            result.response
        )
    }

    @Test
    fun `toLogNetworkMessage overrides the current session flag stored in the request`() {
        // Arrange: the embedded flag says "not current" but the entity session id says otherwise
        val entity = entity(
            request = logMessage(isCurrentSession = false),
            sessionId = CURRENT_SESSION_ID
        )

        // Act
        val result = entity.toLogNetworkMessage(CURRENT_SESSION_ID)

        // Assert
        assertEquals(logMessage(isCurrentSession = true), result.request)
    }

    @Test
    fun `toLogNetworkMessage overrides the current session flag stored in the response`() {
        // Arrange: the embedded flag says "not current" but the entity session id says otherwise
        val entity = entity(
            response = logMessage(id = 2, isCurrentSession = false),
            sessionId = CURRENT_SESSION_ID
        )

        // Act
        val result = entity.toLogNetworkMessage(CURRENT_SESSION_ID)

        // Assert
        assertEquals(logMessage(id = 2, isCurrentSession = true), result.response)
    }

    @Test
    fun `toLogNetworkMessage keeps a null response null`() {
        // Arrange
        val entity = entity(response = null)

        // Act
        val result = entity.toLogNetworkMessage(CURRENT_SESSION_ID)

        // Assert
        assertNull(result.response)
    }

    @Test
    fun `toLogNetworkMessageEntity maps every field`() {
        // Arrange
        val message = domainMessage(
            id = 7,
            request = logMessage(id = 1, message = "request body"),
            response = logMessage(id = 2, message = "response body"),
            curl = "curl https://example.com/api",
            state = NetworkRequestState.SUCCESS
        )

        // Act
        val result = message.toLogNetworkMessageEntity(CURRENT_SESSION_ID)

        // Assert
        assertEquals(
            entity(
                id = 7,
                request = logMessage(id = 1, message = "request body"),
                response = logMessage(id = 2, message = "response body"),
                curl = "curl https://example.com/api",
                state = NetworkRequestState.SUCCESS
            ),
            result
        )
    }

    @Test
    fun `toLogNetworkMessageEntity stores the given session id`() {
        // Arrange
        val message = domainMessage()

        // Act
        val result = message.toLogNetworkMessageEntity(OTHER_SESSION_ID)

        // Assert
        assertEquals(OTHER_SESSION_ID, result.sessionId)
    }

    @Test
    fun `toLogNetworkMessageEntity keeps the current session flag of the request`() {
        // Arrange: unlike the entity to domain direction, this direction does not touch the flags
        val message = domainMessage(request = logMessage(isCurrentSession = false))

        // Act
        val result = message.toLogNetworkMessageEntity(CURRENT_SESSION_ID)

        // Assert
        assertEquals(logMessage(isCurrentSession = false), result.request)
    }

    @Test
    fun `toLogNetworkMessageEntity keeps the current session flag of the response`() {
        // Arrange: unlike the entity to domain direction, this direction does not touch the flags
        val message = domainMessage(response = logMessage(id = 2, isCurrentSession = false))

        // Act
        val result = message.toLogNetworkMessageEntity(CURRENT_SESSION_ID)

        // Assert
        assertEquals(logMessage(id = 2, isCurrentSession = false), result.response)
    }

    @Test
    fun `round trip through the entity preserves the message content`() {
        // Arrange
        val original = entity(
            id = 7,
            request = logMessage(id = 1, message = "request body"),
            response = logMessage(id = 2, message = "response body"),
            curl = "curl https://example.com/api",
            state = NetworkRequestState.SUCCESS
        )

        // Act
        val result = original
            .toLogNetworkMessage(CURRENT_SESSION_ID)
            .toLogNetworkMessageEntity(CURRENT_SESSION_ID)

        // Assert
        assertEquals(original.copy(sessionId = CURRENT_SESSION_ID), result)
    }

    @Test
    fun `round trip through the entity re-stamps the session id with the current one`() {
        // Arrange
        val original = entity(sessionId = OTHER_SESSION_ID)

        // Act
        val result = original
            .toLogNetworkMessage(CURRENT_SESSION_ID)
            .toLogNetworkMessageEntity(CURRENT_SESSION_ID)

        // Assert
        assertEquals(CURRENT_SESSION_ID, result.sessionId)
    }
}
