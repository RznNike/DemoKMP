package ru.rznnike.demokmp.domain.common.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import ru.rznnike.demokmp.domain.common.DispatcherProvider

abstract class FlowUseCase<R>(private val dispatcherProvider: DispatcherProvider) {
    suspend operator fun invoke(): Flow<R> {
        return execute().flowOn(dispatcherProvider.default)
    }

    @Throws(RuntimeException::class)
    protected abstract suspend fun execute(): Flow<R>
}
