package ru.rznnike.demokmp.app.common.viewmodel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import ru.rznnike.demokmp.domain.common.FakeCoroutineScopeProvider
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider

/**
 * Replaces the `Main` dispatcher for ViewModel tests.
 *
 * `viewModelScope` hardcodes `Dispatchers.Main`, so it cannot be replaced by dependency injection —
 * `Dispatchers.setMain` is the only way (see "Setting the Main dispatcher" in the coroutines
 * testing guide). Without it a ViewModel's `init` coroutines run on the Swing EDT on desktop and
 * throw on the Android host JVM.
 *
 * The dispatcher is created **before** [start], so pass it to `runTest(rule.dispatcher)` to share a
 * single scheduler: any `TestDispatcher` created after [start] inherits it automatically.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ViewModelTestRule(
    val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
) {
    /** Defaults to a [TestDispatcher], so `withContext(...)` inside use cases stays on the test thread. */
    val dispatcherProvider = FakeDispatcherProvider(dispatcher)

    /** For [ru.rznnike.demokmp.app.dispatcher.notifier.Notifier] / `EventDispatcher` collaborators. */
    val coroutineScopeProvider = FakeCoroutineScopeProvider(dispatcher)

    fun start() = Dispatchers.setMain(dispatcher)

    fun stop() = Dispatchers.resetMain()
}
