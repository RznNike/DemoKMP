package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.AppGateway
import ru.rznnike.demokmp.domain.model.common.LauncherConfig
import java.net.ServerSocket
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppGatewayImplTest {
    // Mirrors the production fallback: no launcher_configuration.ini exists in the test working directory
    private val port: Int = LauncherConfig().singleInstancePort
    private val gateway: AppGateway = AppGatewayImpl(FakeDispatcherProvider())

    @Test
    fun `returns false when the single instance port is free`() = runTest {
        try {
            // Act
            val result = gateway.checkIfAppIsAlreadyRunning()

            // Assert
            assertFalse(result)
        } finally {
            gateway.closeAppSingleInstanceSocket()
        }
    }

    @Test
    fun `returns true when the single instance port is already occupied`() = runTest {
        // Arrange
        ServerSocket(port).use {
            // Act
            val result = gateway.checkIfAppIsAlreadyRunning()

            // Assert
            assertTrue(result)
        }
    }

    @Test
    fun `releases the port after closeAppSingleInstanceSocket`() = runTest {
        try {
            // Arrange
            assertFalse(gateway.checkIfAppIsAlreadyRunning())

            // Act
            gateway.closeAppSingleInstanceSocket()

            // Assert
            ServerSocket(port).use { socket ->
                assertTrue(socket.isBound)
            }
        } finally {
            gateway.closeAppSingleInstanceSocket()
        }
    }

    @Test
    fun `closeAppSingleInstanceSocket does not fail without a prior check`() = runTest {
        // Act & Assert (no exception expected)
        gateway.closeAppSingleInstanceSocket()
    }
}
