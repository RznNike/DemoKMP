package ru.rznnike.demokmp.app.utils

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import ru.rznnike.demokmp.domain.model.common.Language
import java.util.*
import kotlin.system.exitProcess

fun Activity.restartApp() {
    val intent = Intent(this, this::class.java)
    val restartIntent = Intent.makeRestartActivityTask(intent.component)
    startActivity(restartIntent)
    exitProcess(0)
}

fun getSelectedLanguage() = Language.getByShortTag(
    (AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault()).language
)

fun setSelectedLanguage(language: Language) = AppCompatDelegate.setApplicationLocales(
    LocaleListCompat.forLanguageTags(language.shortTag)
)
