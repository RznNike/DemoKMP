package ru.rznnike.demokmp.domain.interactor.httpexample

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakeHTTPExampleGateway
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetRandomImageLinksUseCaseTest {
    @Test
    fun `forwards count and returns the gateway links`() = runTest {
        // Arrange
        val links = listOf("https://example.com/1", "https://example.com/2")
        val gateway = FakeHTTPExampleGateway().apply { this.links = links }
        val useCase = GetRandomImageLinksUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(GetRandomImageLinksUseCase.Parameters(count = 2))

        // Assert
        assertEquals(listOf(2), gateway.requestedCounts)
        assertEquals(links, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakeHTTPExampleGateway().apply { errorToThrow = expected }
        val useCase = GetRandomImageLinksUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(GetRandomImageLinksUseCase.Parameters(count = 2))

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
