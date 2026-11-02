package ru.rznnike.demokmp.data.gateway

import ru.rznnike.demokmp.app.utils.destroyCOMLibrary
import ru.rznnike.demokmp.app.utils.initCOMLibrary
import ru.rznnike.demokmp.data.shell.ShellManager
import ru.rznnike.demokmp.domain.gateway.ComObjectExampleGateway
import ru.rznnike.demokmp.domain.utils.OperatingSystem

class ComObjectExampleGatewayImpl(
    private val shellManager: ShellManager,
    private val isWindows: Boolean = OperatingSystem.isWindows,
    private val initComCallback: () -> Unit = ::initCOMLibrary,
    private val destroyComCallback: () -> Unit = ::destroyCOMLibrary
) : ComObjectExampleGateway {
    override suspend fun initShellWrapper() {
        if (!isWindows) return

        initComCallback()
        shellManager.initWrapper()
    }

    override suspend fun destroyShellWrapper() {
        if (!isWindows) return

        shellManager.destroyWrapper()
        destroyComCallback()
    }

    override suspend fun getPCData(): String {
        if (!isWindows) return ""

        return shellManager.getPCData()
    }

    override suspend fun openFolderOrFile(path: String) {
        if (!isWindows) return

        shellManager.openFolderOrFile(path)
    }

    override suspend fun minimizeAllWindows() {
        if (!isWindows) return

        shellManager.minimizeAllWindows()
    }
}