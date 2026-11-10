package ru.rznnike.demokmp.app.di

import android.app.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import org.koin.dsl.module
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
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
import ru.rznnike.demokmp.domain.gateway.AppGateway
import ru.rznnike.demokmp.domain.gateway.ChartExampleGateway
import ru.rznnike.demokmp.domain.gateway.ComObjectExampleGateway
import ru.rznnike.demokmp.domain.gateway.DBExampleGateway
import ru.rznnike.demokmp.domain.gateway.FileGateway
import ru.rznnike.demokmp.domain.gateway.HTTPExampleGateway
import ru.rznnike.demokmp.domain.gateway.LogGateway
import ru.rznnike.demokmp.domain.gateway.PdfExampleGateway
import ru.rznnike.demokmp.domain.gateway.PreferencesGateway
import ru.rznnike.demokmp.domain.gateway.WebSocketExampleGateway
import ru.rznnike.demokmp.domain.interactor.app.CheckIfAppIsAlreadyRunningUseCase
import ru.rznnike.demokmp.domain.interactor.app.CloseAppSingleInstanceSocketUseCase
import ru.rznnike.demokmp.domain.interactor.chartexample.GetChartSampleDataUseCase
import ru.rznnike.demokmp.domain.interactor.comobjectexample.DestroyShellWrapperUseCase
import ru.rznnike.demokmp.domain.interactor.comobjectexample.GetPCDataUseCase
import ru.rznnike.demokmp.domain.interactor.comobjectexample.InitShellWrapperUseCase
import ru.rznnike.demokmp.domain.interactor.comobjectexample.MinimizeAllWindowsUseCase
import ru.rznnike.demokmp.domain.interactor.comobjectexample.OpenFolderOrFileUseCase
import ru.rznnike.demokmp.domain.interactor.dbexample.AddDBExampleDataUseCase
import ru.rznnike.demokmp.domain.interactor.dbexample.CloseDBUseCase
import ru.rznnike.demokmp.domain.interactor.dbexample.DeleteAllDBExampleDataUseCase
import ru.rznnike.demokmp.domain.interactor.dbexample.DeleteDBExampleDataUseCase
import ru.rznnike.demokmp.domain.interactor.dbexample.GetDBExampleDataListUseCase
import ru.rznnike.demokmp.domain.interactor.dbexample.GetDBExampleDataUseCase
import ru.rznnike.demokmp.domain.interactor.file.CopyFileUseCase
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
import ru.rznnike.demokmp.domain.interactor.log.SaveLogToFileUseCase
import ru.rznnike.demokmp.domain.interactor.log.SaveTextToFileUseCase
import ru.rznnike.demokmp.domain.interactor.pdfexample.GetSamplePdfUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.GetLanguageUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.GetPrintSettingsUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.GetTestCounterUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.GetThemeUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.GetUiScaleUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetLanguageUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetPrintSettingsUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetTestCounterUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetThemeUseCase
import ru.rznnike.demokmp.domain.interactor.preferences.SetUiScaleUseCase
import ru.rznnike.demokmp.domain.interactor.wsexample.CloseAppWSSessionUseCase
import ru.rznnike.demokmp.domain.interactor.wsexample.GetAppWSSessionUseCase
import ru.rznnike.demokmp.domain.interactor.wsexample.SendAppWSMessageUseCase
import ru.rznnike.demokmp.domain.model.websocket.WebSocketSessionData
import java.time.Clock
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
 * Runs under Robolectric so `androidApplication()` resolves a genuine `Application`
 * and `Room.databaseBuilder(context, name)` can actually open a database.
 *
 * `application = Application::class` is deliberate: the manifest declares
 * `ru.rznnike.demokmp.app.App`, whose `onCreate()` starts the **global** Koin context.
 * Using the plain `Application` keeps this test on an isolated `koinApplication`.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
// API 37 is the module's targetSdk; the test JVM is Java 21 (see the
// `javaLauncher` block in composeApp/build.gradle.kts), which API 37 requires.
@Config(sdk = [37], application = Application::class)
class KoinModuleAndroidTest {
    private lateinit var koinApplication: KoinApplication
    private lateinit var koin: Koin
    private lateinit var database: MainDB

    @BeforeTest
    fun setUp() {
        // AppModule's CoroutineScopeProvider uses MainScope(); a test dispatcher keeps
        // Dispatchers.Main resolvable and prevents any launched coroutine from running.
        Dispatchers.setMain(StandardTestDispatcher())

        val application = RuntimeEnvironment.getApplication()
        koinApplication = koinApplication {
            androidContext(application)
            modules(appComponent)
            // Default allowOverride = true: replaces only the two network leaves.
            modules(
                module {
                    single<AppApi> { StubAppApi() }
                    single<AppWebSocketManager> { StubAppWebSocketManager() }
                }
            )
        }
        koin = koinApplication.koin
        // Resolved eagerly so the real Android Room definition (and the
        // get<DispatcherProvider>() call inside its lambda) is always exercised.
        database = koin.get()
    }

    @AfterTest
    fun tearDown() {
        if (::database.isInitialized) database.close()
        if (::koinApplication.isInitialized) koinApplication.close()
        Dispatchers.resetMain()
    }

    @Test
    fun `android app module resolves every binding at runtime`() {
        koin.get<Clock>()
        koin.get<ErrorHandler>()
        koin.get<Notifier>()
        koin.get<EventDispatcher>()
        koin.get<ShellManager>()
        koin.get<CoroutineScopeProvider>()
        koin.get<DispatcherProvider>()
    }

    @Test
    fun `android preference module resolves the real shared preferences manager`() {
        val preferencesManager = koin.get<PreferencesManager>()

        preferencesManager.testCounter.set(ANALYTICS_TEST_COUNTER)

        assertEquals(ANALYTICS_TEST_COUNTER, preferencesManager.testCounter.get())
    }

    @Test
    fun `android database module resolves the real room database and every dao`() = runTest {
        koin.get<LogMessageDao>()
        koin.get<LogNetworkMessageDao>()
        koin.get<DBExampleDataDao>()

        // Proves the framework-SQLite database really opens (build() alone is lazy).
        assertTrue(koin.get<LogMessageDao>().getAll().isEmpty())
    }

    @Test
    fun `android gateway module resolves every gateway at runtime`() {
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
    fun `android interactor module resolves every use case at runtime`() {
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

    private companion object {
        const val ANALYTICS_TEST_COUNTER = 7
    }
}
