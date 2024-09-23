package ru.rznnike.demokmp.app.ui.screen.home

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.dialog.common.AlertDialogAction
import ru.rznnike.demokmp.app.ui.dialog.common.AlertDialogType
import ru.rznnike.demokmp.app.ui.dialog.common.CommonAlertDialog
import ru.rznnike.demokmp.app.ui.screen.dbexample.DBExampleFlow
import ru.rznnike.demokmp.app.ui.screen.httpexample.HTTPExampleFlow
import ru.rznnike.demokmp.app.ui.screen.settings.SettingsFlow
import ru.rznnike.demokmp.app.ui.screen.wsexample.WebSocketsExampleFlow
import ru.rznnike.demokmp.app.ui.theme.bodyLargeItalic
import ru.rznnike.demokmp.app.ui.view.TextR
import ru.rznnike.demokmp.app.ui.view.Toolbar
import ru.rznnike.demokmp.app.utils.getMacAddress
import ru.rznnike.demokmp.app.utils.platformName
import ru.rznnike.demokmp.app.viewmodel.configuration.AppConfigurationViewModel
import ru.rznnike.demokmp.generated.resources.*

class HomeScreen : NavigationScreen() {
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val appConfigurationViewModel: AppConfigurationViewModel = koinInject()
        val appConfiguration by appConfigurationViewModel.uiState.collectAsState()

        val notifier: Notifier = koinInject()

        screenKeyEventCallback = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                when {
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.F) -> notifier.sendMessage("Ctrl+F")
                    keyEvent.isCtrlPressed && keyEvent.isAltPressed && (keyEvent.key == Key.D) -> notifier.sendMessage("Ctrl+Alt+D")
                }
            }
        }

        var showAboutDialog by remember { mutableStateOf(false) }

        val macAddress = remember { getMacAddress() }

        Column {
            Spacer(Modifier.height(16.dp))
            Toolbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                title = stringResource(Res.string.main_screen)
            )
            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                val verticalScrollState = rememberScrollState()
                FlowRow(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(verticalScrollState)
                        .fillMaxSize()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    @Composable
                    fun MenuButton(text: StringResource, onClick: () -> Unit) {
                        Button(
                            modifier = Modifier
                                .weight(1f)
                                .widthIn(
                                    min = 150.dp
                                )
                                .height(70.dp),
                            onClick = onClick
                        ) {
                            TextR(
                                modifier = Modifier.fillMaxWidth(),
                                textRes = text,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    MenuButton(Res.string.open_settings) {
                        navigator.openFlow(SettingsFlow())
                    }
                    MenuButton(Res.string.open_http_example) {
                        navigator.openFlow(HTTPExampleFlow())
                    }
                    MenuButton(Res.string.open_ws_example) {
                        navigator.openFlow(WebSocketsExampleFlow())
                    }
                    MenuButton(Res.string.open_db_example) {
                        navigator.openFlow(DBExampleFlow())
                    }
                    MenuButton(Res.string.about_app) {
                        showAboutDialog = true
                    }
                    MenuButton(Res.string.test_dialog) {
                        notifier.sendAlert(Res.string.test_dialog)
                    }
                    MenuButton(Res.string.test_message) {
                        notifier.sendActionMessage(Res.string.test_message, Res.string.close) {}
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(verticalScrollState)
                )
            }
            TextR(
                textRes = Res.string.hotkeys_tip,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLargeItalic,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(Modifier.height(16.dp))
        }

        if (showAboutDialog) {
            val details = "%s: %s.%d%s\n%s: %s\n%s: %s\n%s: %s".format(
                stringResource(Res.string.version),
                BuildKonfig.VERSION_NAME,
                BuildKonfig.VERSION_CODE,
                if (BuildKonfig.DEBUG) " debug" else "",
                stringResource(Res.string.environment),
                platformName,
                stringResource(Res.string.mac),
                macAddress,
                stringResource(Res.string.launch_args),
                appConfiguration.args.joinToString()
            )
            CommonAlertDialog(
                type = AlertDialogType.HORIZONTAL,
                header = stringResource(Res.string.window_title),
                message = details,
                cancellable = true,
                onCancelListener = {
                    showAboutDialog = false
                },
                actions = listOf(
                    AlertDialogAction(stringResource(Res.string.close)) {
                        showAboutDialog = false
                    }
                )
            )
        }
    }
}