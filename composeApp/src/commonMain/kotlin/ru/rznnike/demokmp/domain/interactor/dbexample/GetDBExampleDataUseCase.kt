package ru.rznnike.demokmp.domain.interactor.dbexample

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.DBExampleGateway
import ru.rznnike.demokmp.domain.model.dbexample.DBExampleData

class GetDBExampleDataUseCase(
    private val dbExampleGateway: DBExampleGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<GetDBExampleDataUseCase.Parameters, GetDBExampleDataUseCase.Result>(dispatcherProvider) {
    override suspend fun execute(parameters: Parameters) = Result(
        data = dbExampleGateway.get(id = parameters.id)
    )

    data class Parameters(
        val id: Long
    )

    data class Result(
        val data: DBExampleData?
    )
}