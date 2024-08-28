package ru.rznnike.demokmp.app.model.common

import demokmp.composeapp.generated.resources.*
import demokmp.composeapp.generated.resources.Res
import demokmp.composeapp.generated.resources.theme_dark
import demokmp.composeapp.generated.resources.theme_light
import org.jetbrains.compose.resources.StringResource
import ru.rznnike.demokmp.app.model.common.ThemeAppModel.Companion.default
import ru.rznnike.demokmp.app.model.common.ThemeAppModel.entries
import ru.rznnike.demokmp.domain.model.common.Theme

enum class ThemeAppModel(
    val data: Theme,
    val nameRes: StringResource
) {
    AUTO(
        data = Theme.AUTO,
        nameRes = Res.string.theme_auto
    ),
    LIGHT(
        data = Theme.LIGHT,
        nameRes = Res.string.theme_light
    ),
    DARK(
        data = Theme.DARK,
        nameRes = Res.string.theme_dark
    );

    companion object {
        val default = AUTO
    }
}

fun Theme.toAppModel() = entries.find { it.data == this } ?: default