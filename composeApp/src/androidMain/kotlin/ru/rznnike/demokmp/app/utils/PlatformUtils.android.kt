package ru.rznnike.demokmp.app.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.net.toUri
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual val platformName: String = "Android ${Build.VERSION.RELEASE}"

actual fun getMacAddress(): String? = null

actual fun KoinComponent.openLink(link: String) {
    val context: Context by inject()
    val intent = Intent(Intent.ACTION_VIEW)
        .setData(link.toUri())
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    context.startActivity(intent)
}

actual fun initCOMLibrary() = Unit

actual fun destroyCOMLibrary() = Unit