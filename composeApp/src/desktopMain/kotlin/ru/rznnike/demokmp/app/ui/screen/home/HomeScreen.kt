package ru.rznnike.demokmp.app.ui.screen.home

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.app.dispatcher.notifier.Notifier
import ru.rznnike.demokmp.app.model.common.HotkeyDescription
import ru.rznnike.demokmp.app.navigation.DesktopNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.dialog.common.AlertDialogAction
import ru.rznnike.demokmp.app.ui.dialog.common.AlertDialogType
import ru.rznnike.demokmp.app.ui.dialog.common.CommonAlertDialog
import ru.rznnike.demokmp.app.ui.screen.chartexample.ChartExampleFlow
import ru.rznnike.demokmp.app.ui.screen.comobjectexample.ComObjectExampleFlow
import ru.rznnike.demokmp.app.ui.screen.customui.CustomUIFlow
import ru.rznnike.demokmp.app.ui.screen.dbexample.DBExampleFlow
import ru.rznnike.demokmp.app.ui.screen.httpexample.HTTPExampleFlow
import ru.rznnike.demokmp.app.ui.screen.navigation.NavigationExampleFlow
import ru.rznnike.demokmp.app.ui.screen.pdfexample.PdfExampleFlow
import ru.rznnike.demokmp.app.ui.screen.settings.SettingsFlow
import ru.rznnike.demokmp.app.ui.screen.wsexample.WebSocketsExampleFlow
import ru.rznnike.demokmp.app.ui.view.SelectableButton
import ru.rznnike.demokmp.app.ui.view.TextR
import ru.rznnike.demokmp.app.ui.view.Toolbar
import ru.rznnike.demokmp.app.utils.getMacAddress
import ru.rznnike.demokmp.app.utils.platformName
import ru.rznnike.demokmp.app.viewmodel.global.configuration.AppConfigurationViewModel
import ru.rznnike.demokmp.app.viewmodel.home.HomeViewModel
import ru.rznnike.demokmp.domain.utils.OperatingSystem
import ru.rznnike.demokmp.generated.resources.*

@Serializable
class HomeScreen : DesktopNavigationScreen() {
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val appConfigurationViewModel: AppConfigurationViewModel = koinInject()
        val appConfigurationUiState by appConfigurationViewModel.uiState.collectAsState()

        val viewModel = viewModel { HomeViewModel() }

        val notifier: Notifier = koinInject()

        screenKeyEventCallback = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                when {
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.W) -> navigator.closeScreen()
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.F) -> notifier.sendMessage("Ctrl+F")
                }
            }
        }

        var showAboutDialog by remember { mutableStateOf(false) }

        val macAddress = remember { getMacAddress() }

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
                    FlowRow(
                        modifier = Modifier
                            .verticalScroll(verticalScrollState)
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        @Composable
                        fun MenuButton(textRes: StringResource, onClick: () -> Unit) = SelectableButton(
                            modifier = Modifier
                                .weight(1f)
                                .widthIn(
                                    min = 190.dp
                                )
                                .height(70.dp),
                            onClick = onClick
                        ) {
                            TextR(
                                modifier = Modifier.fillMaxWidth(),
                                textRes = textRes,
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                        }

                        MenuButton(Res.string.settings) {
                            navigator.openFlow(SettingsFlow())
                        }
                        MenuButton(Res.string.http_example) {
                            navigator.openFlow(HTTPExampleFlow())
                        }
                        MenuButton(Res.string.ws_example) {
                            navigator.openFlow(WebSocketsExampleFlow())
                        }
                        MenuButton(Res.string.db_example) {
                            navigator.openFlow(DBExampleFlow())
                        }
                        MenuButton(Res.string.pdf_example) {
                            navigator.openFlow(PdfExampleFlow())
                        }
                        MenuButton(Res.string.chart_example) {
                            navigator.openFlow(ChartExampleFlow())
                        }
                        MenuButton(Res.string.custom_ui_elements) {
                            navigator.openFlow(CustomUIFlow())
                        }
                        MenuButton(Res.string.navigation_example) {
                            navigator.openFlow(NavigationExampleFlow())
                        }
                        if (OperatingSystem.isWindows) {
                            MenuButton(Res.string.com_object_example) {
                                navigator.openFlow(ComObjectExampleFlow())
                            }
                        }
                        MenuButton(Res.string.test_dialog) {
                            notifier.sendAlert(Res.string.test_dialog)
                        }
                        MenuButton(Res.string.test_message) {
                            notifier.sendActionMessage(Res.string.test_message, Res.string.close) {}
                        }
                        MenuButton(Res.string.restart) {
                            viewModel.restartApp()
                        }
                        MenuButton(Res.string.about_app) {
                            showAboutDialog = true
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(verticalScrollState)
                    )
                }
            }
        }

        if (showAboutDialog) {
            val details = "%s: %s.%d%s\n%s: %s\n%s: %s\n%s: %s".format(
                stringResource(Res.string.version),
                BuildKonfig.VERSION_NAME,
                BuildKonfig.VERSION_CODE,
                if (BuildKonfig.DEBUG) " ${BuildKonfig.BUILD_TYPE}" else "",
                stringResource(Res.string.environment),
                platformName,
                stringResource(Res.string.mac),
                macAddress ?: "",
                stringResource(Res.string.launch_args),
                appConfigurationUiState.args.joinToString()
            )
            CommonAlertDialog(
                type = AlertDialogType.HORIZONTAL,
                header = stringResource(Res.string.app_name),
                message = details,
                cancellable = true,
                onCancelListener = {
                    showAboutDialog = false
                },
                actions = listOf(
                    AlertDialogAction(stringResource(Res.string.close)) {
                        showAboutDialog = false
                    },
                    AlertDialogAction(stringResource(Res.string.source_code)) {
                        showAboutDialog = false
                        viewModel.openSourceCodeLink()
                    }
                )
            )
        }
    }

    @Composable
    override fun getHotkeysDescription(): List<HotkeyDescription> = listOf(
        HotkeyDescription(
            hotkey = "Ctrl+F",
            description = stringResource(Res.string.hotkey_test_combination)
        )
    )
}