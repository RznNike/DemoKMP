package ru.rznnike.demokmp.app.di

import kotlin.coroutines.ContinuationInterceptor
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import ru.rznnike.demokmp.domain.common.CoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertSame

/**
 * [appModule] is the only place that decides which coroutine dispatcher application code runs
 * on. `verify()` inspects the definitions but not the values, so a swapped `io` / `default`
 * slot would start work on the wrong thread without failing anything.
 *
 * Runs from `commonTest`, so both the desktop and the Android host suites check the mapping.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class DispatcherProviderTest {
    private lateinit var koinApplication: KoinApplication
    private lateinit var koin: Koin

    @BeforeTest
    fun setUp() {
        // `appModule` evaluates `Dispatchers.Main` when the provider is created, and Android
        // host tests have no Main dispatcher installed. A test dispatcher is deterministic and
        // keeps the identity assertions meaningful on both platforms.
        Dispatchers.setMain(StandardTestDispatcher())

        koinApplication = koinApplication { modules(appModule) }
        koin = koinApplication.koin
    }

    @AfterTest
    fun tearDown() {
        koinApplication.close()
        Dispatchers.resetMain()
    }

    @Test
    fun `dispatcher provider maps every slot to the expected dispatcher`() {
        // Act
        val provider = koin.get<DispatcherProvider>()

        // Assert
        assertSame(Dispatchers.Main, provider.ui)
        assertSame(Dispatchers.Default, provider.default)
        assertSame(Dispatchers.IO, provider.io)
        assertSame(Dispatchers.Unconfined, provider.unconfined)
    }

    @Test
    fun `coroutine scope provider maps every slot to the expected dispatcher`() {
        // Act
        val provider = koin.get<CoroutineScopeProvider>()

        // Assert
        assertSame(Dispatchers.Main, provider.ui.dispatcher)
        assertSame(Dispatchers.Default, provider.default.dispatcher)
        assertSame(Dispatchers.IO, provider.io.dispatcher)
        assertSame(Dispatchers.Unconfined, provider.unconfined.dispatcher)
    }
}

private val CoroutineScope.dispatcher: CoroutineDispatcher?
    get() = coroutineContext[ContinuationInterceptor] as? CoroutineDispatcher
