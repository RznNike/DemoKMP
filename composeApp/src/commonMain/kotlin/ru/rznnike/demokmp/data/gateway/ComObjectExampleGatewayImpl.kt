package ru.rznnike.demokmp.data.gateway

import ru.rznnike.demokmp.app.utils.destroyCOMLibrary
import ru.rznnike.demokmp.app.utils.initCOMLibrary
import ru.rznnike.demokmp.data.shell.ShellManager
import ru.rznnike.demokmp.domain.gateway.ComObjectExampleGateway
import ru.rznnike.demokmp.domain.utils.OperatingSystem

class ComObjectExampleGatewayImpl(
    private val shellManager: ShellManager
) : ComObjectExampleGateway {
    override suspend fun initShellWrapper() {
        if (!OperatingSystem.isWindows) return

        initCOMLibrary()
        shellManager.initWrapper()
    }

    override suspend fun destroyShellWrapper() {
        if (!OperatingSystem.isWindows) return

        shellManager.destroyWrapper()
        destroyCOMLibrary()
    }

    override suspend fun getPCData(): String {
        if (!OperatingSystem.isWindows) return ""

        return shellManager.getPCData()
    }

    override suspend fun openFolderOrFile(path: String) {
        if (!OperatingSystem.isWindows) return

        shellManager.openFolderOrFile(path)
    }

    override suspend fun minimizeAllWindows() {
        if (!OperatingSystem.isWindows) return

        shellManager.minimizeAllWindows()
    }
}