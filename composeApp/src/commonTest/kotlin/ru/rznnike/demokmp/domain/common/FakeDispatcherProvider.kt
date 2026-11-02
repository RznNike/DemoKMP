package ru.rznnike.demokmp.domain.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/**
 * Substitutes [DispatcherProvider] in tests. Defaults to [Dispatchers.Unconfined], which runs
 * eagerly — sufficient for code under test that performs no delays (no scheduler plumbing needed).
 */
class FakeDispatcherProvider(
    dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
) : DispatcherProvider {
    override val ui: CoroutineDispatcher = dispatcher
    override val default: CoroutineDispatcher = dispatcher
    override val io: CoroutineDispatcher = dispatcher
    override val unconfined: CoroutineDispatcher = dispatcher
}
