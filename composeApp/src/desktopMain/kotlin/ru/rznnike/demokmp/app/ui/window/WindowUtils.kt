package ru.rznnike.demokmp.app.ui.window

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.FrameWindowScope
import java.awt.Dimension
import kotlin.math.roundToInt

val LocalWindow = staticCompositionLocalOf { ComposeWindow() }

@Composable
fun BackgroundBox(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) = Box(
    modifier = modifier
) {
    Column( // Background artefacts fix for Intel Arc GPU
        modifier = Modifier.fillMaxSize()
    ) {
        @Composable
        fun BackgroundPart() = Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
        )

        // Background views must not be fillMaxSize(), so we need at least 2 views to fix this bug
        BackgroundPart()
        BackgroundPart()
    }
    content()
}

class WindowFocusRequester(
    var onFocusRequested: (() -> Unit) = { }
)

@Composable
fun FrameWindowScope.SetMinimumSize(
    width: Dp = Dp.Unspecified,
    height: Dp = Dp.Unspecified,
    scale: Int = 100
) {
    val density = LocalDensity.current
    key(density) {
        window.minimumSize = with(density) {
            val scaledWidth = (width.toPx() * scale / 100).roundToInt()
            val scaledHeight = (height.toPx() * scale / 100).roundToInt()
            Dimension(scaledWidth, scaledHeight)
        }
    }
}