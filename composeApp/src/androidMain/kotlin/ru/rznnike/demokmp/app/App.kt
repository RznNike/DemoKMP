package ru.rznnike.demokmp.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import ru.rznnike.demokmp.app.di.appComponent
import ru.rznnike.demokmp.domain.log.Logger
import ru.rznnike.demokmp.domain.log.extension.ConsoleLoggerExtension

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initLogger()
        Logger.i("Application start")
        initKoin()
    }

    private fun initLogger() {
        Logger.init(
            extensions = listOf(
                ConsoleLoggerExtension()
            )
        )
        Thread.setDefaultUncaughtExceptionHandler(
            object : Thread.UncaughtExceptionHandler {
                var defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

                override fun uncaughtException(thread: Thread, throwable: Throwable) {
                    Logger.e(throwable)
                    defaultHandler?.uncaughtException(thread, throwable)
                }
            }
        )
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
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
}