package ru.rznnike.demokmp.app.di

import java.io.File
import java.nio.file.Files
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class NetworkModuleTest {
    private val tempPaths = mutableListOf<File>()

    private fun tempFile(): File =
        Files.createTempFile("demokmp-network-cache", ".txt").toFile().also { tempPaths += it }

    private fun tempDirectory(): File =
        Files.createTempDirectory("demokmp-network-cache").toFile().also { tempPaths += it }

    @AfterTest
    fun tearDown() {
        tempPaths.forEach { it.deleteRecursively() }
        tempPaths.clear()
    }

    @Test
    fun `createNetworkCache returns null when the directory cannot be created`() {
        // Arrange (a regular file stands in for the parent, so mkdirs must fail)
        val blockingFile = tempFile()
        val cacheDirectory = File(blockingFile, "networkCache").also { tempPaths += it }

        // Act
        val cache = createNetworkCache(cacheDirectory)

        // Assert
        assertNull(cache)
    }

    @Test
    fun `createNetworkCache creates a missing directory and returns a cache`() {
        // Arrange
        val cacheDirectory = File(tempDirectory(), "networkCache").also { tempPaths += it }

        // Act
        val cache = createNetworkCache(cacheDirectory)

        // Assert
        assertNotNull(cache)
    }
}
