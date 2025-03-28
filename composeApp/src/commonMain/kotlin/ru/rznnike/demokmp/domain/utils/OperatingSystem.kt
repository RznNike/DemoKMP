package ru.rznnike.demokmp.domain.utils

import ru.rznnike.demokmp.BuildKonfig

enum class OperatingSystem(
    val tag: String
) {
    WINDOWS("windows"),
    LINUX("linux"),
    ANDROID("android");

    companion object {
        val current = OperatingSystem[BuildKonfig.OS]
        val isWindows = current == WINDOWS
        val isLinux = current == LINUX
        val isDesktop = current != ANDROID
        val isAndroid = current == ANDROID

        operator fun get(tag: String) = entries.find { it.tag == tag } ?: WINDOWS
    }
}