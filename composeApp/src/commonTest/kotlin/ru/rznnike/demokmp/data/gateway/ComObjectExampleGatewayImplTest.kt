package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.data.shell.ShellManager
import ru.rznnike.demokmp.domain.gateway.ComObjectExampleGateway
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private const val COM_INIT = "comInit"
private const val COM_DESTROY = "comDestroy"

class ComObjectExampleGatewayImplTest {
    private class FakeShellManager : ShellManager {
        val calls = mutableListOf<String>()
        var pcData: String = ""
        var failure: Throwable? = null

        override suspend fun initWrapper() = record("initWrapper")

        override suspend fun destroyWrapper() = record("destroyWrapper")

        override suspend fun getPCData(): String {
            record("getPCData")
            return pcData
        }

        override suspend fun openFolderOrFile(path: String) = record("openFolderOrFile:$path")

        override suspend fun minimizeAllWindows() = record("minimizeAllWindows")

        private fun record(call: String) {
            failure?.let { throw it }
            calls += call
        }
    }

    /** COM calls are recorded into the same log as the shell manager calls, so their order is asserted. */
    private fun createGateway(
        shellManager: FakeShellManager = FakeShellManager(),
        isWindows: Boolean = true
    ): ComObjectExampleGateway = ComObjectExampleGatewayImpl(
        shellManager = shellManager,
        isWindows = isWindows,
        initComCallback = { shellManager.calls += COM_INIT },
        destroyComCallback = { shellManager.calls += COM_DESTROY }
    )

    @Test
    fun `initShellWrapper initializes COM before the shell wrapper`() = runTest {
        // Arrange
        val shellManager = FakeShellManager()
        val gateway = createGateway(shellManager = shellManager)

        // Act
        gateway.initShellWrapper()

        // Assert
        assertEquals(listOf(COM_INIT, "initWrapper"), shellManager.calls)
    }

    @Test
    fun `destroyShellWrapper destroys the shell wrapper before uninitializing COM`() = runTest {
        // Arrange
        val shellManager = FakeShellManager()
        val gateway = createGateway(shellManager = shellManager)

        // Act
        gateway.destroyShellWrapper()

        // Assert
        assertEquals(listOf("destroyWrapper", COM_DESTROY), shellManager.calls)
    }

    @Test
    fun `getPCData returns shell manager data`() = runTest {
        // Arrange
        val shellManager = FakeShellManager().apply {
            pcData = "CPU 2400 Mhz (amd64) | RAM 16.00 Gb"
        }
        val gateway = createGateway(shellManager = shellManager)

        // Act
        val result = gateway.getPCData()

        // Assert
        assertEquals("CPU 2400 Mhz (amd64) | RAM 16.00 Gb", result)
        assertEquals(listOf("getPCData"), shellManager.calls)
    }

    @Test
    fun `openFolderOrFile passes the path to the shell manager`() = runTest {
        // Arrange
        val shellManager = FakeShellManager()
        val gateway = createGateway(shellManager = shellManager)

        // Act
        gateway.openFolderOrFile("C:/temp/some file.txt")

        // Assert
        assertEquals(listOf("openFolderOrFile:C:/temp/some file.txt"), shellManager.calls)
    }

    @Test
    fun `minimizeAllWindows delegates to the shell manager`() = runTest {
        // Arrange
        val shellManager = FakeShellManager()
        val gateway = createGateway(shellManager = shellManager)

        // Act
        gateway.minimizeAllWindows()

        // Assert
        assertEquals(listOf("minimizeAllWindows"), shellManager.calls)
    }

    @Test
    fun `all methods are no-ops outside Windows`() = runTest {
        // Arrange
        val shellManager = FakeShellManager()
        val gateway = createGateway(shellManager = shellManager, isWindows = false)

        // Act
        gateway.initShellWrapper()
        gateway.destroyShellWrapper()
        val pcData = gateway.getPCData()
        gateway.openFolderOrFile("C:/temp")
        gateway.minimizeAllWindows()

        // Assert
        assertEquals(emptyList(), shellManager.calls)
        assertEquals("", pcData)
    }

    @Test
    fun `getPCData propagates shell manager failure`() = runTest {
        // Arrange
        val shellManager = FakeShellManager().apply {
            failure = IllegalStateException("PowerShellWrapper is not initialized")
        }
        val gateway = createGateway(shellManager = shellManager)

        // Act & Assert
        assertFailsWith<IllegalStateException> { gateway.getPCData() }
    }
}
