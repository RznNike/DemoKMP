package ru.rznnike.demokmp.app.utils

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme
import java.util.*

fun Activity.restartApp() {
    val intent = Intent(this, this::class.java)
    val restartIntent = Intent.makeRestartActivityTask(intent.component)
    startActivity(restartIntent)
}

fun applyTheme(theme: Theme) {
    val flag = when (theme) {
        Theme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
        Theme.DARK -> AppCompatDelegate.MODE_NIGHT_YES
        Theme.AUTO -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
    }
    AppCompatDelegate.setDefaultNightMode(flag)
}

fun getSelectedLanguage() = Language.getByShortTag(
    (AppCompatDelegate.getApplicationLocales()[0] ?: Locale.getDefault()).language
)

fun setSelectedLanguage(language: Language) = AppCompatDelegate.setApplicationLocales(
    LocaleListCompat.forLanguageTags(language.shortTag)
)
