package ru.rznnike.demokmp.domain.interactor.wsexample

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCase
import ru.rznnike.demokmp.domain.gateway.WebSocketExampleGateway

class CloseAppWSSessionUseCase(
    private val webSocketExampleGateway: WebSocketExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<Unit>(dispatcherProvider) {
    override suspend fun execute() = webSocketExampleGateway.closeSession()
}