package ru.rznnike.demokmp.data.shell

internal interface PCShell {
    fun minimizeAll()

    fun getCPUSpeedMhz(): Int

    fun getCPUArchitecture(): String

    fun getRAMAmountGb(): Double

    fun openFolderOrFile(path: String)
}