package ru.rznnike.demokmp.domain.interactor.wsexample

import ru.rznnike.demokmp.data.network.websocket.WebSocketData
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCase
import ru.rznnike.demokmp.domain.gateway.WebSocketExampleGateway
import ru.rznnike.demokmp.domain.model.wsexample.WebSocketMessage

class OpenAppWSUseCase(
    private val webSocketExampleGateway: WebSocketExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<WebSocketData<WebSocketMessage>>(dispatcherProvider) {
    override suspend fun execute() =
        webSocketExampleGateway.openAppWS()
}