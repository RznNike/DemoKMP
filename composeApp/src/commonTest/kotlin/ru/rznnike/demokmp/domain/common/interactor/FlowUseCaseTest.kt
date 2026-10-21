package ru.rznnike.demokmp.domain.common.interactor

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import kotlin.coroutines.ContinuationInterceptor
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

private class FakeFlowUseCase(
    dispatcherProvider: FakeDispatcherProvider,
    private val flow: suspend () -> Flow<Int>
) : FlowUseCase<Int>(dispatcherProvider) {
    override suspend fun execute(): Flow<Int> = flow()
}

class FlowUseCaseTest {
    @Test
    fun `emits values from execute`() = runTest {
        // Arrange
        val useCase = FakeFlowUseCase(FakeDispatcherProvider()) { listOf(1, 2, 3).asFlow() }

        // Act
        val result = useCase().toList()

        // Assert
        assertEquals(listOf(1, 2, 3), result)
    }

    @Test
    fun `propagates exception thrown by execute`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val useCase = FakeFlowUseCase(FakeDispatcherProvider()) { throw expected }

        // Act
        val result = runCatching { useCase() }

        // Assert
        assertTrue(result.isFailure)
        assertEquals(expected, result.exceptionOrNull())
    }

    @Test
    fun `emits on injected io dispatcher`() = runTest {
        // Arrange
        val ioDispatcher = StandardTestDispatcher(testScheduler)
        var collectorDispatcher: ContinuationInterceptor? = null
        val useCase = FakeFlowUseCase(FakeDispatcherProvider(ioDispatcher)) {
            flow {
                collectorDispatcher = currentCoroutineContext()[ContinuationInterceptor]
                emit(1)
            }
        }

        // Act
        val result = useCase().toList()

        // Assert
        assertEquals(listOf(1), result)
        assertSame(ioDispatcher, collectorDispatcher)
    }
}
