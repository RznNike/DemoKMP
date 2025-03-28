package ru.rznnike.demokmp.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.jetbrains.compose.resources.StringResource
import ru.rznnike.demokmp.app.ui.theme.LocalCustomColorScheme
import ru.rznnike.demokmp.domain.log.LogLevel
import ru.rznnike.demokmp.domain.log.NetworkRequestState
import ru.rznnike.demokmp.domain.model.common.Theme
import ru.rznnike.demokmp.domain.model.print.TwoSidedPrint
import ru.rznnike.demokmp.generated.resources.*

val Theme.nameRes: StringResource
    @Composable
    get() = when (this) {
        Theme.AUTO -> Res.string.theme_auto
        Theme.LIGHT -> Res.string.theme_light
        Theme.DARK -> Res.string.theme_dark
    }

val TwoSidedPrint.nameRes: StringResource
    @Composable
    get() = when (this) {
        TwoSidedPrint.DISABLED -> Res.string.two_sided_print_disabled
        TwoSidedPrint.TWO_SIDED_LONG_EDGE -> Res.string.two_sided_print_long_edge
        TwoSidedPrint.TWO_SIDED_SHORT_EDGE -> Res.string.two_sided_print_short_edge
    }

val LogLevel.backgroundColor: Color
    @Composable
    get() = when (this) {
        LogLevel.DEBUG -> LocalCustomColorScheme.current.logDebug
        LogLevel.INFO -> LocalCustomColorScheme.current.logInfo
        LogLevel.ERROR -> LocalCustomColorScheme.current.logError
    }

val NetworkRequestState.backgroundColor: Color
    @Composable
    get() = when (this) {
        NetworkRequestState.SENT -> LocalCustomColorScheme.current.logNetworkSent
        NetworkRequestState.SUCCESS -> LocalCustomColorScheme.current.logNetworkSuccess
        NetworkRequestState.ERROR -> LocalCustomColorScheme.current.logNetworkError
        NetworkRequestState.TIMEOUT,
        NetworkRequestState.CANCELLED -> LocalCustomColorScheme.current.logNetworkCancelled
    }
