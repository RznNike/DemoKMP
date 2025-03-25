package ru.rznnike.demokmp.app.utils

import android.os.Build

actual val platformName: String = "Android ${Build.VERSION.RELEASE}"

actual fun getMacAddress(): String? = null