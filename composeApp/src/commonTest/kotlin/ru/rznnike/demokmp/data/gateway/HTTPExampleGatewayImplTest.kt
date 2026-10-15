package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.data.network.AppApi
import ru.rznnike.demokmp.data.network.model.RandomImageLinksModel
import ru.rznnike.demokmp.domain.gateway.HTTPExampleGateway
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FakeAppApi(
    private val response: RandomImageLinksModel
) : AppApi {
    var lastCount: Int? = null
    override suspend fun getRandomImages(count: Int): RandomImageLinksModel {
        lastCount = count
        return response
    }
}

class HTTPExampleGatewayImplTest {
    @Test
    fun `getRandomImageLinks returns links from api`() = runTest {
        // Arrange
        val links = listOf("https://example.com/1.jpg", "https://example.com/2.jpg", "https://example.com/3.jpg")
        val fakeApi = FakeAppApi(RandomImageLinksModel(links = links))
        val gateway: HTTPExampleGateway = HTTPExampleGatewayImpl(fakeApi)

        // Act
        val result = gateway.getRandomImageLinks(count = 3)

        // Assert
        assertEquals(links, result)
        assertEquals(3, fakeApi.lastCount)
    }

    @Test
    fun `getRandomImageLinks passes count parameter`() = runTest {
        // Arrange
        val fakeApi = FakeAppApi(RandomImageLinksModel(links = emptyList()))
        val gateway: HTTPExampleGateway = HTTPExampleGatewayImpl(fakeApi)

        // Act
        val result = gateway.getRandomImageLinks(count = 10)

        // Assert
        assertEquals(0, result.size)
        assertEquals(10, fakeApi.lastCount)
    }
}
