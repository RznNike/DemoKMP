package ru.rznnike.demokmp.domain.interactor.app

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeAppGateway
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CheckIfAppIsAlreadyRunningUseCaseTest {
    @Test
    fun `returns true when the app is already running`() = runTest {
        // Arrange
        val gateway = FakeAppGateway().apply { alreadyRunning = true }
        val useCase = CheckIfAppIsAlreadyRunningUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(true, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns false when the app is not running yet`() = runTest {
        // Arrange
        val gateway = FakeAppGateway().apply { alreadyRunning = false }
        val useCase = CheckIfAppIsAlreadyRunningUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        // The gateway answered false, so the result must carry false and not a null payload.
        assertEquals(false, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of the running state`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeAppGateway().apply { errorToThrow = expected }
        val useCase = CheckIfAppIsAlreadyRunningUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
