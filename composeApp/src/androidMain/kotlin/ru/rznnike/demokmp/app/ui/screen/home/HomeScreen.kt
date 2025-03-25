package ru.rznnike.demokmp.app.ui.screen.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
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
import ru.rznnike.demokmp.app.ui.theme.bodyLargeItalic
import ru.rznnike.demokmp.app.ui.view.SelectableButton
import ru.rznnike.demokmp.app.ui.view.TextR
import ru.rznnike.demokmp.app.ui.view.Toolbar
import ru.rznnike.demokmp.app.utils.platformName
import ru.rznnike.demokmp.app.viewmodel.global.configuration.AppConfigurationViewModel
import ru.rznnike.demokmp.app.viewmodel.home.HomeViewModel
import ru.rznnike.demokmp.generated.resources.*

class HomeScreen : NavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val appConfigurationViewModel: AppConfigurationViewModel = koinInject()
        val appConfigurationUiState by appConfigurationViewModel.uiState.collectAsState()

        val viewModel = viewModel { HomeViewModel() }

        val notifier: Notifier = koinInject()

        var showAboutDialog by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(16.dp))
            Toolbar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.main_screen)
            )
            Spacer(Modifier.height(16.dp))
            Surface(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    val verticalScrollState = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .verticalScroll(verticalScrollState)
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        @Composable
                        fun MenuButton(text: StringResource, onClick: () -> Unit) {
                            SelectableButton(
                                modifier = Modifier
                                    .fillMaxWidth()
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

                        MenuButton(Res.string.settings) {
//                            navigator.openFlow(SettingsFlow())
                        }
                        MenuButton(Res.string.http_example) {
//                            navigator.openFlow(HTTPExampleFlow())
                        }
                        MenuButton(Res.string.ws_example) {
//                            navigator.openFlow(WebSocketsExampleFlow())
                        }
                        MenuButton(Res.string.db_example) {
//                            navigator.openFlow(DBExampleFlow())
                        }
                        MenuButton(Res.string.pdf_example) {
//                            navigator.openFlow(PdfExampleFlow())
                        }
                        MenuButton(Res.string.custom_ui_elements) {
//                            navigator.openFlow(CustomUIFlow())
                        }
                        MenuButton(Res.string.about_app) {
                            showAboutDialog = true
                        }
                        MenuButton(Res.string.restart) {
                            viewModel.restartApp()
                        }
                        MenuButton(Res.string.test_dialog) {
                            notifier.sendAlert(Res.string.test_dialog)
                        }
                        MenuButton(Res.string.test_message) {
                            notifier.sendActionMessage(Res.string.test_message, Res.string.close) {}
                        }
                    }
                }
            }
            Spacer(Modifier.height(8.dp))
            TextR(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textRes = Res.string.hotkeys_tip,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLargeItalic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
        }

        if (showAboutDialog) {
            val details = "%s: %s.%d%s\n%s: %s\n%s: %s".format(
                stringResource(Res.string.version),
                BuildKonfig.VERSION_NAME,
                BuildKonfig.VERSION_CODE,
                if (BuildKonfig.DEBUG) " debug" else "",
                stringResource(Res.string.environment),
                platformName,
                stringResource(Res.string.launch_args),
                appConfigurationUiState.args.joinToString()
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