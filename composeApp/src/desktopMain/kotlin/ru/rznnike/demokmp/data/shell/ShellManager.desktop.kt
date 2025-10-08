package ru.rznnike.demokmp.data.shell

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import ru.rznnike.demokmp.data.comobject.COMObjectWrapper
import ru.rznnike.demokmp.domain.log.Logger

class ShellManagerImpl : ShellManager {
    private val logger = Logger.withTag("ShellManager")
    private var wrapper: PowerShellWrapper? = null
    private var wrapperLock = Mutex()

    override suspend fun initWrapper(): Unit = wrapperLock.withLock {
        if (wrapper != null) {
            logger.w("PowerShellWrapper was initialized earlier, skipping")
            return
        }

        logger.i("Initializing PowerShellWrapper")
        try {
            COMObjectWrapper.initialize()
            wrapper = PowerShellWrapper()
        } catch (e: Exception) {
            logger.e(e, "PowerShellWrapper initialization failed")
        }
    }

    override suspend fun destroyWrapper(): Unit = wrapperLock.withLock {
        logger.i("Destroying PowerShellWrapper")
        wrapper = null
    }

    override suspend fun getPCData(): String {
        logger.i("Getting PC data")
        var result = ""
        withWrapper {
            result = "CPU %d Mhz (%s) | RAM %.2f Gb".format(
                getCPUSpeedMhz(),
                getCPUArchitecture(),
                getRAMAmountGb()
            )
        }
        logger.i(result)
        return result
    }

    override suspend fun openFolderOrFile(path: String) {
        logger.i("trying to open path: $path")
        withWrapper {
            openFolderOrFile(path)
        }
    }

    override suspend fun minimizeAllWindows() {
        logger.i("Minimizing all windows")
        withWrapper {
            minimizeAll()
        }
    }

    private suspend fun withWrapper(
        action: suspend PowerShellWrapper.() -> Unit
    ): Unit = wrapperLock.withLock {
        wrapper?.action() ?: logger.e("PowerShellWrapper is not initialized")
    }
}

actual fun getShellManager(): ShellManager = ShellManagerImpl()