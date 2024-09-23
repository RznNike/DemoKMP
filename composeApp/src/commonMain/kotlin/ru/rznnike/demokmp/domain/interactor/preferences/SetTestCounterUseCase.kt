package ru.rznnike.demokmp.domain.interactor.preferences

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.PreferencesGateway

class SetTestCounterUseCase(
    private val preferencesGateway: PreferencesGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<Int, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: Int) = preferencesGateway.setTestCounter(parameters)
}