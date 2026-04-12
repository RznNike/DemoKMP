package ru.rznnike.demokmp.app.utils

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