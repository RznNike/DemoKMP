package ru.rznnike.demokmp.domain.interactor.dbexample

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCase
import ru.rznnike.demokmp.domain.gateway.DBExampleGateway

class CloseDBUseCase(
    private val dbExampleGateway: DBExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<Unit>(dispatcherProvider) {
    override suspend fun execute() = dbExampleGateway.closeDB()
}