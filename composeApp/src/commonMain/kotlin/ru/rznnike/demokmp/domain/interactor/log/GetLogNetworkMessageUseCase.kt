package ru.rznnike.demokmp.domain.interactor.log

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.LogGateway
import ru.rznnike.demokmp.domain.log.LogNetworkMessage
import java.util.UUID

class GetLogNetworkMessageUseCase(
    private val logGateway: LogGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<UUID, GetLogNetworkMessageUseCase.Result>(dispatcherProvider) {
    override suspend fun execute(parameters: UUID) = Result(
        message = logGateway.getLogNetworkMessage(parameters)
    )

    data class Result(
        val message: LogNetworkMessage?
    )
}