package ru.rznnike.demokmp.domain.gateway

import java.io.File

interface FileGateway {
    suspend fun copyFile(original: File, copy: File)
}