@file:OptIn(ExperimentalForeignApi::class)

package ru.rznnike.demokmp.launcher

import kotlinx.cinterop.*
import platform.windows.*

private const val EXECUTABLE_PATH_BUFFER_SIZE = 32768
private const val FILE_READ_BUFFER_SIZE = 8192

internal actual val pathSeparator: Char = '\\'

internal actual fun executableDirectory(): String = memScoped {
    val buffer = allocArray<UShortVar>(EXECUTABLE_PATH_BUFFER_SIZE)
    val length = GetModuleFileNameW(null, buffer, EXECUTABLE_PATH_BUFFER_SIZE.convert())
    val fullPath = if (length > 0u) buffer.toKString() else ""
    val directory = fullPath.substringBeforeLast('\\', missingDelimiterValue = "")
    when {
        directory.isEmpty() -> "."
        directory.endsWith(':') -> "$directory\\"
        else -> directory
    }
}

internal actual fun pathExists(path: String): Boolean =
    GetFileAttributesW(path) != INVALID_FILE_ATTRIBUTES

internal actual fun readTextFile(path: String): String? {
    val handle = CreateFileW(
        lpFileName = path,
        dwDesiredAccess = GENERIC_READ.convert(),
        dwShareMode = FILE_SHARE_READ.convert(),
        lpSecurityAttributes = null,
        dwCreationDisposition = OPEN_EXISTING.convert(),
        dwFlagsAndAttributes = FILE_ATTRIBUTE_NORMAL.convert(),
        hTemplateFile = null
    )
    if (handle == INVALID_HANDLE_VALUE) return null
    try {
        return readAllFromHandle(handle)
    } finally {
        CloseHandle(handle)
    }
}

internal actual fun captureCommandOutput(executable: String, arguments: List<String>): String? = memScoped {
    val commandLine = buildWindowsCommandLine(executable, arguments)
    val securityAttributes = alloc<SECURITY_ATTRIBUTES>().apply {
        nLength = sizeOf<SECURITY_ATTRIBUTES>().convert()
        bInheritHandle = TRUE
        lpSecurityDescriptor = null
    }
    val readHandle = alloc<COpaquePointerVar>()
    val writeHandle = alloc<COpaquePointerVar>()
    if (CreatePipe(readHandle.ptr, writeHandle.ptr, securityAttributes.ptr, 0u) == 0) {
        return@memScoped null
    }
    SetHandleInformation(readHandle.value, HANDLE_FLAG_INHERIT.convert(), 0u)

    val startupInfo = alloc<STARTUPINFOW>().apply {
        cb = sizeOf<STARTUPINFOW>().convert()
        dwFlags = STARTF_USESTDHANDLES.convert()
        hStdInput = null
        hStdOutput = writeHandle.value
        hStdError = writeHandle.value
    }
    val processInfo = alloc<PROCESS_INFORMATION>()
    val created = CreateProcessW(
        lpApplicationName = null,
        lpCommandLine = allocWideString(commandLine),
        lpProcessAttributes = null,
        lpThreadAttributes = null,
        bInheritHandles = TRUE,
        dwCreationFlags = CREATE_NO_WINDOW.convert(),
        lpEnvironment = null,
        lpCurrentDirectory = null,
        lpStartupInfo = startupInfo.ptr,
        lpProcessInformation = processInfo.ptr
    )
    CloseHandle(writeHandle.value)
    if (created == 0) {
        CloseHandle(readHandle.value)
        return@memScoped null
    }

    val output = readAllFromHandle(readHandle.value)
    WaitForSingleObject(processInfo.hProcess, INFINITE.convert())
    CloseHandle(processInfo.hProcess)
    CloseHandle(processInfo.hThread)
    CloseHandle(readHandle.value)
    output
}

internal actual fun launchDetached(
    executable: String,
    arguments: List<String>,
    workingDirectory: String
): Boolean = memScoped {
    val commandLine = buildWindowsCommandLine(executable, arguments)
    val startupInfo = alloc<STARTUPINFOW>().apply {
        cb = sizeOf<STARTUPINFOW>().convert()
        dwFlags = STARTF_USESHOWWINDOW.convert()
        wShowWindow = SW_HIDE.toUShort()
    }
    val processInfo = alloc<PROCESS_INFORMATION>()
    val created = CreateProcessW(
        lpApplicationName = null,
        lpCommandLine = allocWideString(commandLine),
        lpProcessAttributes = null,
        lpThreadAttributes = null,
        bInheritHandles = FALSE,
        dwCreationFlags = (CREATE_NO_WINDOW or DETACHED_PROCESS or CREATE_NEW_PROCESS_GROUP).convert(),
        lpEnvironment = null,
        lpCurrentDirectory = allocWideString(workingDirectory),
        lpStartupInfo = startupInfo.ptr,
        lpProcessInformation = processInfo.ptr
    )
    if (created != 0) {
        CloseHandle(processInfo.hThread)
        CloseHandle(processInfo.hProcess)
    }
    created != 0
}

internal actual fun showErrorMessage(message: String) {
    MessageBoxW(
        hWnd = null,
        lpText = message,
        lpCaption = "Launch error",
        uType = (MB_OK or MB_ICONERROR).convert()
    )
}

private fun readAllFromHandle(handle: HANDLE?): String = memScoped {
    val output = StringBuilder()
    val buffer = ByteArray(FILE_READ_BUFFER_SIZE)
    val bytesRead = alloc<UIntVar>()
    buffer.usePinned { pinned ->
        while (true) {
            val read = ReadFile(
                hFile = handle,
                lpBuffer = pinned.addressOf(0),
                nNumberOfBytesToRead = buffer.size.toUInt(),
                lpNumberOfBytesRead = bytesRead.ptr,
                lpOverlapped = null
            )
            if (read == 0 || bytesRead.value == 0u) break
            output.append(buffer.decodeToString(0, bytesRead.value.toInt()))
        }
    }
    output.toString()
}

private fun MemScope.allocWideString(value: String): CPointer<UShortVar> {
    val buffer = allocArray<UShortVar>(value.length + 1)
    value.forEachIndexed { index, char -> buffer[index] = char.code.toUShort() }
    buffer[value.length] = 0u
    return buffer
}

private fun buildWindowsCommandLine(executable: String, arguments: List<String>): String =
    (listOf(executable) + arguments).joinToString(" ") { quoteWindowsArgument(it) }

private fun quoteWindowsArgument(argument: String): String =
    if (argument.isEmpty() || argument.any { it.isWhitespace() } || '"' in argument) {
        "\"" + argument.replace("\"", "\\\"") + "\""
    } else {
        argument
    }
