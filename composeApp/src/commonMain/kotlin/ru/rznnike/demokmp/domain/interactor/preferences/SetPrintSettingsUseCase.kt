package ru.rznnike.demokmp.domain.interactor.preferences

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCaseWithParams
import ru.rznnike.demokmp.domain.gateway.PreferencesGateway
import ru.rznnike.demokmp.domain.model.print.PrintSettings

class SetPrintSettingsUseCase(
    private val preferencesGateway: PreferencesGateway,
    dispatcherProvider: DispatcherProvider
) : UseCaseWithParams<PrintSettings, Unit>(dispatcherProvider) {
    override suspend fun execute(parameters: PrintSettings) = preferencesGateway.setPrintSettings(parameters)
}