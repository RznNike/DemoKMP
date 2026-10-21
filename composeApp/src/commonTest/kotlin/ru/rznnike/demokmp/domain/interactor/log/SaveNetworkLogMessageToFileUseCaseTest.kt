package ru.rznnike.demokmp.domain.interactor.log

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeLogGateway
import ru.rznnike.demokmp.domain.log.testNetworkLogMessage
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SaveNetworkLogMessageToFileUseCaseTest {
    @Test
    fun `forwards file and message to the gateway`() = runTest {
        // Arrange
        val file = File("network.log")
        val message = testNetworkLogMessage()
        val gateway = FakeLogGateway()
        val useCase = SaveNetworkLogMessageToFileUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(
            SaveNetworkLogMessageToFileUseCase.Parameters(file = file, message = message)
        )

        // Assert
        assertEquals(listOf(file to message), gateway.savedNetworkMessages)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeLogGateway().apply { errorToThrow = expected }
        val useCase = SaveNetworkLogMessageToFileUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(
            SaveNetworkLogMessageToFileUseCase.Parameters(file = File("network.log"), message = testNetworkLogMessage())
        )

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
