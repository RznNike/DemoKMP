package ru.rznnike.demokmp.app.navigation

import androidx.navigation.NavGraphBuilder
import ru.rznnike.demokmp.app.ui.screen.chartexample.ChartExampleScreen
import ru.rznnike.demokmp.app.ui.screen.customui.CustomUIScreen
import ru.rznnike.demokmp.app.ui.screen.dbexample.DBExampleScreen
import ru.rznnike.demokmp.app.ui.screen.home.HomeScreen
import ru.rznnike.demokmp.app.ui.screen.httpexample.HTTPExampleScreen
import ru.rznnike.demokmp.app.ui.screen.logger.LoggerScreen
import ru.rznnike.demokmp.app.ui.screen.logger.network.NetworkLogDetailsScreen
import ru.rznnike.demokmp.app.ui.screen.navigation.NavigationExampleScreen
import ru.rznnike.demokmp.app.ui.screen.pdfexample.PdfExampleScreen
import ru.rznnike.demokmp.app.ui.screen.settings.NestedSettingsScreen
import ru.rznnike.demokmp.app.ui.screen.settings.SettingsScreen
import ru.rznnike.demokmp.app.ui.screen.splash.SplashScreen
import ru.rznnike.demokmp.app.ui.screen.wsexample.WebSocketsExampleScreen

actual fun NavGraphBuilder.buildNavGraph() {
    addToNavGraph<LoggerScreen>()
    addToNavGraph<NetworkLogDetailsScreen>(NetworkLogDetailsScreen.typeMap)
    addToNavGraph<SplashScreen>()
    addToNavGraph<HomeScreen>()
    addToNavGraph<SettingsScreen>()
    addToNavGraph<NestedSettingsScreen>()
    addToNavGraph<HTTPExampleScreen>()
    addToNavGraph<DBExampleScreen>()
    addToNavGraph<WebSocketsExampleScreen>()
    addToNavGraph<ChartExampleScreen>()
    addToNavGraph<CustomUIScreen>()
    addToNavGraph<PdfExampleScreen>()
    addToNavGraph<NavigationExampleScreen>()
}