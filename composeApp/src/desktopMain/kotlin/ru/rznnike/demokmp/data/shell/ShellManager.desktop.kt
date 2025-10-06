package ru.rznnike.demokmp.data.shell

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
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
            PowerShellWrapper.initialize()
            wrapper = PowerShellWrapper()
        } catch (e: Exception) {
            logger.e(e, "PowerShellWrapper initialization failed")
        }
    }

    override suspend fun destroyWrapper(): Unit = wrapperLock.withLock {
        logger.i("Destroying PowerShellWrapper")
        wrapper = null
        PowerShellWrapper.uninitialize()
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