package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.data.network.AppApi
import ru.rznnike.demokmp.data.network.model.RandomImageLinksModel
import ru.rznnike.demokmp.domain.gateway.HTTPExampleGateway
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeAppApi(
    private val response: RandomImageLinksModel = RandomImageLinksModel(links = emptyList()),
    private val error: Exception? = null
) : AppApi {
    var lastCount: Int? = null

    override suspend fun getRandomImages(count: Int): RandomImageLinksModel {
        lastCount = count
        error?.let { throw it }
        return response
    }
}

class HTTPExampleGatewayImplTest {
    @Test
    fun `returns links from api response`() = runTest {
        // Arrange
        val links = listOf("https://example.com/1.jpg", "https://example.com/2.jpg", "https://example.com/3.jpg")
        val gateway: HTTPExampleGateway = HTTPExampleGatewayImpl(FakeAppApi(RandomImageLinksModel(links = links)))

        // Act
        val result = gateway.getRandomImageLinks(count = 3)

        // Assert
        assertEquals(links, result)
    }

    @Test
    fun `passes count to api`() = runTest {
        // Arrange
        val fakeApi = FakeAppApi()
        val gateway: HTTPExampleGateway = HTTPExampleGatewayImpl(fakeApi)

        // Act
        gateway.getRandomImageLinks(count = 10)

        // Assert
        assertEquals(10, fakeApi.lastCount)
    }

    @Test
    fun `returns empty list when api responds with no links`() = runTest {
        // Arrange
        val gateway: HTTPExampleGateway = HTTPExampleGatewayImpl(FakeAppApi())

        // Act
        val result = gateway.getRandomImageLinks(count = 5)

        // Assert
        assertEquals(emptyList(), result)
    }

    @Test
    fun `propagates api exception`() = runTest {
        // Arrange
        val expected = IllegalStateException("network failure")
        val gateway: HTTPExampleGateway = HTTPExampleGatewayImpl(FakeAppApi(error = expected))

        // Act
        // runCatching is inline, so a suspend call is allowed inside it (assertFailsWith's block is not)
        val result = runCatching { gateway.getRandomImageLinks(count = 1) }

        // Assert
        assertTrue(result.isFailure)
        assertEquals(expected, result.exceptionOrNull())
    }
}
