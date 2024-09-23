package ru.rznnike.demokmp.domain.interactor.dbexample

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.DBExampleGateway
import ru.rznnike.demokmp.domain.model.dbexample.DBExampleData

class AddDBExampleDataUseCase(
    private val dbExampleGateway: DBExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<DBExampleData, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: DBExampleData) = dbExampleGateway.add(parameters)
}