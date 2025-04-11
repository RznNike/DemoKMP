package ru.rznnike.demokmp.app.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.AndroidNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.view.*
import ru.rznnike.demokmp.app.utils.getSelectedLanguage
import ru.rznnike.demokmp.app.utils.nameRes
import ru.rznnike.demokmp.app.utils.setSelectedLanguage
import ru.rznnike.demokmp.app.utils.windowViewModel
import ru.rznnike.demokmp.app.viewmodel.global.configuration.AppConfigurationViewModel
import ru.rznnike.demokmp.app.viewmodel.profile.ProfileViewModel
import ru.rznnike.demokmp.app.viewmodel.settings.SettingsViewModel
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme
import ru.rznnike.demokmp.generated.resources.*

class SettingsScreen : AndroidNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel { SettingsViewModel() }
        val uiState by viewModel.uiState.collectAsState()
        val profileViewModel = windowViewModel<ProfileViewModel>()
        val appConfigurationViewModel = windowViewModel<AppConfigurationViewModel>()
        val appConfigurationUiState by appConfigurationViewModel.uiState.collectAsState()

        Column(
            modifier = Modifier
                .systemBarsPadding()
                .fillMaxSize()
        ) {
            Spacer(Modifier.height(16.dp))
            Toolbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                title = stringResource(Res.string.settings),
                leftButton = ToolbarButton(Res.drawable.ic_back) {
                    navigator.closeScreen()
                }
            )
            Spacer(Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
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
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            val nameString = "%s: %s".format(
                                stringResource(Res.string.user_name),
                                profileViewModel.nameInput
                            )
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = nameString,
                                textAlign = TextAlign.Center,
                            )
                            Spacer(Modifier.height(16.dp))
                            FilledButton(
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                onClick = {
                                    navigator.openScreen(NestedSettingsScreen())
                                }
                            ) {
                                TextR(Res.string.nested_settings)
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextR(
                                textRes = Res.string.test_counter,
                                modifier = Modifier
                                    .weight(1f)
                            )
                            Spacer(Modifier.width(16.dp))
                            SelectableOutlinedIconButton(
                                iconRes = Res.drawable.ic_minus,
                                onClick = {
                                    viewModel.onCounterInput(uiState.counter - 1)
                                }
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = uiState.counter.toString(),
                                modifier = Modifier
                                    .width(50.dp),
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.width(8.dp))
                            SelectableOutlinedIconButton(
                                iconRes = Res.drawable.ic_plus,
                                onClick = {
                                    viewModel.onCounterInput(uiState.counter + 1)
                                }
                            )
                        }
                    }

                    @Composable
                    fun OptionsSelector(
                        headerRes: StringResource,
                        buttonText: String,
                        content: @Composable (ColumnScope.(closeMenu: () -> Unit) -> Unit)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(Modifier.width(4.dp))
                                TextR(
                                    textRes = headerRes,
                                    modifier = Modifier.weight(1f),
                                )
                                Box {
                                    var showMenu by remember { mutableStateOf(false) }
                                    SelectableButton(
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
                                            onDismissRequest = { showMenu = false },
                                            containerColor = MaterialTheme.colorScheme.surface
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

                    Spacer(Modifier.height(16.dp))
                    OptionsSelector(
                        headerRes = Res.string.language,
                        buttonText = getSelectedLanguage().localizedName
                    ) { closeMenu ->
                        Language.entries.forEach { language ->
                            DropdownMenuItem(
                                text = {
                                    Text(language.localizedName)
                                },
                                onClick = {
                                    setSelectedLanguage(language)
                                    closeMenu()
                                }
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    OptionsSelector(
                        headerRes = Res.string.theme,
                        buttonText = stringResource(appConfigurationUiState.theme.nameRes)
                    ) { closeMenu ->
                        Theme.entries.forEach { theme ->
                            DropdownMenuItem(
                                text = {
                                    TextR(theme.nameRes)
                                },
                                onClick = {
                                    appConfigurationViewModel.setTheme(theme)
                                    closeMenu()
                                }
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}