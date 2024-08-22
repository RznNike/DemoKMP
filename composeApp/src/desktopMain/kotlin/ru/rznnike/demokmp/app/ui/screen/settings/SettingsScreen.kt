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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import demokmp.composeapp.generated.resources.*
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

        var showLanguages by remember { mutableStateOf(false) }
        var showThemes by remember { mutableStateOf(false) }

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
                        Text(
                            text = settingsUiState.counter.toString(),
                            style = TextStyle(fontSize = 20.sp),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 10.dp)
                        )
                        Button(
                            modifier = Modifier.padding(top = 10.dp),
                            onClick = {
                                settingsViewModel.incrementCounter()
                            }
                        ) {
                            Text("++")
                        }

                        Button(
                            modifier = Modifier.padding(top = 10.dp),
                            onClick = {
                                screenNavigator.open(NestedSettingsScreen())
                            }
                        ) {
                            TextR(Res.string.open_nested_settings)
                        }

                        val nameString = "%s: %s".format(
                            stringResource(Res.string.user_name),
                            profileViewModel.nameInput
                        )
                        Text(
                            text = nameString,
                            style = TextStyle(fontSize = 20.sp),
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 10.dp)
                        )

                        TextR(
                            textRes = Res.string.language,
                            style = TextStyle(fontSize = 20.sp),
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 10.dp)
                        )
                        Row(
                            modifier = Modifier.padding(top = 10.dp)
                        ) {
                            DropdownMenu(
                                expanded = showLanguages,
                                onDismissRequest = { showLanguages = false }
                            ) {
                                Language.entries.forEach { language ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(language.localizedName)
                                        },
                                        onClick = {
                                            appConfigurationViewModel.setLanguage(language)
                                            showLanguages = false
                                        }
                                    )
                                }
                            }
                        }
                        Button(
                            modifier = Modifier.padding(top = 10.dp),
                            onClick = {
                                showLanguages = !showLanguages
                            }
                        ) {
                            Text(appConfigurationUiState.language.localizedName)
                        }

                        TextR(
                            textRes = Res.string.theme,
                            style = TextStyle(fontSize = 20.sp),
                            modifier = Modifier.fillMaxWidth()
                                .padding(top = 10.dp)
                        )
                        Row(
                            modifier = Modifier.padding(top = 10.dp)
                        ) {
                            DropdownMenu(
                                expanded = showThemes,
                                onDismissRequest = { showThemes = false }
                            ) {
                                Theme.entries.forEach { theme ->
                                    DropdownMenuItem(
                                        text = {
                                            TextR(theme.toAppModel().nameRes)
                                        },
                                        onClick = {
                                            appConfigurationViewModel.setTheme(theme)
                                            showThemes = false
                                        }
                                    )
                                }
                            }
                        }
                        Button(
                            modifier = Modifier.padding(top = 10.dp),
                            onClick = {
                                showThemes = !showThemes
                            }
                        ) {
                            TextR(appConfigurationUiState.theme.toAppModel().nameRes)
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