package ru.rznnike.demokmp.domain.interactor.networktest

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCase
import ru.rznnike.demokmp.domain.gateway.NetworkTestGateway

class GetRandomImageLinkUseCase(
    private val networkTestGateway: NetworkTestGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<String>(dispatcherProvider) {
    override suspend fun execute() = networkTestGateway.getRandomImageLink()
}