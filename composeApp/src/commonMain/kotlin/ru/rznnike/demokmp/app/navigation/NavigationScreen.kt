package ru.rznnike.demokmp.app.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import ru.rznnike.demokmp.domain.log.Logger

@Serializable
abstract class NavigationScreen : NavKey {
    @Transient
    private val logger = Logger.withTag("Navigation")

    @Composable
    open fun Content() {
        Layout()

        LifecycleResumeEffect(Unit) {
            logger.i(this@NavigationScreen::class.java.simpleName)
            onPauseOrDispose { }
        }
    }

    @Composable
    abstract fun Layout()
}