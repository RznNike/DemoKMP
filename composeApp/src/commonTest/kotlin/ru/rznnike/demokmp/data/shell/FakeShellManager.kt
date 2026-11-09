package ru.rznnike.demokmp.data.shell

/**
 * Test double for [ShellManager].
 *
 * Every call is recorded in [calls] so tests can assert which command reached the shell manager
 * and in which order it ran relative to other callbacks.
 * [failure] makes every call throw it before recording, so error branches are reachable.
 */
class FakeShellManager : ShellManager {
    val calls = mutableListOf<String>()
    var pcData: String = ""
    var failure: Throwable? = null

    override suspend fun initWrapper() = record("initWrapper")

    override suspend fun destroyWrapper() = record("destroyWrapper")

    override suspend fun getPCData(): String {
        record("getPCData")
        return pcData
    }

    override suspend fun openFolderOrFile(path: String) = record("openFolderOrFile:$path")

    override suspend fun minimizeAllWindows() = record("minimizeAllWindows")

    private fun record(call: String) {
        failure?.let { throw it }
        calls += call
    }
}
