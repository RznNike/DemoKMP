package ru.rznnike.demokmp.data.network.error

import kotlin.test.Test
import kotlin.test.assertEquals

class CustomServerExceptionTest {
    @Test
    fun `constructor preserves http code`() {
        // Arrange + Act
        val error = CustomServerException(404)

        // Assert
        assertEquals(404, error.httpCode)
    }

    @Test
    fun `isServerSide is false for redirect code`() {
        // Arrange + Act
        val error = CustomServerException(300)

        // Assert
        assertEquals(false, error.isServerSide)
    }

    @Test
    fun `isServerSide is false for client error`() {
        // Arrange + Act
        val error = CustomServerException(404)

        // Assert
        assertEquals(false, error.isServerSide)
    }

    @Test
    fun `isServerSide is false just below server range`() {
        // Arrange + Act
        val error = CustomServerException(499)

        // Assert
        assertEquals(false, error.isServerSide)
    }

    @Test
    fun `isServerSide is true at lower server boundary`() {
        // Arrange + Act
        val error = CustomServerException(500)

        // Assert
        assertEquals(true, error.isServerSide)
    }

    @Test
    fun `isServerSide is true at upper server boundary`() {
        // Arrange + Act
        val error = CustomServerException(599)

        // Assert
        assertEquals(true, error.isServerSide)
    }

    @Test
    fun `isServerSide follows server side threshold for all codes`() {
        // Arrange
        val codes = listOf(199, 200, 302, 400, 499, 500, 503, 599)

        // Act + Assert
        codes.forEach { code ->
            val error = CustomServerException(code)
            assertEquals(code >= 500, error.isServerSide, "code=$code")
        }
    }
}
