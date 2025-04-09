package ru.rznnike.demokmp.app

import androidx.compose.ui.window.application
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import ru.rznnike.demokmp.app.di.appComponent
import ru.rznnike.demokmp.app.ui.window.main.MainWindow
import ru.rznnike.demokmp.domain.log.Logger

fun main(args: Array<String>) = application {
    Logger.i("Application start")
    initKoin()
    MainWindow(args)
}

fun initKoin() {
    startKoin {
        modules(appComponent)
        val koinLogger = Logger.withTag("Koin")
        logger(
            object : org.koin.core.logger.Logger() {
                override fun display(level: Level, msg: String) {
                    when (level) {
                        Level.DEBUG -> koinLogger.d(msg)
                        Level.NONE,
                        Level.INFO -> koinLogger.i(msg)
                        Level.WARNING -> koinLogger.w(msg)
                        Level.ERROR -> koinLogger.e(msg)
                    }
                }
            }
        )
    }
}