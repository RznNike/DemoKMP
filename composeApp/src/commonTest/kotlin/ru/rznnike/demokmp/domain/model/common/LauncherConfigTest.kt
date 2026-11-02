package ru.rznnike.demokmp.domain.model.common

import kotlin.test.Test
import kotlin.test.assertEquals

class LauncherConfigTest {
    @Test
    fun `toLauncherConfig parses all keys`() {
        // Arrange
        val text = """
            java_path=C:\Java\bin\java.exe
            render_api=vulkan
            single_instance_port=64000
        """.trimIndent()

        // Act
        val result = text.toLauncherConfig()

        // Assert
        assertEquals(
            LauncherConfig(
                javaPath = "C:\\Java\\bin\\java.exe",
                renderApi = "vulkan",
                singleInstancePort = 64000
            ),
            result
        )
    }

    @Test
    fun `toLauncherConfig uses defaults for empty input`() {
        // Act
        val result = "".toLauncherConfig()

        // Assert
        assertEquals(LauncherConfig(), result)
    }

    @Test
    fun `toLauncherConfig uses default port for a non-numeric value`() {
        // Act
        val result = "single_instance_port=not-a-number".toLauncherConfig()

        // Assert
        assertEquals(LauncherConfig().singleInstancePort, result.singleInstancePort)
    }

    @Test
    fun `toLauncherConfig ignores blank lines`() {
        // Arrange
        val text = "\n\njava_path=java\n\n"

        // Act
        val result = text.toLauncherConfig()

        // Assert
        assertEquals("java", result.javaPath)
    }

    @Test
    fun `toLauncherConfig keeps empty values as defaults`() {
        // Arrange
        val text = "java_path=\nrender_api=\nsingle_instance_port="

        // Act
        val result = text.toLauncherConfig()

        // Assert
        assertEquals(LauncherConfig(), result)
    }

    @Test
    fun `toLauncherConfig handles CRLF line endings`() {
        // Arrange: hand-edited .ini files on Windows use CRLF
        val text = "java_path=C:\\Java\\bin\\java.exe\r\nrender_api=vulkan\r\nsingle_instance_port=64000\r\n"

        // Act
        val result = text.toLauncherConfig()

        // Assert
        assertEquals(
            LauncherConfig(
                javaPath = "C:\\Java\\bin\\java.exe",
                renderApi = "vulkan",
                singleInstancePort = 64000
            ),
            result
        )
    }

    @Test
    fun `toLauncherConfig keeps values containing an equals sign`() {
        // Arrange
        val text = "render_api=--flag=a=b"

        // Act
        val result = text.toLauncherConfig()

        // Assert
        assertEquals("--flag=a=b", result.renderApi)
    }
}
