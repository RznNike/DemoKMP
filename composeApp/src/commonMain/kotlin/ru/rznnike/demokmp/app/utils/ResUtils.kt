package ru.rznnike.demokmp.app.utils

import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import ru.rznnike.demokmp.domain.model.common.Theme
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.theme_auto
import ru.rznnike.demokmp.generated.resources.theme_dark
import ru.rznnike.demokmp.generated.resources.theme_light

val Theme.nameRes: StringResource
    @Composable
    get() = when (this) {
        Theme.AUTO -> Res.string.theme_auto
        Theme.LIGHT -> Res.string.theme_light
        Theme.DARK -> Res.string.theme_dark
    }