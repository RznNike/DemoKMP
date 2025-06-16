package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.withContext
import okhttp3.internal.closeQuietly
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.data.utils.DataConstants
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.AppGateway
import ru.rznnike.demokmp.domain.log.Logger
import ru.rznnike.demokmp.domain.model.common.LauncherConfig
import ru.rznnike.demokmp.domain.model.common.toLauncherConfig
import ru.rznnike.demokmp.domain.utils.OperatingSystem
import java.io.File
import java.net.ServerSocket

class AppGatewayImpl(
    private val dispatcherProvider: DispatcherProvider
) : AppGateway {
    private var singleInstanceSocket: ServerSocket? = null

    override suspend fun checkIfAppIsAlreadyRunning(): Boolean = withContext(dispatcherProvider.io) {
        if (BuildKonfig.RUN_FROM_IDE || OperatingSystem.isAndroid) return@withContext false

        Logger.d("Checking if app is already running")
        try {
            val port = getLauncherConfig().singleInstancePort
            singleInstanceSocket = ServerSocket(port)
            false
        } catch (_: Exception) {
            Logger.e("App is already running!")
            true
        }
    }

    private fun getLauncherConfig(): LauncherConfig {
        val configFile = File(DataConstants.LAUNCHER_CONFIGURATION_PATH)
        return if (configFile.exists()) {
            configFile.readText().toLauncherConfig()
        } else LauncherConfig()
    }

    override suspend fun closeAppSingleInstanceSocket(): Unit = withContext(dispatcherProvider.io) {
        if (BuildKonfig.RUN_FROM_IDE || OperatingSystem.isAndroid) return@withContext

        singleInstanceSocket?.closeQuietly()
    }
}