@file:OptIn(ExperimentalForeignApi::class)

package ru.rznnike.demokmp.launcher

import kotlinx.cinterop.*
import platform.posix.*

private const val EXECUTABLE_PATH_BUFFER_SIZE = 4096
private const val FILE_READ_BUFFER_SIZE = 8192
private const val SAFE_POSIX_ARGUMENT_CHARACTERS = "._-/:=@+,"

internal actual val pathSeparator: Char = '/'

internal actual fun executableDirectory(): String = memScoped {
    val buffer = allocArray<ByteVar>(EXECUTABLE_PATH_BUFFER_SIZE)
    val length = readlink("/proc/self/exe", buffer, (EXECUTABLE_PATH_BUFFER_SIZE - 1).toULong())
    if (length <= 0) return@memScoped "."
    buffer[length.toInt()] = 0.toByte()
    buffer.toKString().substringBeforeLast('/', missingDelimiterValue = ".")
}

internal actual fun pathExists(path: String): Boolean = access(path, F_OK) == 0

internal actual fun readTextFile(path: String): String? {
    val file = fopen(path, "rb") ?: return null
    try {
        val output = StringBuilder()
        val buffer = ByteArray(FILE_READ_BUFFER_SIZE)
        buffer.usePinned { pinned ->
            while (true) {
                val bytesRead = fread(pinned.addressOf(0), 1uL, buffer.size.toULong(), file)
                if (bytesRead == 0uL) break
                output.append(buffer.decodeToString(0, bytesRead.toInt()))
            }
        }
        return output.toString()
    } finally {
        fclose(file)
    }
}

internal actual fun captureCommandOutput(executable: String, arguments: List<String>): String? {
    val command = buildPosixCommand(
        executable = executable,
        arguments = arguments,
        redirectStandardError = true
    )
    val process = popen(command, "r") ?: return null
    try {
        val output = StringBuilder()
        memScoped {
            val line = allocArray<ByteVar>(FILE_READ_BUFFER_SIZE)
            while (fgets(line, FILE_READ_BUFFER_SIZE, process) != null) {
                output.append(line.toKString())
            }
        }
        return output.toString()
    } finally {
        pclose(process)
    }
}

internal actual fun windowlessJavaCommand(
    javaCommand: String,
    pathExists: (String) -> Boolean
): String = javaCommand

internal actual fun launchDetached(
    executable: String,
    arguments: List<String>,
    workingDirectory: String
): Boolean {
    val command = "cd ${quotePosixArgument(workingDirectory)} && nohup ${buildPosixCommand(executable, arguments)} </dev/null >/dev/null 2>&1 &"
    return system(command) == 0
}

internal actual fun showErrorMessage(message: String) {
    val quotedMessage = quotePosixArgument(message)
    val commands = listOf(
        "kdialog --title \"Launch error\" --error $quotedMessage",
        "xdialog --title \"Launch error\" --msgbox $quotedMessage",
        "zenity --title \"Launch error\" --error --text=$quotedMessage",
        "fly-dialog --title \"Launch error\" --error $quotedMessage"
    )
    for (command in commands) {
        if (system("$command >/dev/null 2>&1") == 0) return
    }
}

private fun buildPosixCommand(
    executable: String,
    arguments: List<String>,
    redirectStandardError: Boolean = false
): String {
    val command = (listOf(executable) + arguments).joinToString(" ") { quotePosixArgument(it) }
    return if (redirectStandardError) "$command 2>&1" else command
}

private fun quotePosixArgument(argument: String): String =
    if (argument.isNotEmpty() && argument.all { it.isLetterOrDigit() || it in SAFE_POSIX_ARGUMENT_CHARACTERS }) {
        argument
    } else {
        "'" + argument.replace("'", "'\\''") + "'"
    }
