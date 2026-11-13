package ru.rznnike.demokmp.launcher

internal expect val pathSeparator: Char

internal expect fun executableDirectory(): String

internal expect fun pathExists(path: String): Boolean

internal expect fun readTextFile(path: String): String?

internal expect fun captureCommandOutput(executable: String, arguments: List<String>): String?

internal expect fun launchDetached(
    executable: String,
    arguments: List<String>,
    workingDirectory: String
): Boolean

internal expect fun showErrorMessage(message: String)
