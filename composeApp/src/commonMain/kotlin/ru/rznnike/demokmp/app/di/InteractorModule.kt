package ru.rznnike.demokmp.app.di

import org.koin.dsl.module
import ru.rznnike.demokmp.domain.interactor.app.CheckIfAppIsAlreadyRunningUseCase
import ru.rznnike.demokmp.domain.interactor.app.CloseAppSingleInstanceSocketUseCase
import ru.rznnike.demokmp.domain.interactor.chartexample.GetChartSampleDataUseCase
import ru.rznnike.demokmp.domain.interactor.comobjectexample.MinimizeAllWindowsUseCase
import ru.rznnike.demokmp.domain.interactor.dbexample.*
import ru.rznnike.demokmp.domain.interactor.httpexample.GetRandomImageLinksUseCase
import ru.rznnike.demokmp.domain.interactor.log.AddLogMessageToDBUseCase
import ru.rznnike.demokmp.domain.interactor.log.AddLogNetworkMessageToDBUseCase
import ru.rznnike.demokmp.domain.interactor.log.ClearLogUseCase
import ru.rznnike.demokmp.domain.interactor.log.ClearNetworkLogUseCase
import ru.rznnike.demokmp.domain.interactor.log.DeleteOldLogsUseCase
import ru.rznnike.demokmp.domain.interactor.log.GetLogNetworkMessageAsFlowUseCase
import ru.rznnike.demokmp.domain.interactor.log.GetLogNetworkMessageUseCase
import ru.rznnike.demokmp.domain.interactor.log.GetLogUseCase
import ru.rznnike.demokmp.domain.interactor.log.GetNetworkLogUseCase
import ru.rznnike.demokmp.domain.interactor.log.GetNewLogUseCase
import ru.rznnike.demokmp.domain.interactor.log.GetNewNetworkLogUseCase
import ru.rznnike.demokmp.domain.interactor.pdfexample.GetSamplePdfUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.*
import ru.rznnike.demokmp.domain.interactor.wsexample.CloseAppWSSessionUseCase
import ru.rznnike.demokmp.domain.interactor.wsexample.GetAppWSSessionUseCase
import ru.rznnike.demokmp.domain.interactor.wsexample.SendAppWSMessageUseCase

internal val interactorModule = module {
    single { CheckIfAppIsAlreadyRunningUseCase(get(), get()) }
    single { CloseAppSingleInstanceSocketUseCase(get(), get()) }

    single { AddLogMessageToDBUseCase(get(), get()) }
    single { AddLogNetworkMessageToDBUseCase(get(), get()) }
    single { GetLogNetworkMessageUseCase(get(), get()) }
    single { GetLogNetworkMessageAsFlowUseCase(get(), get()) }
    single { GetLogUseCase(get(), get()) }
    single { GetNewLogUseCase(get(), get()) }
    single { GetNetworkLogUseCase(get(), get()) }
    single { GetNewNetworkLogUseCase(get(), get()) }
    single { ClearLogUseCase(get(), get()) }
    single { ClearNetworkLogUseCase(get(), get()) }
    single { DeleteOldLogsUseCase(get(), get()) }

    single { GetTestCounterUseCase(get(), get()) }
    single { SetTestCounterUseCase(get(), get()) }
    single { GetLanguageUseCase(get(), get()) }
    single { SetLanguageUseCase(get(), get()) }
    single { GetThemeUseCase(get(), get()) }
    single { SetThemeUseCase(get(), get()) }
    single { GetPrintSettingsUseCase(get(), get()) }
    single { SetPrintSettingsUseCase(get(), get()) }

    single { GetRandomImageLinksUseCase(get(), get()) }

    single { GetDBExampleDataUseCase(get(), get()) }
    single { GetDBExampleDataListUseCase(get(), get()) }
    single { AddDBExampleDataUseCase(get(), get()) }
    single { DeleteDBExampleDataUseCase(get(), get()) }
    single { DeleteAllDBExampleDataUseCase(get(), get()) }
    single { CloseDBUseCase(get(), get()) }

    single { GetAppWSSessionUseCase(get(), get()) }
    single { CloseAppWSSessionUseCase(get(), get()) }
    single { SendAppWSMessageUseCase(get(), get()) }

    single { GetSamplePdfUseCase(get(), get()) }

    single { GetChartSampleDataUseCase(get(), get()) }

    single { MinimizeAllWindowsUseCase(get(), get()) }
}