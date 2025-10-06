package ru.rznnike.demokmp.domain.gateway

interface ComObjectExampleGateway {
    suspend fun initShellWrapper()

    suspend fun destroyShellWrapper()

    suspend fun getPCData(): String

    suspend fun openFolderOrFile(path: String)

    suspend fun minimizeAllWindows()
}