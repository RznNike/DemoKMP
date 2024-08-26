package ru.rznnike.demokmp.domain.interactor.wsexample

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.FlowUseCase
import ru.rznnike.demokmp.domain.gateway.WebSocketExampleGateway
import ru.rznnike.demokmp.domain.model.wsexample.WebSocketMessage

class OpenAppWSSessionUseCase(
    private val webSocketExampleGateway: WebSocketExampleGateway,
    dispatcherProvider: DispatcherProvider
) : FlowUseCase<WebSocketMessage>(dispatcherProvider) {
    override suspend fun execute() =
        webSocketExampleGateway.openSession()
}