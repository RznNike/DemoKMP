package ru.rznnike.demokmp.domain.interactor.wsexample

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.WebSocketExampleGateway
import ru.rznnike.demokmp.domain.model.wsexample.WebSocketMessage

class SendAppWSMessageUseCase(
    private val webSocketExampleGateway: WebSocketExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<WebSocketMessage, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: WebSocketMessage) =
        webSocketExampleGateway.sendAppWSMessage(parameters)
}