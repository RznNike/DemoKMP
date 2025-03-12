package ru.rznnike.demokmp.domain.interactor.preferences

import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.common.interactor.UseCase
import ru.rznnike.demokmp.domain.gateway.PreferencesGateway
import ru.rznnike.demokmp.domain.model.print.PrintSettings

class GetPrintSettingsUseCase(
    private val preferencesGateway: PreferencesGateway,
    dispatcherProvider: DispatcherProvider
) : UseCase<PrintSettings>(dispatcherProvider) {
    override suspend fun execute() = preferencesGateway.getPrintSettings()
}