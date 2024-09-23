package ru.rznnike.demokmp.domain.interactor.dbexample

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCase
import ru.rznnike.demokmp.domain.gateway.DBExampleGateway
import ru.rznnike.demokmp.domain.model.dbexample.DBExampleData

class GetDBExampleDataListUseCase(
    private val dbExampleGateway: DBExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<List<DBExampleData>>(dispatcherProvider) {
    override suspend fun execute() = dbExampleGateway.getAll()
}