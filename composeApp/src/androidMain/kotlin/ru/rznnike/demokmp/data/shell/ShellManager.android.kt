package ru.rznnike.demokmp.data.shell

class ShellManagerImpl : ShellManager {
    override suspend fun initWrapper() = Unit

    override suspend fun destroyWrapper() = Unit

    override suspend fun getPCData() = ""

    override suspend fun openFolderOrFile(path: String) = Unit

    override suspend fun minimizeAllWindows() = Unit
}

actual fun getShellManager(): ShellManager = ShellManagerImpl()