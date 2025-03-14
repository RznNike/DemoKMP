package ru.rznnike.demokmp.app.utils

import android.os.Build
import java.net.NetworkInterface

actual val platformName: String = "Android ${Build.VERSION.SDK_INT}"

actual fun getMacAddress(): String? = // TODO check if it works
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