package ru.rznnike.demokmp.app.ui.screen.customui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.AndroidNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.view.*
import ru.rznnike.demokmp.app.utils.cardAlterBackground
import ru.rznnike.demokmp.app.utils.cardBackground
import ru.rznnike.demokmp.app.utils.dashedBorder
import ru.rznnike.demokmp.app.utils.navigationBarsSidesPadding
import ru.rznnike.demokmp.app.utils.onClick
import ru.rznnike.demokmp.app.utils.statusBarsAndCutoutPadding
import ru.rznnike.demokmp.app.viewmodel.customui.CustomUIViewModel
import ru.rznnike.demokmp.generated.resources.*

@Serializable
class CustomUIScreen : AndroidNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel { CustomUIViewModel() }
        val uiState by viewModel.uiState.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
                .statusBarsAndCutoutPadding()
        ) {
            Toolbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsSidesPadding(),
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
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(bottom = 16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .cardBackground()
                            .padding(4.dp)
                    ) {
                        CustomUIViewModel.Tab.entries.forEach {
                            TabText(
                                modifier = Modifier
                                    .onClick {
                                        viewModel.onTabChanged(it)
                                    }
                                    .padding(12.dp),
                                text = stringResource(it.nameRes),
                                selected = it == uiState.selectedTab
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .cardBackground()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        FilledButton(
                            onClick = { }
                        ) {
                            Text(Res.string.button)
                        }
                        OutlinedRoundedButton(
                            onClick = { }
                        ) {
                            Text(Res.string.button)
                        }
                        SelectableOutlinedIconButton(
                            iconRes = Res.drawable.ic_refresh,
                            onClick = { }
                        )
                    }
                    Spacer(Modifier.height(16.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .cardBackground()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        SlimOutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = viewModel.textInput,
                            onValueChange = viewModel::onTextInput,
                            singleLine = true,
                            label = {
                                Text(Res.string.input_field)
                            }
                        )
                        Spacer(Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier
                                    .cardAlterBackground()
                                    .dashedBorder()
                                    .padding(16.dp),
                                textRes = Res.string.dashed_border
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    Spacer(Modifier.height(16.dp))
                }
            }
        }
    }
}