package ru.rznnike.demokmp.app.utils

import java.net.InetAddress
import java.net.NetworkInterface

actual val platformName: String = "Java ${System.getProperty("java.version")}"

actual fun getMacAddress(): String {
    val address = InetAddress.getLocalHost()
    val networkInterface = NetworkInterface.getByInetAddress(address)
    val macAddress = networkInterface.hardwareAddress
    return macAddress.joinToString(separator = "-") { "%02X".format(it) }
}