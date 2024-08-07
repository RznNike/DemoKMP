package ru.rznnike.demokmp.app.ui.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import demokmp.composeapp.generated.resources.*
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getScreenNavigator
import ru.rznnike.demokmp.app.utils.TextR
import ru.rznnike.demokmp.app.viewmodel.language.LanguageViewModel
import ru.rznnike.demokmp.app.viewmodel.profile.ProfileViewModel
import ru.rznnike.demokmp.domain.model.common.Language

class NestedSettingsScreen : NavigationScreen() {
    @Preview
    @Composable
    override fun Content() {
        val profileViewModel = koinInject<ProfileViewModel>()
        val profileUiState by profileViewModel.uiState.collectAsState()
        val languageViewModel = koinInject<LanguageViewModel>()
        val languageUiState by languageViewModel.uiState.collectAsState()

        val screenNavigator = getScreenNavigator()

        var showLanguages by remember { mutableStateOf(false) }

        key(languageUiState.language) {
            MaterialTheme {
                Column(modifier = Modifier.padding(20.dp)) {
                    TextR(
                        textRes = Res.string.nested_settings_screen_title,
                        style = TextStyle(fontSize = 20.sp),
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
                        value = profileUiState.name,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        onValueChange = profileViewModel::changeName
                    )

                    TextR(
                        textRes = Res.string.select_language,
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
                                    onClick = {
                                        languageViewModel.setLanguage(language)
                                        showLanguages = false
                                    }
                                ) {
                                    Text(language.localizedName)
                                }
                            }
                        }
                    }
                    Button(
                        modifier = Modifier.padding(top = 10.dp),
                        onClick = {
                            showLanguages = !showLanguages
                        }
                    ) {
                        Text(languageUiState.language.localizedName)
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
}