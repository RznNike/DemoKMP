package ru.rznnike.demokmp.app.ui.screen.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import demokmp.composeapp.generated.resources.*
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.model.common.toAppModel
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getScreenNavigator
import ru.rznnike.demokmp.app.utils.TextR
import ru.rznnike.demokmp.app.viewmodel.configuration.AppConfigurationViewModel
import ru.rznnike.demokmp.app.viewmodel.profile.ProfileViewModel
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme

class NestedSettingsScreen : NavigationScreen() {
    @Preview
    @Composable
    override fun Content() {
        val profileViewModel: ProfileViewModel = koinInject()
        val appConfigurationViewModel: AppConfigurationViewModel = koinInject()
        val appConfigurationUiState by appConfigurationViewModel.uiState.collectAsState()

        val screenNavigator = getScreenNavigator()

        var showLanguages by remember { mutableStateOf(false) }
        var showThemes by remember { mutableStateOf(false) }

        key(appConfigurationUiState.language.tag) {
            Column(modifier = Modifier.padding(20.dp)) {
                TextR(
                    textRes = Res.string.nested_settings_screen_title,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                )

                TextR(
                    textRes = Res.string.enter_user_name,
                    style = TextStyle(fontSize = 20.sp),
                    modifier = Modifier.fillMaxWidth()
                        .padding(top = 10.dp)
                )
                OutlinedTextField(
                    value = profileViewModel.nameInput,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    onValueChange = profileViewModel::onNameInput
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

                Button(
                    modifier = Modifier.padding(top = 10.dp),
                    onClick = {
                        screenNavigator.close()
                    }
                ) {
                    TextR(Res.string.close)
                }
            }
        }
    }
}