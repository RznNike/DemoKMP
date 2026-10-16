package ru.rznnike.demokmp.domain.common.interactor

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

private class FakeUseCase(
    dispatcherProvider: FakeDispatcherProvider,
    private val result: () -> String
) : UseCase<String>(dispatcherProvider) {
    override suspend fun execute(): String = result()
}

class UseCaseTest {
    @Test
    fun `returns data when execute succeeds`() = runTest {
        // Arrange
        val useCase = FakeUseCase(FakeDispatcherProvider()) { "value" }

        // Act
        val result = useCase()

        // Assert
        assertEquals("value", result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns error when execute fails`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val useCase = FakeUseCase(FakeDispatcherProvider()) { throw expected }

        // Act
        val result = useCase()

        // Assert
        assertNull(result.data)
        assertEquals(expected, result.error)
    }
}
