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

private class FakeFlowUseCaseWithParams(
    dispatcherProvider: FakeDispatcherProvider,
    private val flow: (Int) -> Flow<String>
) : FlowUseCaseWithParams<Int, String>(dispatcherProvider) {
    var lastParameters: Int? = null

    override suspend fun execute(parameters: Int): Flow<String> {
        lastParameters = parameters
        return flow(parameters)
    }
}

class FlowUseCaseWithParamsTest {
    @Test
    fun `emits values from execute`() = runTest {
        // Arrange
        val useCase = FakeFlowUseCaseWithParams(FakeDispatcherProvider()) {
            listOf("a", "b", "c").asFlow()
        }

        // Act
        val result = useCase(1).toList()

        // Assert
        assertEquals(listOf("a", "b", "c"), result)
    }

    @Test
    fun `passes parameters to execute`() = runTest {
        // Arrange
        val useCase = FakeFlowUseCaseWithParams(FakeDispatcherProvider()) { parameters ->
            listOf("value:$parameters").asFlow()
        }

        // Act
        useCase(42).toList()

        // Assert
        assertEquals(42, useCase.lastParameters)
    }

    @Test
    fun `propagates exception thrown by execute`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val useCase = FakeFlowUseCaseWithParams(FakeDispatcherProvider()) { throw expected }

        // Act
        val result = runCatching { useCase(1) }

        // Assert
        assertTrue(result.isFailure)
        assertEquals(expected, result.exceptionOrNull())
    }

    @Test
    fun `emits on injected io dispatcher`() = runTest {
        // Arrange
        val ioDispatcher = StandardTestDispatcher(testScheduler)
        var collectorDispatcher: ContinuationInterceptor? = null
        val useCase = FakeFlowUseCaseWithParams(FakeDispatcherProvider(ioDispatcher)) { parameters ->
            flow {
                collectorDispatcher = currentCoroutineContext()[ContinuationInterceptor]
                emit("value:$parameters")
            }
        }

        // Act
        val result = useCase(42).toList()

        // Assert
        assertEquals(listOf("value:42"), result)
        assertSame(ioDispatcher, collectorDispatcher)
    }
}
