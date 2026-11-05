package ru.rznnike.demokmp.app.di

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import ru.rznnike.demokmp.app.dispatcher.event.EventDispatcher
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.error.ErrorHandler
import ru.rznnike.demokmp.data.network.AppApi
import ru.rznnike.demokmp.data.network.AppWebSocketManager
import ru.rznnike.demokmp.data.network.model.RandomImageLinksModel
import ru.rznnike.demokmp.data.network.model.WebSocketMessageModel
import ru.rznnike.demokmp.data.preference.PreferencesManager
import ru.rznnike.demokmp.data.shell.ShellManager
import ru.rznnike.demokmp.data.storage.MainDB
import ru.rznnike.demokmp.data.storage.dao.DBExampleDataDao
import ru.rznnike.demokmp.data.storage.dao.LogMessageDao
import ru.rznnike.demokmp.data.storage.dao.LogNetworkMessageDao
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.*
import ru.rznnike.demokmp.domain.interactor.app.CheckIfAppIsAlreadyRunningUseCase
import ru.rznnike.demokmp.domain.interactor.app.CloseAppSingleInstanceSocketUseCase
import ru.rznnike.demokmp.domain.interactor.chartexample.GetChartSampleDataUseCase
import ru.rznnike.demokmp.domain.interactor.comobjectexample.*
import ru.rznnike.demokmp.domain.interactor.dbexample.*
import ru.rznnike.demokmp.domain.interactor.file.CopyFileUseCase
import ru.rznnike.demokmp.domain.interactor.httpexample.GetRandomImageLinksUseCase
import ru.rznnike.demokmp.domain.interactor.log.*
import ru.rznnike.demokmp.domain.interactor.pdfexample.GetSamplePdfUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.*
import ru.rznnike.demokmp.domain.interactor.wsexample.CloseAppWSSessionUseCase
import ru.rznnike.demokmp.domain.interactor.wsexample.GetAppWSSessionUseCase
import ru.rznnike.demokmp.domain.interactor.wsexample.SendAppWSMessageUseCase
import ru.rznnike.demokmp.domain.model.websocket.WebSocketSessionData
import java.time.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

private class StubAppApi : AppApi {
    override suspend fun getRandomImages(count: Int): RandomImageLinksModel =
        error("Network is not exercised by the DI resolution test")
}

private class StubAppWebSocketManager : AppWebSocketManager {
    override fun getSession(): WebSocketSessionData<WebSocketMessageModel> =
        error("Network is not exercised by the DI resolution test")

    override suspend fun closeSession() = Unit

    override suspend fun sendMessage(message: WebSocketMessageModel) = Unit
}

