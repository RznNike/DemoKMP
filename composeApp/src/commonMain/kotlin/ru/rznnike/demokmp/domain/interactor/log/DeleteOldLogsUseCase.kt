package ru.rznnike.demokmp.domain.interactor.log

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.LogGateway
import ru.rznnike.demokmp.domain.log.extension.DatabaseLoggerExtension.LogsRetentionMode

class DeleteOldLogsUseCase(
    private val logGateway: LogGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<LogsRetentionMode, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: LogsRetentionMode) = logGateway.deleteOldLogs(parameters)
}