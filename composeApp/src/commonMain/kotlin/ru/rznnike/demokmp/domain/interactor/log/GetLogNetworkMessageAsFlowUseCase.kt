package ru.rznnike.demokmp.domain.interactor.log

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.FlowUseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.LogGateway
import ru.rznnike.demokmp.domain.log.LogNetworkMessage
import java.util.*

class GetLogNetworkMessageAsFlowUseCase(
    private val logGateway: LogGateway,
    dispatcherProvider: DispatcherProvider
) : FlowUseCaseWithParams<UUID, LogNetworkMessage?>(dispatcherProvider) {
    override suspend fun execute(parameters: UUID) = logGateway.getLogNetworkMessageAsFlow(parameters)
}