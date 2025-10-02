package ru.rznnike.demokmp.app.utils

import org.koin.core.component.KoinComponent
import java.awt.Desktop
import java.net.NetworkInterface
import java.net.URI

actual val platformName: String = "Java ${System.getProperty("java.version")} | ${System.getProperty("java.vm.name")} | ${System.getProperty("java.vm.vendor")}"

actual fun getMacAddress(): String? =
    NetworkInterface
        .getNetworkInterfaces()
        .toList()
        .filter { it.hardwareAddress != null }
        .sortedWith(
            compareBy(
                { !it.displayName.contains(Regex("Ethernet|Wi-Fi", RegexOption.IGNORE_CASE)) },
                { it.displayName.contains(Regex("Virtual", RegexOption.IGNORE_CASE)) }
            )
        )
        .firstNotNullOfOrNull { element ->
            element.hardwareAddress?.joinToString(separator = "-") { "%02X".format(it) }
        }

actual fun KoinComponent.openLink(link: String) {
    Desktop.getDesktop().browse(URI(link))
}