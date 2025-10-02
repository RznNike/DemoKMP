package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.data.shell.ShellManager
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.ComObjectExampleGateway
import ru.rznnike.demokmp.domain.utils.OperatingSystem

class ComObjectExampleGatewayImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val shellManager: ShellManager
) : ComObjectExampleGateway {
    override suspend fun minimizeAllWindows(): Unit = withContext(dispatcherProvider.io) {
        if (!OperatingSystem.isWindows) return@withContext

        shellManager.apply {
            initWrapper()
            minimizeAllWindows()
            destroyWrapper()
        }
    }
}