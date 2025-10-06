package ru.rznnike.demokmp.data.shell

import com.sun.jna.platform.win32.Variant
import ru.rznnike.demokmp.data.comobject.COMObjectWrapper

class PowerShellWrapper : COMObjectWrapper("Shell.Application") {
    fun minimizeAll() {
        invoke("MinimizeAll")
    }

    private fun getSystemInformation(infoName: String): Variant.VARIANT {
        return invoke("GetSystemInformation", Variant.VARIANT(infoName))
    }

    fun getCPUSpeedMhz(): Int {
        return getSystemInformation("ProcessorSpeed").intValue()
    }

    fun getCPUArchitecture(): String {
        return when (getSystemInformation("ProcessorArchitecture").intValue()) {
            0 -> "x86"
            5 -> "ARM"
            6 -> "Intel Itanium"
            9 -> "x64"
            12 -> "ARM64"
            else -> "unknown"
        }
    }

    fun getRAMAmountGb(): Double {
        return getSystemInformation("PhysicalMemoryInstalled").longValue() / 1073741824.0
    }

    fun openFolderOrFile(path: String) {
        invoke("Open", Variant.VARIANT(path))
    }

    companion object {
        fun initialize() = COMObjectWrapper.initialize()

        fun uninitialize() = COMObjectWrapper.uninitialize()
    }
}