/**
 * [AppComponentTest] already runs the static `verify()` over the aggregate module list, which
 * checks constructor signatures only. This test starts an isolated Koin instance and resolves
 * every binding, so that dependencies requested inside definition lambdas (for example
 * `get<DispatcherProvider>()` in `DatabaseModule.desktop.kt`) and constructors that throw are
 * caught here instead of at application startup.
 *
 * Hermetic by construction: the platform I/O leaves are overridden with in-memory/stub
 * implementations, so resolution never writes files, opens sockets, or starts network threads.
 *
 * Desktop-only for the same reason as the other Room tests: `sqlite-bundled` cannot load inside
 * Android unit tests on the host JVM.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class KoinModuleTest {
    private lateinit var database: MainDB
    private lateinit var koinApplication: KoinApplication
    private lateinit var koin: Koin

    @BeforeTest
    fun setUp() {
        // MainScope()/Dispatchers.Main are resolved by AppModule; a test dispatcher keeps the
        // resolution deterministic and prevents any launched coroutine from running.
        Dispatchers.setMain(StandardTestDispatcher())

        database = Room
            .inMemoryDatabaseBuilder<MainDB>()
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()

        koinApplication = koinApplication {
            modules(appComponent)
            // Default allowOverride = true, so these replace the platform I/O leaves only.
            modules(
                module {
                    single<MainDB> { database }
                    single<Settings> { MapSettings() }
                    single<AppApi> { StubAppApi() }
                    single<AppWebSocketManager> { StubAppWebSocketManager() }
                }
            )
        }
        koin = koinApplication.koin
    }

    @AfterTest
    fun tearDown() {
        koinApplication.close()
        database.close()
        Dispatchers.resetMain()
    }

    @Test
    fun `app module resolves every binding at runtime`() {
        koin.get<Clock>()
        koin.get<ErrorHandler>()
        koin.get<Notifier>()
        koin.get<EventDispatcher>()
        koin.get<ShellManager>()
        koin.get<CoroutineScopeProvider>()
        koin.get<DispatcherProvider>()
    }

    @Test
    fun `preference module resolves its manager at runtime`() {
        koin.get<PreferencesManager>()
    }

    @Test
    fun `database module resolves every dao at runtime`() {
        koin.get<LogMessageDao>()
        koin.get<LogNetworkMessageDao>()
        koin.get<DBExampleDataDao>()
    }

    @Test
    fun `gateway module resolves every gateway at runtime`() {
        koin.get<AppGateway>()
        koin.get<PreferencesGateway>()
        koin.get<HTTPExampleGateway>()
        koin.get<WebSocketExampleGateway>()
        koin.get<DBExampleGateway>()
        koin.get<PdfExampleGateway>()
        koin.get<ChartExampleGateway>()
        koin.get<LogGateway>()
        koin.get<ComObjectExampleGateway>()
        koin.get<FileGateway>()
    }

    @Test
    fun `interactor module resolves every use case at runtime`() {
        koin.get<CheckIfAppIsAlreadyRunningUseCase>()
        koin.get<CloseAppSingleInstanceSocketUseCase>()

        koin.get<AddLogMessageToDBUseCase>()
        koin.get<AddLogNetworkMessageToDBUseCase>()
        koin.get<GetLogNetworkMessageUseCase>()
        koin.get<GetLogNetworkMessageAsFlowUseCase>()
        koin.get<GetLogUseCase>()
        koin.get<GetNewLogUseCase>()
        koin.get<GetNetworkLogUseCase>()
        koin.get<GetNewNetworkLogUseCase>()
        koin.get<ClearLogUseCase>()
        koin.get<ClearNetworkLogUseCase>()
        koin.get<DeleteOldLogsUseCase>()
        koin.get<SaveLogToFileUseCase>()
        koin.get<SaveTextToFileUseCase>()

        koin.get<GetTestCounterUseCase>()
        koin.get<SetTestCounterUseCase>()
        koin.get<GetLanguageUseCase>()
        koin.get<SetLanguageUseCase>()
        koin.get<GetThemeUseCase>()
        koin.get<SetThemeUseCase>()
        koin.get<GetPrintSettingsUseCase>()
        koin.get<SetPrintSettingsUseCase>()
        koin.get<GetUiScaleUseCase>()
        koin.get<SetUiScaleUseCase>()

        koin.get<GetRandomImageLinksUseCase>()

        koin.get<GetDBExampleDataUseCase>()
        koin.get<GetDBExampleDataListUseCase>()
        koin.get<AddDBExampleDataUseCase>()
        koin.get<DeleteDBExampleDataUseCase>()
        koin.get<DeleteAllDBExampleDataUseCase>()
        koin.get<CloseDBUseCase>()

        koin.get<GetAppWSSessionUseCase>()
        koin.get<CloseAppWSSessionUseCase>()
        koin.get<SendAppWSMessageUseCase>()

        koin.get<GetSamplePdfUseCase>()

        koin.get<GetChartSampleDataUseCase>()

        koin.get<InitShellWrapperUseCase>()
        koin.get<DestroyShellWrapperUseCase>()
        koin.get<GetPCDataUseCase>()
        koin.get<OpenFolderOrFileUseCase>()
        koin.get<MinimizeAllWindowsUseCase>()

        koin.get<CopyFileUseCase>()
    }
}
