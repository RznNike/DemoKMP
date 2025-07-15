package ru.rznnike.demokmp.domain.interactor.log

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.FlowUseCase
import ru.rznnike.demokmp.domain.gateway.LogGateway
import ru.rznnike.demokmp.domain.log.LogMessage

class GetLogUseCase(
    private val logGateway: LogGateway,
    dispatcherProvider: DispatcherProvider
) : FlowUseCase<List<LogMessage>>(dispatcherProvider) {
    override suspend fun execute() = logGateway.getLog()
}