package ru.rznnike.demokmp.app.ui.screen.customui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.view.*
import ru.rznnike.demokmp.app.utils.onClick
import ru.rznnike.demokmp.app.utils.onEnterKey
import ru.rznnike.demokmp.app.viewmodel.customui.CustomUIViewModel
import ru.rznnike.demokmp.generated.resources.*
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.button
import ru.rznnike.demokmp.generated.resources.custom_ui_elements
import ru.rznnike.demokmp.generated.resources.ic_back

class CustomUIScreen : NavigationScreen() {
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel { CustomUIViewModel() }
        val uiState by viewModel.uiState.collectAsState()

        screenKeyEventCallback = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                when {
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.W) -> navigator.closeScreen()
                }
            }
        }

        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Toolbar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.custom_ui_elements),
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
                        .fillMaxSize()
                        .verticalScroll(state)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                        ) {
                            CustomUIViewModel.Tab.entries.forEach {
                                TabText(
                                    modifier = Modifier
                                        .onClick {
                                            viewModel.onTabChanged(it)
                                        }
                                        .padding(12.dp),
                                    textRes = it.nameRes,
                                    selected = it == uiState.selectedTab
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        FlowRow(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            FilledButton(
                                modifier = Modifier.padding(vertical = 4.dp),
                                onClick = { }
                            ) {
                                TextR(Res.string.button)
                            }
                            SelectableButton(
                                onClick = { }
                            ) {
                                TextR(Res.string.button)
                            }
                            OutlinedRoundedButton(
                                modifier = Modifier.padding(vertical = 4.dp),
                                onClick = { }
                            ) {
                                TextR(Res.string.button)
                            }
                            SelectableOutlinedButton(
                                modifier = Modifier.padding(vertical = 4.dp),
                                onClick = { }
                            ) {
                                TextR(Res.string.button)
                            }
                            SelectableOutlinedIconButton(
                                modifier = Modifier.padding(vertical = 4.dp),
                                iconRes = Res.drawable.ic_refresh,
                                onClick = { }
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                SlimOutlinedTextField(
                                    modifier = Modifier
                                        .width(200.dp)
                                        .onEnterKey {
                                            focusManager.moveFocus(FocusDirection.Next)
                                        },
                                    value = viewModel.textInput,
                                    onValueChange = viewModel::onTextInput,
                                    singleLine = true,
                                    label = {
                                        TextR(Res.string.input_field)
                                    }
                                )
                                Spacer(Modifier.width(16.dp))
                                DateTextField(
                                    modifier = Modifier
                                        .width(155.dp)
                                        .height(48.dp),
                                    labelRes = Res.string.date_selection,
                                    value = viewModel.dateInput,
                                    isError = viewModel.dateError,
                                    calendarInitialDate = uiState.date,
                                    calendarMinDate = uiState.dateMin,
                                    calendarMaxDate = uiState.dateMax,
                                    onValueChange = viewModel::onDateInput,
                                    onDateChange = viewModel::onDateChange,
                                    onSave = viewModel::confirmDateInput
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                DropdownSelector(
                                    modifier = Modifier
                                        .width(200.dp),
                                    label = stringResource(Res.string.dropdown),
                                    items = uiState.dropdownOptions,
                                    selectedItem = uiState.dropdownSelection,
                                    itemNameRetriever = { it ?: "" },
                                    onItemSelected = viewModel::onDropdownSelectionChanged
                                )
                                Spacer(Modifier.width(16.dp))
                                DropdownQuerySelector(
                                    modifier = Modifier
                                        .width(200.dp),
                                    label = stringResource(Res.string.dropdown_with_filter),
                                    items = uiState.dropdownOptions,
                                    selectedItem = uiState.dropdownQuerySelection,
                                    itemNameRetriever = { it ?: "" },
                                    onItemSelected = viewModel::onDropdownQuerySelectionChanged
                                )
                            }
                            Spacer(Modifier.height(8.dp))
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