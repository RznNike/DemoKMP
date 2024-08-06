package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.data.network.AppApi
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.NetworkTestGateway

class NetworkTestGatewayImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val appApi: AppApi
) : NetworkTestGateway {
    override suspend fun getRandomImageLink(): String = withContext(dispatcherProvider.io) {
        appApi.getRandomImage().link
    }
}