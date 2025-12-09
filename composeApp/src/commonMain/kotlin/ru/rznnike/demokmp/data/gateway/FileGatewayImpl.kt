package ru.rznnike.demokmp.data.gateway

import kotlinx.coroutines.withContext
import ru.rznnike.demokmp.domain.common.DispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FileGateway
import java.io.File

class FileGatewayImpl(
    private val dispatcherProvider: DispatcherProvider
) : FileGateway {
    override suspend fun copyFile(original: File, copy: File): Unit = withContext(dispatcherProvider.io) {
        original.copyTo(
            target = copy,
            overwrite = true
        )
    }
}