package ru.rznnike.demokmp.app.ui.screen.customui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.AndroidNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.view.*
import ru.rznnike.demokmp.app.utils.onClick
import ru.rznnike.demokmp.app.viewmodel.customui.CustomUIViewModel
import ru.rznnike.demokmp.generated.resources.*

class CustomUIScreen : AndroidNavigationScreen() {
    @OptIn(ExperimentalLayoutApi::class)
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel { CustomUIViewModel() }
        val uiState by viewModel.uiState.collectAsState()

        Column(
            modifier = Modifier
                .systemBarsPadding()
                .imePadding()
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
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            FilledButton(
                                onClick = { }
                            ) {
                                TextR(Res.string.button)
                            }
                            OutlinedRoundedButton(
                                onClick = { }
                            ) {
                                TextR(Res.string.button)
                            }
                            SelectableOutlinedIconButton(
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
                            SlimOutlinedTextField(
                                modifier = Modifier.fillMaxWidth(),
                                value = viewModel.textInput,
                                onValueChange = viewModel::onTextInput,
                                singleLine = true,
                                label = {
                                    TextR(Res.string.input_field)
                                }
                            )
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}