package ru.rznnike.demokmp.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import org.koin.compose.koinInject
import ru.rznnike.demokmp.domain.utils.GlobalConstants
import java.time.Clock

class ClicksFilter(
    private val clock: Clock,
    private val thresholdMs: Long
) {
    private var lastClickTime = -1L

    fun filter(onClick: () -> Unit) {
        val currentTime = clock.millis()
        if (currentTime > (lastClickTime + thresholdMs)) {
            onClick()
            lastClickTime = currentTime
        }
    }
}

@Composable
fun rememberClicksFilter(
    clock: Clock = koinInject(),
    thresholdMs: Long = GlobalConstants.IMPORTANT_BUTTON_CLICKS_FILTER_MS
) = remember { ClicksFilter(clock, thresholdMs) }