package ru.rznnike.demokmp.domain.common

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

/**
 * Substitutes [CoroutineScopeProvider] in tests. Scopes default to [Dispatchers.Unconfined] so
 * launches run eagerly; pass distinct dispatchers to prove which scope a class under test uses.
 */
class FakeCoroutineScopeProvider(
    uiDispatcher: CoroutineDispatcher = Dispatchers.Unconfined,
    ioDispatcher: CoroutineDispatcher = uiDispatcher,
    defaultDispatcher: CoroutineDispatcher = uiDispatcher,
    unconfinedDispatcher: CoroutineDispatcher = uiDispatcher
) : CoroutineScopeProvider {
    private val job = Job()

    override val ui: CoroutineScope = CoroutineScope(uiDispatcher + job)
    override val default: CoroutineScope = CoroutineScope(defaultDispatcher + job)
    override val io: CoroutineScope = CoroutineScope(ioDispatcher + job)
    override val unconfined: CoroutineScope = CoroutineScope(unconfinedDispatcher + job)

    fun cancel() = job.cancel()
}
