package ru.rznnike.demokmp.domain.interactor.wsexample

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.WebSocketExampleGateway

class OpenAppWSSessionUseCase(
    private val webSocketExampleGateway: WebSocketExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<OpenAppWSSessionUseCase.Parameters, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: Parameters) =
        webSocketExampleGateway.openSession(
            onMessage = parameters.onMessage
        )

    data class Parameters(
        val onMessage: (String) -> Unit
    )
}