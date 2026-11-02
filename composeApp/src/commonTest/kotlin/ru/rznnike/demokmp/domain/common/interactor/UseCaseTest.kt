package ru.rznnike.demokmp.domain.common.interactor

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertSame

private class FakeUseCase(
    dispatcherProvider: FakeDispatcherProvider,
    private val result: suspend () -> String
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

    @Test
    fun `executes on injected io dispatcher`() = runTest {
        // Arrange
        val ioDispatcher = StandardTestDispatcher(testScheduler)
        var executorDispatcher: ContinuationInterceptor? = null
        val useCase = FakeUseCase(FakeDispatcherProvider(ioDispatcher)) {
            executorDispatcher = currentCoroutineContext()[ContinuationInterceptor]
            "value"
        }

        // Act
        val result = useCase()

        // Assert
        assertEquals("value", result.data)
        assertSame(ioDispatcher, executorDispatcher)
    }
}
