package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.app.utils.destroyCOMLibrary
import ru.rznnike.demokmp.app.utils.initCOMLibrary
import ru.rznnike.demokmp.data.shell.ShellManager
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.ComObjectExampleGateway
import ru.rznnike.demokmp.domain.utils.OperatingSystem

class ComObjectExampleGatewayImpl(
    private val dispatcherProvider: DispatcherProvider,
    private val shellManager: ShellManager
) : ComObjectExampleGateway {
    override suspend fun initShellWrapper(): Unit = withContext(dispatcherProvider.io) {
        if (!OperatingSystem.isWindows) return@withContext

        initCOMLibrary()
        shellManager.initWrapper()
    }

    override suspend fun destroyShellWrapper(): Unit = withContext(dispatcherProvider.io) {
        if (!OperatingSystem.isWindows) return@withContext

        shellManager.destroyWrapper()
        destroyCOMLibrary()
    }

    override suspend fun getPCData(): String = withContext(dispatcherProvider.io) {
        if (!OperatingSystem.isWindows) return@withContext ""

        shellManager.getPCData()
    }

    override suspend fun openFolderOrFile(path: String): Unit = withContext(dispatcherProvider.io) {
        if (!OperatingSystem.isWindows) return@withContext

        shellManager.openFolderOrFile(path)
    }

    override suspend fun minimizeAllWindows(): Unit = withContext(dispatcherProvider.io) {
        if (!OperatingSystem.isWindows) return@withContext

        shellManager.minimizeAllWindows()
    }
}