package ru.rznnike.demokmp.launcher

import kotlin.test.Test
import kotlin.test.assertEquals

class WindowlessJavaCommandTest {
    private val windowlessPath = "C:\\jdk\\bin\\javaw.exe"

    @Test
    fun `windowlessJavaCommand maps a bare java command to javaw`() {
        // Act
        val command = windowlessJavaCommand("java") { false }

        // Assert
        assertEquals("javaw.exe", command)
    }

    @Test
    fun `windowlessJavaCommand maps java exe ignoring case`() {
        // Act
        val command = windowlessJavaCommand("JAVA.EXE") { false }

        // Assert
        assertEquals("javaw.exe", command)
    }

    @Test
    fun `windowlessJavaCommand maps an existing absolute java executable`() {
        // Act
        val command = windowlessJavaCommand(
            javaCommand = "C:\\jdk\\bin\\java.exe",
            pathExists = { it == windowlessPath }
        )

        // Assert
        assertEquals("C:\\jdk\\bin\\javaw.exe", command)
    }

    @Test
    fun `windowlessJavaCommand maps an absolute java command without extension`() {
        // Act
        val command = windowlessJavaCommand(
            javaCommand = "C:\\jdk\\bin\\java",
            pathExists = { it == windowlessPath }
        )

        // Assert
        assertEquals("C:\\jdk\\bin\\javaw.exe", command)
    }

    @Test
    fun `windowlessJavaCommand keeps the original separator style`() {
        // Act
        val command = windowlessJavaCommand(
            javaCommand = "C:/jdk/bin/java.exe",
            pathExists = { it == "C:/jdk/bin/javaw.exe" }
        )

        // Assert
        assertEquals("C:/jdk/bin/javaw.exe", command)
    }

    @Test
    fun `windowlessJavaCommand falls back when javaw is missing`() {
        // Act
        val command = windowlessJavaCommand(
            javaCommand = "C:\\jdk\\bin\\java.exe",
            pathExists = { false }
        )

        // Assert
        assertEquals("C:\\jdk\\bin\\java.exe", command)
    }

    @Test
    fun `windowlessJavaCommand keeps an already windowless executable`() {
        // Act
        val command = windowlessJavaCommand("C:\\jdk\\bin\\javaw.exe") { true }

        // Assert
        assertEquals("C:\\jdk\\bin\\javaw.exe", command)
    }

    @Test
    fun `windowlessJavaCommand keeps a non java executable`() {
        // Act
        val command = windowlessJavaCommand("C:\\tools\\java-wrapper.exe") { true }

        // Assert
        assertEquals("C:\\tools\\java-wrapper.exe", command)
    }
}
