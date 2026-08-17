package ru.rznnike.demokmp.data.gateway

import ru.rznnike.demokmp.domain.gateway.FileGateway
import java.io.File

class FileGatewayImpl : FileGateway {
    override suspend fun copyFile(original: File, copy: File) {
        original.copyTo(
            target = copy,
            overwrite = true
        )
    }
}