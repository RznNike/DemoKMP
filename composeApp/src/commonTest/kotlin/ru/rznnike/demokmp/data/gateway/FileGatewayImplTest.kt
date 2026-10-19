package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.gateway.FileGateway
import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FileGatewayImplTest {
    private val gateway: FileGateway = FileGatewayImpl()

    private val tempDirs = mutableListOf<File>()

    private fun createTempDir(): File =
        Files.createTempDirectory("demokmp-file-gateway").toFile().also { tempDirs += it }

    @AfterTest
    fun tearDown() {
        tempDirs.forEach { it.deleteRecursively() }
        tempDirs.clear()
    }

    @Test
    fun `copyFile copies content to target`() = runTest {
        // Arrange
        val content = byteArrayOf(0x25, 0x50, 0x44, 0x46, 0x00, 0xFF.toByte(), 0x0A)
        val original = File(createTempDir(), "original.bin").apply { writeBytes(content) }
        val copy = File(createTempDir(), "copy.bin")

        // Act
        gateway.copyFile(original = original, copy = copy)

        // Assert
        assertTrue(copy.exists())
        assertContentEquals(content, copy.readBytes())
    }

    @Test
    fun `copyFile creates target when it does not exist`() = runTest {
        // Arrange
        val content = "new target".toByteArray()
        val original = File(createTempDir(), "original.txt").apply { writeBytes(content) }
        val copy = File(createTempDir(), "not-yet-created.txt")
        assertFalse(copy.exists())

        // Act
        gateway.copyFile(original = original, copy = copy)

        // Assert
        assertTrue(copy.exists())
        assertContentEquals(content, copy.readBytes())
    }

    @Test
    fun `copyFile overwrites and truncates existing target`() = runTest {
        // Arrange: new content is shorter than the stale content
        val content = "short".toByteArray()
        val original = File(createTempDir(), "original.txt").apply { writeBytes(content) }
        val copy = File(createTempDir(), "copy.txt")
            .apply { writeBytes("much longer stale content".toByteArray()) }

        // Act
        gateway.copyFile(original = original, copy = copy)

        // Assert
        assertContentEquals(content, copy.readBytes())
    }

    @Test
    fun `copyFile preserves source file`() = runTest {
        // Arrange
        val content = "untouched".toByteArray()
        val original = File(createTempDir(), "original.txt").apply { writeBytes(content) }
        val copy = File(createTempDir(), "copy.txt")

        // Act
        gateway.copyFile(original = original, copy = copy)

        // Assert
        assertTrue(original.exists())
        assertContentEquals(content, original.readBytes())
    }

    @Test
    fun `copyFile throws when source is missing`() = runTest {
        // Arrange
        val original = File(createTempDir(), "missing.txt")
        val copy = File(createTempDir(), "copy.txt")
        assertFalse(original.exists())

        // Act & Assert
        assertFailsWith<NoSuchFileException> {
            gateway.copyFile(original = original, copy = copy)
        }
    }
}
