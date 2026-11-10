package ru.rznnike.demokmp.data.shell

import com.sun.jna.platform.win32.Variant
import ru.rznnike.demokmp.data.comobject.COMObjectWrapper

internal class PowerShellWrapper : COMObjectWrapper("Shell.Application"), PCShell {
    override fun minimizeAll() {
        invoke("MinimizeAll")
    }

    private fun getSystemInformation(infoName: String) =
        invoke("GetSystemInformation", Variant.VARIANT(infoName))

    override fun getCPUSpeedMhz() = getSystemInformation("ProcessorSpeed").intValue()

    override fun getCPUArchitecture() = when (getSystemInformation("ProcessorArchitecture").intValue()) {
        0 -> "x86"
        5 -> "ARM"
        6 -> "Intel Itanium"
        9 -> "x64"
        12 -> "ARM64"
        else -> "unknown"
    }

    override fun getRAMAmountGb() =
        getSystemInformation("PhysicalMemoryInstalled").longValue() / 1073741824.0

    override fun openFolderOrFile(path: String) {
        invoke("Open", Variant.VARIANT(path))
    }
}