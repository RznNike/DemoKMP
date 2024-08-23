package ru.rznnike.demokmp.app.ui.screen.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import demokmp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.model.common.toAppModel
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getScreenNavigator
import ru.rznnike.demokmp.app.ui.view.Toolbar
import ru.rznnike.demokmp.app.ui.view.ToolbarButton
import ru.rznnike.demokmp.app.utils.TextR
import ru.rznnike.demokmp.app.viewmodel.configuration.AppConfigurationViewModel
import ru.rznnike.demokmp.app.viewmodel.profile.ProfileViewModel
import ru.rznnike.demokmp.app.viewmodel.settings.SettingsViewModel
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme

class SettingsScreen : NavigationScreen() {
    @Preview
    @Composable
    override fun Content() {
        val profileViewModel: ProfileViewModel = koinInject()
        val settingsViewModel = viewModel { SettingsViewModel() }
        val settingsUiState by settingsViewModel.uiState.collectAsState()
        val appConfigurationViewModel: AppConfigurationViewModel = koinInject()
        val appConfigurationUiState by appConfigurationViewModel.uiState.collectAsState()

        val screenNavigator = getScreenNavigator()

        key(appConfigurationUiState.language.tag) {
            Column {
                Spacer(Modifier.height(16.dp))
                Toolbar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    title = stringResource(Res.string.settings),
                    leftButton = ToolbarButton(Res.drawable.ic_back) {
                        screenNavigator.close()
                    }
                )
                Spacer(Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    val state = rememberScrollState()
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .fillMaxSize()
                            .verticalScroll(state)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val nameString = "%s: %s".format(
                                    stringResource(Res.string.user_name),
                                    profileViewModel.nameInput
                                )
                                Text(
                                    text = nameString,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                                Spacer(Modifier.width(16.dp))
                                Button(
                                    onClick = {
                                        screenNavigator.open(NestedSettingsScreen())
                                    }
                                ) {
                                    TextR(Res.string.open_nested_settings)
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surfaceContainer
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                @Composable
                                fun CounterButton(text: String, onClick: () -> Unit) {
                                    Button(
                                        modifier = Modifier.size(40.dp),
                                        contentPadding = PaddingValues(0.dp),
                                        onClick = onClick
                                    ) {
                                        Text(
                                            text = text,
                                            modifier = Modifier.wrapContentSize(),
                                            style = MaterialTheme.typography.titleLarge,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                TextR(
                                    textRes = Res.string.test_counter,
                                    modifier = Modifier
                                        .weight(1f)
                                )
                                Spacer(Modifier.width(16.dp))
                                CounterButton("-") {
                                    settingsViewModel.onCounterInput(settingsUiState.counter - 1)
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = settingsUiState.counter.toString(),
                                    modifier = Modifier
                                        .width(50.dp),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.width(8.dp))
                                CounterButton("+") {
                                    settingsViewModel.onCounterInput(settingsUiState.counter + 1)
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))
                        Row {
                            @Composable
                            fun OptionsSelector(
                                headerRes: StringResource,
                                buttonText: String,
                                content: @Composable (ColumnScope.(closeMenu: () -> Unit) -> Unit)
                            ) {
                                Surface(
                                    modifier = Modifier.weight(1f),
                                    shape = MaterialTheme.shapes.medium,
                                    color = MaterialTheme.colorScheme.surfaceContainer
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        TextR(
                                            textRes = headerRes,
                                            modifier = Modifier.weight(1f),
                                        )
                                        Box {
                                            var showMenu by remember { mutableStateOf(false) }
                                            Button(
                                                onClick = {
                                                    showMenu = !showMenu
                                                }
                                            ) {
                                                Text(buttonText)
                                            }
                                            Box(
                                                modifier = Modifier.padding(end = 16.dp)
                                            ) {
                                                DropdownMenu(
                                                    expanded = showMenu,
                                                    onDismissRequest = { showMenu = false }
                                                ) {
                                                    content {
                                                        showMenu = false
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            OptionsSelector(
                                headerRes = Res.string.language,
                                buttonText = appConfigurationUiState.language.localizedName
                            ) { closeMenu ->
                                Language.entries.forEach { language ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(language.localizedName)
                                        },
                                        onClick = {
                                            appConfigurationViewModel.setLanguage(language)
                                            closeMenu()
                                        }
                                    )
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            OptionsSelector(
                                headerRes = Res.string.theme,
                                buttonText = stringResource(appConfigurationUiState.theme.toAppModel().nameRes)
                            ) { closeMenu ->
                                Theme.entries.forEach { theme ->
                                    DropdownMenuItem(
                                        text = {
                                            TextR(theme.toAppModel().nameRes)
                                        },
                                        onClick = {
                                            appConfigurationViewModel.setTheme(theme)
                                            closeMenu()
                                        }
                                    )
                                }
                            }
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(state)
                    )
                }
            }
        }
    }
}