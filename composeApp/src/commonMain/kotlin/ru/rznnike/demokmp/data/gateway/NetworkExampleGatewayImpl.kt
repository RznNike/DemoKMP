package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.data.network.AppApi
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.NetworkExampleGateway

class NetworkExampleGatewayImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val appApi: AppApi
) : NetworkExampleGateway {
    override suspend fun getRandomImageLinks(
        count: Int
    ): List<String> = withContext(dispatcherProvider.io) {
        appApi.getRandomImages(
            count = count
        ).links
    }
}