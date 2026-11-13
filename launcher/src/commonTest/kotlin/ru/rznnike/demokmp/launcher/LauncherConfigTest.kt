package ru.rznnike.demokmp.launcher

import kotlin.test.Test
import kotlin.test.assertEquals

class LauncherConfigTest {
    @Test
    fun `parseLauncherConfiguration parses all known keys`() {
        // Arrange
        val text = """
            java_path=C:\Java\bin\java.exe
            render_api=vulkan
        """.trimIndent()

        // Act
        val result = text.toLauncherConfig()

        // Assert
        assertEquals(
            LauncherConfig(
                javaPath = "C:\\Java\\bin\\java.exe",
                renderApi = "vulkan"
            ),
            result
        )
    }

    @Test
    fun `parseLauncherConfiguration uses defaults for empty input`() {
        // Act
        val result = "".toLauncherConfig()

        // Assert
        assertEquals(LauncherConfig(), result)
    }

    @Test
    fun `parseLauncherConfiguration handles CRLF line endings`() {
        // Arrange
        val text = "java_path=C:\\Java\\bin\\java.exe\r\nrender_api=vulkan\r\n"

        // Act
        val result = text.toLauncherConfig()

        // Assert
        assertEquals(
            LauncherConfig(
                javaPath = "C:\\Java\\bin\\java.exe",
                renderApi = "vulkan"
            ),
            result
        )
    }

    @Test
    fun `parseLauncherConfiguration keeps values containing an equals sign`() {
        // Act
        val result = "render_api=--flag=a=b".toLauncherConfig()

        // Assert
        assertEquals("--flag=a=b", result.renderApi)
    }

    @Test
    fun `parseLauncherConfiguration ignores blank lines and comments`() {
        // Arrange
        val text = """
            # comment

            ; another comment
            java_path=java
        """.trimIndent()

        // Act
        val result = text.toLauncherConfig()

        // Assert
        assertEquals("java", result.javaPath)
    }

    @Test
    fun `parseLauncherConfiguration trims surrounding whitespace`() {
        // Act
        val result = "  java_path = java  ".toLauncherConfig()

        // Assert
        assertEquals("java", result.javaPath)
    }

    @Test
    fun `parseLauncherConfiguration keeps empty values as defaults`() {
        // Act
        val result = "java_path=\nrender_api=".toLauncherConfig()

        // Assert
        assertEquals(LauncherConfig(), result)
    }
}
