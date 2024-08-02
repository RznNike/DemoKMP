package ru.rznnike.demokmp.domain.interactor.preferences

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCase
import ru.rznnike.demokmp.domain.gateway.PreferencesGateway

class GetTestCounterUseCase(
    private val preferencesGateway: PreferencesGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<Int>(dispatcherProvider) {
    override suspend fun execute() = preferencesGateway.getTestCounter()
}