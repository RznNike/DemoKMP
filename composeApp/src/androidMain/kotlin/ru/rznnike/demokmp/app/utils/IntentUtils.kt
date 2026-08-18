package ru.rznnike.demokmp.app.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.core.net.toUri
import ru.rznnike.demokmp.domain.model.common.Position
import java.util.*

fun Context.openLink(
    link: String,
    specificPackage: String? = null,
    onErrorCallback: (Exception) -> Unit = {}
) = openLink(
    link = link.toUri(),
    specificPackage = specificPackage,
    onErrorCallback = onErrorCallback
)

fun Context.openLink(
    link: Uri,
    specificPackage: String? = null,
    onErrorCallback: (Exception) -> Unit = {}
) {
    val intent = Intent(Intent.ACTION_VIEW)
        .setData(link)
        .setPackage(specificPackage)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        startActivity(intent)
    } catch (e: Exception) { onErrorCallback(e) }
}

fun Context.openDial(phone: String) {
    val intent = Intent(Intent.ACTION_DIAL)
        .setData(Uri.fromParts("tel", phone, null))
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    startActivity(intent)
}

fun Context.openAppSettings() {
    val settingsIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .setData(("package:$packageName").toUri())
        .addCategory(Intent.CATEGORY_DEFAULT)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        startActivity(settingsIntent)
    } catch (_: ActivityNotFoundException) {
        startActivity(Intent(Settings.ACTION_SETTINGS))
    }
}

fun Context.openNotificationSettings() {
    val settingsIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
    try {
        startActivity(settingsIntent)
    } catch (_: ActivityNotFoundException) {
        startActivity(Intent(Settings.ACTION_SETTINGS))
    }
}

fun Context.openGooglePlayPage() {
    try {
        openLink(
            link = "market://details?id=$packageName",
            specificPackage = "com.android.vending"
        )
    } catch (_: ActivityNotFoundException) {
        openLink("https://play.google.com/store/apps/details?id=$packageName")
    }
}

fun Context.openNavigation(position: Position? = null, address: String? = null) {
    val addressString = address ?: ""
    try {
        val uri = position?.let {
            "geo:%f,%f?q=%f,%f".format(
                Locale.ENGLISH,
                position.lat,
                position.lng,
                position.lat,
                position.lng
            )
        } ?: run {
            "geo:0,0?q=$addressString"
        }
        openLink(uri)
    } catch (_: ActivityNotFoundException) {
        val searchUrl = position?.let {
            "https://yandex.ru/maps/?whatshere[point]=%f,%f&whatshere[zoom]=17".format(
                Locale.ENGLISH,
                position.lng,
                position.lat
            )
        } ?: run {
            "https://yandex.ru/maps/?mode=search&text=$addressString"
        }
        openLink(searchUrl)
    }
}