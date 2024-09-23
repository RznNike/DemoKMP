package ru.rznnike.demokmp.app.utils

import java.net.NetworkInterface

actual val platformName: String = "Java ${System.getProperty("java.version")}"

actual fun getMacAddress(): String =
    NetworkInterface
        .getNetworkInterfaces()
        .toList()
        .sortedByDescending { it.name.startsWith("ethernet") }
        .firstNotNullOf { element ->
            element.hardwareAddress?.joinToString(separator = "-") { "%02X".format(it) }
        }