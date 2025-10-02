package ru.rznnike.demokmp.data.shell

interface ShellManager {
    suspend fun initWrapper()

    suspend fun destroyWrapper()

    suspend fun minimizeAllWindows()
}

expect fun getShellManager(): ShellManager