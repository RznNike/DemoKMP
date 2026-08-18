package ru.rznnike.demokmp.app.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.modules.PolymorphicModuleBuilder
import kotlinx.serialization.modules.subclass
import ru.rznnike.demokmp.app.ui.screen.chartexample.ChartExampleScreen
import ru.rznnike.demokmp.app.ui.screen.comobjectexample.ComObjectExampleScreen
import ru.rznnike.demokmp.app.ui.screen.customui.CustomUIScreen
import ru.rznnike.demokmp.app.ui.screen.dbexample.DBExampleScreen
import ru.rznnike.demokmp.app.ui.screen.home.HomeScreen
import ru.rznnike.demokmp.app.ui.screen.httpexample.HTTPExampleScreen
import ru.rznnike.demokmp.app.ui.screen.logger.LoggerScreen
import ru.rznnike.demokmp.app.ui.screen.logger.network.NetworkLogDetailsScreen
import ru.rznnike.demokmp.app.ui.screen.markdown.MarkdownExampleScreen
import ru.rznnike.demokmp.app.ui.screen.navigation.NavigationExampleScreen
import ru.rznnike.demokmp.app.ui.screen.pdfexample.PdfExampleScreen
import ru.rznnike.demokmp.app.ui.screen.settings.NestedSettingsScreen
import ru.rznnike.demokmp.app.ui.screen.settings.SettingsScreen
import ru.rznnike.demokmp.app.ui.screen.splash.SplashScreen
import ru.rznnike.demokmp.app.ui.screen.wsexample.WebSocketsExampleScreen

actual val screenKeyList: PolymorphicModuleBuilder<NavKey>.() -> Unit = {
    subclass(LoggerScreen::class)
    subclass(NetworkLogDetailsScreen::class)
    subclass(SplashScreen::class)
    subclass(HomeScreen::class)
    subclass(SettingsScreen::class)
    subclass(NestedSettingsScreen::class)
    subclass(HTTPExampleScreen::class)
    subclass(DBExampleScreen::class)
    subclass(WebSocketsExampleScreen::class)
    subclass(ChartExampleScreen::class)
    subclass(CustomUIScreen::class)
    subclass(PdfExampleScreen::class)
    subclass(NavigationExampleScreen::class)
    subclass(ComObjectExampleScreen::class)
    subclass(MarkdownExampleScreen::class)
}