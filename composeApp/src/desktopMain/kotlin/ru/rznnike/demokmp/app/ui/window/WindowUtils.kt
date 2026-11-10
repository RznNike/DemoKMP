package ru.rznnike.demokmp.app.ui.window

import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.isUnspecified
import androidx.compose.ui.window.FrameWindowScope
import java.awt.Dimension
import kotlin.math.roundToInt

val LocalWindow = staticCompositionLocalOf { ComposeWindow() }

class WindowFocusRequester(
    var onFocusRequested: (() -> Unit) = { }
)

internal fun calculateMinimumSize(
    width: Dp,
    height: Dp,
    scale: Int,
    density: Density
): Dimension? {
    if (width.isUnspecified || height.isUnspecified) return null

    return with(density) {
        Dimension(
            (width.toPx() * scale / 100).roundToInt(),
            (height.toPx() * scale / 100).roundToInt()
        )
    }
}

@Composable
fun FrameWindowScope.SetMinimumSize(
    width: Dp = Dp.Unspecified,
    height: Dp = Dp.Unspecified,
    scale: Int = 100
) {
    val density = LocalDensity.current
    key(density) {
        calculateMinimumSize(width, height, scale, density)?.let {
            window.minimumSize = it
        }
    }
}