package ru.rznnike.demokmp.app.ui.theme

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.dp

@Composable
fun DesktopAppTheme(
    content: @Composable () -> Unit
) {
    val scrollbarStyle = ScrollbarStyle(
        minimalHeight = 16.dp,
        thickness = 8.dp,
        shape = RoundedCornerShape(4.dp),
        hoverDurationMillis = 300,
        unhoverColor = MaterialTheme.colorScheme.onBackground.copy(alpha = LocalCustomColorScheme.current.scrollbarAlpha),
        hoverColor = MaterialTheme.colorScheme.onBackground.copy(alpha = LocalCustomColorScheme.current.scrollbarHoverAlpha)
    )

    CompositionLocalProvider(
        LocalScrollbarStyle provides scrollbarStyle
    ) {
        AppTheme(content)
    }
}

@Composable
fun PreviewDesktopAppTheme(
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalScrollbarStyle provides defaultScrollbarStyle()
    ) {
        AppTheme(content)
    }
}
