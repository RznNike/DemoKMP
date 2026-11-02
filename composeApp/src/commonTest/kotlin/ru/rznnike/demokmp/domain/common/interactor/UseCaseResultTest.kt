package ru.rznnike.demokmp.domain.common.interactor

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class UseCaseResultTest {
    @Test
    fun `invokes success callback when data is present`() = runTest {
        // Arrange
        var received: String? = null
        val result = UseCaseResult(data = "value")

        // Act
        result.process(onSuccessCallback = { received = it })

        // Assert
        assertEquals("value", received)
    }

    @Test
    fun `invokes error callback when error is present`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        var received: Exception? = null
        val result = UseCaseResult<String>(error = expected)

        // Act
        result.process(onSuccessCallback = {}, onErrorCallback = { received = it })

        // Assert
        assertEquals(expected, received)
    }

    @Test
    fun `prefers error over data when both are present`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        var successCalled = false
        var received: Exception? = null
        val result = UseCaseResult(data = "value", error = expected)

        // Act
        result.process(onSuccessCallback = { successCalled = true }, onErrorCallback = { received = it })

        // Assert
        assertEquals(expected, received)
        assertFalse(successCalled)
    }

    @Test
    fun `invokes error callback with unexpected exception when data and error are absent`() = runTest {
        // Arrange
        var received: Exception? = null
        val result = UseCaseResult<String>()

        // Act
        result.process(onSuccessCallback = {}, onErrorCallback = { received = it })

        // Assert
        assertEquals(
            "Unexpected exception in UseCaseResult: data=null and error=null",
            received?.message
        )
    }

    @Test
    fun `does not throw and returns itself when error is present without error callback`() = runTest {
        // Arrange
        val result = UseCaseResult<String>(error = IllegalStateException("boom"))

        // Act
        val returned = result.process(onSuccessCallback = {})

        // Assert
        assertEquals(result, returned)
    }

    @Test
    fun `does not invoke callbacks and returns itself when data is absent without error callback`() = runTest {
        // Arrange
        var successCalled = false
        val result = UseCaseResult<String>()

        // Act
        val returned = result.process(onSuccessCallback = { successCalled = true })

        // Assert
        assertFalse(successCalled)
        assertEquals(result, returned)
    }

    @Test
    fun `toString describes data on success`() = runTest {
        // Act
        val description = UseCaseResult(data = "value").toString()

        // Assert
        assertEquals("Success[data=value]", description)
    }

    @Test
    fun `toString describes error on failure`() = runTest {
        // Arrange
        val error = IllegalStateException("boom")

        // Act
        val description = UseCaseResult<String>(error = error).toString()

        // Assert
        assertEquals("Error[exception=$error]", description)
    }
}
