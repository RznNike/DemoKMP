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

private class FakeUseCaseWithParams(
    dispatcherProvider: FakeDispatcherProvider,
    private val result: suspend (Int) -> String
) : UseCaseWithParams<Int, String>(dispatcherProvider) {
    var lastParameters: Int? = null

    override suspend fun execute(parameters: Int): String {
        lastParameters = parameters
        return result(parameters)
    }
}

class UseCaseWithParamsTest {
    @Test
    fun `returns data when execute succeeds`() = runTest {
        // Arrange
        val useCase = FakeUseCaseWithParams(FakeDispatcherProvider()) { "value:$it" }

        // Act
        val result = useCase(42)

        // Assert
        assertEquals("value:42", result.data)
        assertNull(result.error)
    }

    @Test
    fun `passes parameters to execute`() = runTest {
        // Arrange
        val useCase = FakeUseCaseWithParams(FakeDispatcherProvider()) { "value" }

        // Act
        useCase(42)

        // Assert
        assertEquals(42, useCase.lastParameters)
    }

    @Test
    fun `returns error when execute fails`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val useCase = FakeUseCaseWithParams(FakeDispatcherProvider()) { throw expected }

        // Act
        val result = useCase(1)

        // Assert
        assertNull(result.data)
        assertEquals(expected, result.error)
    }

    @Test
    fun `executes on injected io dispatcher`() = runTest {
        // Arrange
        val ioDispatcher = StandardTestDispatcher(testScheduler)
        var executorDispatcher: ContinuationInterceptor? = null
        val useCase = FakeUseCaseWithParams(FakeDispatcherProvider(ioDispatcher)) { parameters ->
            executorDispatcher = currentCoroutineContext()[ContinuationInterceptor]
            "value:$parameters"
        }

        // Act
        val result = useCase(42)

        // Assert
        assertEquals("value:42", result.data)
        assertSame(ioDispatcher, executorDispatcher)
    }
}
