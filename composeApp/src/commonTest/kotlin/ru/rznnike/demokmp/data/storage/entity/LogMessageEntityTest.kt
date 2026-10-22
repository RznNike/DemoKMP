package ru.rznnike.demokmp.data.storage.entity

import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.LogMessage
import ru.rznnike.demokmp.domain.log.LogType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val CURRENT_SESSION_ID = 1_000L
private const val OTHER_SESSION_ID = 2_000L

class LogMessageEntityTest {
    private fun entity(
        id: Long = 42,
        type: LogType = LogType.DEFAULT,
        level: LogLevel = LogLevel.INFO,
        timestamp: Long = 1_234_567L,
        tag: String = "Tag",
        message: String = "message",
        sessionId: Long = CURRENT_SESSION_ID
    ) = LogMessageEntity(
        id = id,
        type = type,
        level = level,
        timestamp = timestamp,
        tag = tag,
        message = message,
        sessionId = sessionId
    )

    private fun domainMessage(
        id: Long = 42,
        type: LogType = LogType.DEFAULT,
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

    @Test
    fun `toLogMessage maps every field`() {
        // Arrange
        val entity = entity(
            id = 7,
            type = LogType.NETWORK,
            level = LogLevel.ERROR,
            timestamp = 9_876_543L,
            tag = "NetworkTag",
            message = "boom"
        )

        // Act
        val result = entity.toLogMessage(CURRENT_SESSION_ID)

        // Assert
        assertEquals(
            domainMessage(
                id = 7,
                type = LogType.NETWORK,
                level = LogLevel.ERROR,
                timestamp = 9_876_543L,
                tag = "NetworkTag",
                message = "boom"
            ),
            result
        )
    }

    @Test
    fun `toLogMessage marks a message of the current session as current`() {
        // Arrange
        val entity = entity(sessionId = CURRENT_SESSION_ID)

        // Act
        val result = entity.toLogMessage(CURRENT_SESSION_ID)

        // Assert
        assertTrue(result.isCurrentSession)
    }

    @Test
    fun `toLogMessage marks a message of another session as not current`() {
        // Arrange
        val entity = entity(sessionId = OTHER_SESSION_ID)

        // Act
        val result = entity.toLogMessage(CURRENT_SESSION_ID)

        // Assert
        assertFalse(result.isCurrentSession)
    }

    @Test
    fun `toLogMessageEntity maps every field`() {
        // Arrange
        val message = domainMessage(
            id = 7,
            type = LogType.SESSION_START,
            level = LogLevel.WARNING,
            timestamp = 9_876_543L,
            tag = "NetworkTag",
            message = "boom"
        )

        // Act
        val result = message.toLogMessageEntity(CURRENT_SESSION_ID)

        // Assert
        assertEquals(
            entity(
                id = 7,
                type = LogType.SESSION_START,
                level = LogLevel.WARNING,
                timestamp = 9_876_543L,
                tag = "NetworkTag",
                message = "boom"
            ),
            result
        )
    }

    @Test
    fun `toLogMessageEntity stores the given session id`() {
        // Arrange
        val message = domainMessage()

        // Act
        val result = message.toLogMessageEntity(OTHER_SESSION_ID)

        // Assert
        assertEquals(OTHER_SESSION_ID, result.sessionId)
    }

    @Test
    fun `toLogMessageEntity derives the session id from the argument and not from the message`() {
        // Arrange: the domain model carries no session id, only a boolean flag
        val message = domainMessage(isCurrentSession = false)

        // Act
        val result = message.toLogMessageEntity(CURRENT_SESSION_ID)

        // Assert
        assertEquals(CURRENT_SESSION_ID, result.sessionId)
    }

    @Test
    fun `round trip through the entity preserves the message content`() {
        // Arrange
        val original = entity(
            id = 7,
            type = LogType.NETWORK,
            level = LogLevel.DEBUG,
            timestamp = 9_876_543L,
            tag = "NetworkTag",
            message = "boom"
        )

        // Act
        val result = original.toLogMessage(CURRENT_SESSION_ID).toLogMessageEntity(CURRENT_SESSION_ID)

        // Assert
        assertEquals(original.copy(sessionId = CURRENT_SESSION_ID), result)
    }

    @Test
    fun `round trip through the entity re-stamps the session id with the current one`() {
        // Arrange
        val original = entity(sessionId = OTHER_SESSION_ID)

        // Act
        val result = original.toLogMessage(CURRENT_SESSION_ID).toLogMessageEntity(CURRENT_SESSION_ID)

        // Assert
        assertEquals(CURRENT_SESSION_ID, result.sessionId)
    }
}
