package ru.rznnike.demokmp.data.comobject

import com.sun.jna.platform.win32.COM.COMLateBindingObject
import com.sun.jna.platform.win32.Ole32
import com.sun.jna.platform.win32.OleAuto
import com.sun.jna.platform.win32.Variant
import ru.rznnike.demokmp.domain.utils.OperatingSystem
import java.util.*

open class COMObjectWrapper(name: String) : COMLateBindingObject(name, false) {
    interface Property<Type> {
        fun get(): Type

        fun set(value: Type)
    }

    protected fun boolProperty(key: String) = object : Property<Boolean> {
        override fun get(): Boolean = getIntProperty(key) != 0

        override fun set(value: Boolean) = setProperty(key, if (value) 1 else 0)
    }

    protected fun stringProperty(key: String) = object : Property<String> {
        override fun get(): String = getStringProperty(key)

        override fun set(value: String) = setProperty(key, value)
    }

    protected fun intProperty(key: String) = object : Property<Int> {
        override fun get(): Int = getIntProperty(key)

        override fun set(value: Int) = setProperty(key, value)
    }

    protected fun doubleProperty(key: String) = object : Property<Double> {
        override fun get(): Double = getDoubleProperty(key)

        override fun set(value: Double) = setProperty(key, Variant.VARIANT(value))
    }

    private fun getDoubleProperty(propertyName: String?): Double {
        val result = Variant.VARIANT.ByReference()
        this.oleMethod(OleAuto.DISPATCH_PROPERTYGET, result, propertyName)

        return result.doubleValue()
    }

    @Suppress("SameParameterValue")
    protected fun dateProperty(key: String) = object : Property<Date> {
        override fun get(): Date = getDateProperty(key)

        override fun set(value: Date) = setProperty(key, value)
    }

    companion object {
        @JvmStatic
        protected fun initialize() {
            if (OperatingSystem.isWindows) {
                Ole32.INSTANCE.CoInitializeEx(null, Ole32.COINIT_MULTITHREADED)
            } else {
                throw UnsupportedOperationException("This driver only supports Windows!")
            }
        }

        @JvmStatic
        protected fun uninitialize() {
            if (OperatingSystem.isWindows) {
                Ole32.INSTANCE.CoUninitialize()
            }
        }
    }
}