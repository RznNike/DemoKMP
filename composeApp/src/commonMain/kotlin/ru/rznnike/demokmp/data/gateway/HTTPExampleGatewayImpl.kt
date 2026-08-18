package ru.rznnike.demokmp.data.gateway

import ru.rznnike.demokmp.data.network.AppApi
import ru.rznnike.demokmp.domain.gateway.HTTPExampleGateway

class HTTPExampleGatewayImpl(
    private val appApi: AppApi
) : HTTPExampleGateway {
    override suspend fun getRandomImageLinks(
        count: Int
    ): List<String> {
        return appApi.getRandomImages(
            count = count
        ).links
    }
}