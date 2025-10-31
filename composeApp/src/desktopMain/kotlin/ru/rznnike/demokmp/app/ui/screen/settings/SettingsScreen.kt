package ru.rznnike.demokmp.app.ui.screen.settings

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.navigation.DesktopNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.view.*
import ru.rznnike.demokmp.app.utils.nameRes
import ru.rznnike.demokmp.app.utils.windowViewModel
import ru.rznnike.demokmp.app.viewmodel.global.configuration.AppConfigurationViewModel
import ru.rznnike.demokmp.app.viewmodel.global.profile.ProfileViewModel
import ru.rznnike.demokmp.app.viewmodel.settings.SettingsViewModel
import ru.rznnike.demokmp.domain.model.common.Language
import ru.rznnike.demokmp.domain.model.common.Theme
import ru.rznnike.demokmp.domain.model.common.UiScale
import ru.rznnike.demokmp.generated.resources.*

@Serializable
class SettingsScreen : DesktopNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel { SettingsViewModel() }
        val uiState by viewModel.uiState.collectAsState()
        val profileViewModel = windowViewModel<ProfileViewModel>()
        val appConfigurationViewModel: AppConfigurationViewModel = koinInject()
        val appConfigurationUiState by appConfigurationViewModel.uiState.collectAsState()

        screenKeyEventCallback = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                when {
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.W) -> navigator.closeScreen()
                }
            }
        }

        key(Locale.current) {
            Column(
                modifier = Modifier.fillMaxSize()
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
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Spacer(Modifier.width(4.dp))
                                val nameString = "%s: %s".format(
                                    stringResource(Res.string.user_name),
                                    profileViewModel.nameInput
                                )
                                Text(
                                    text = nameString,
                                    modifier = Modifier.weight(1f)
                                )
                                Spacer(Modifier.width(16.dp))
                                SelectableButton(
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

                        Spacer(Modifier.height(16.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.medium,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            FlowRow(
                                modifier = Modifier.padding(top = 12.dp, bottom = 16.dp, start = 16.dp, end = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                DropdownSelector(
                                    modifier = Modifier
                                        .width(150.dp),
                                    label = stringResource(Res.string.language),
                                    items = Language.entries,
                                    selectedItem = appConfigurationUiState.language,
                                    itemNameRetriever = { it?.localizedName ?: "" },
                                    onItemSelected = appConfigurationViewModel::setLanguage
                                )
                                DropdownSelector(
                                    modifier = Modifier
                                        .width(150.dp),
                                    label = stringResource(Res.string.theme),
                                    items = Theme.entries,
                                    selectedItem = appConfigurationUiState.theme,
                                    itemNameRetriever = { it?.let { stringResource(it.nameRes) } ?: "" },
                                    onItemSelected = appConfigurationViewModel::setTheme
                                )
                                DropdownSelector(
                                    modifier = Modifier
                                        .width(150.dp),
                                    label = stringResource(Res.string.ui_scale),
                                    items = UiScale.entries,
                                    selectedItem = appConfigurationUiState.uiScale,
                                    itemNameRetriever = { it?.let { "%d%%".format(it.value) } ?: "" },
                                    onItemSelected = appConfigurationViewModel::setUiScale
                                )
                            }
                        }
                        Spacer(Modifier.height(16.dp))
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