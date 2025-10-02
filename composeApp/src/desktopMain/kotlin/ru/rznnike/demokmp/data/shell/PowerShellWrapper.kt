package ru.rznnike.demokmp.data.shell

import ru.rznnike.demokmp.data.comobject.COMObjectWrapper

class PowerShellWrapper : COMObjectWrapper("Shell.Application") {
    fun minimizeAll() {
        invoke("MinimizeAll")
    }

    companion object {
        fun initialize() = COMObjectWrapper.initialize()

        fun uninitialize() = COMObjectWrapper.uninitialize()
    }
}