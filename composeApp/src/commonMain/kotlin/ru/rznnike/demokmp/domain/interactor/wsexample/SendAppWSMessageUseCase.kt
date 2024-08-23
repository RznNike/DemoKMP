package ru.rznnike.demokmp.domain.interactor.wsexample

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.WebSocketExampleGateway

class SendAppWSMessageUseCase(
    private val webSocketExampleGateway: WebSocketExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<String, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: String) =
        webSocketExampleGateway.sendMessage(parameters)
}