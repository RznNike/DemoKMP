package ru.rznnike.demokmp.app.utils

import android.os.Build

actual val platformName: String = "Android ${Build.VERSION.SDK_INT}"

actual fun getMacAddress(): String = TODO("Not yet implemented")