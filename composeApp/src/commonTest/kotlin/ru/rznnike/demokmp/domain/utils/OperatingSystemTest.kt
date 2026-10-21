package ru.rznnike.demokmp.domain.utils

import kotlin.test.Test
import kotlin.test.assertEquals

class OperatingSystemTest {
    @Test
    fun `tags match the OS values emitted by BuildKonfig`() {
        // Act
        val tags = OperatingSystem.entries.map { it.tag }

        // Assert
        assertEquals(listOf("windows", "linux", "android"), tags)
    }

    @Test
    fun `get resolves linux tag`() {
        // Act
        val result = OperatingSystem["linux"]

        // Assert
        assertEquals(OperatingSystem.LINUX, result)
    }

    @Test
    fun `get resolves android tag`() {
        // Act
        val result = OperatingSystem["android"]

        // Assert
        assertEquals(OperatingSystem.ANDROID, result)
    }

    @Test
    fun `get falls back to Windows for an empty tag`() {
        // Act
        val result = OperatingSystem[""]

        // Assert
        assertEquals(OperatingSystem.WINDOWS, result)
    }

    @Test
    fun `get falls back to Windows for an unsupported OS tag`() {
        // Act
        val result = OperatingSystem["macos"]

        // Assert
        assertEquals(OperatingSystem.WINDOWS, result)
    }
}
