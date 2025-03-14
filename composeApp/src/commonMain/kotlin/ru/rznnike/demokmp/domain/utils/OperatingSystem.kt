package ru.rznnike.demokmp.domain.utils

import ru.rznnike.demokmp.BuildKonfig

enum class OperatingSystem(
    val tag: String
) {
    WINDOWS("windows"),
    LINUX("linux");

    companion object {
        val current = OperatingSystem[BuildKonfig.OS]
        val isWindows = current == WINDOWS
        val isLinux = current == LINUX

        operator fun get(tag: String) = entries.find { it.tag == tag } ?: WINDOWS
    }
}