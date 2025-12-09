package ru.rznnike.demokmp.app.ui.screen.comobjectexample

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.DesktopNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.theme.bodyLargeBold
import ru.rznnike.demokmp.app.ui.view.*
import ru.rznnike.demokmp.app.ui.viewmodel.comobjectexample.ComObjectExampleViewModel
import ru.rznnike.demokmp.app.utils.onEnterKey
import ru.rznnike.demokmp.generated.resources.*

@Serializable
class ComObjectExampleScreen : DesktopNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel { ComObjectExampleViewModel() }
        val uiState by viewModel.uiState.collectAsState()

        screenKeyEventCallback = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                when {
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.W) -> navigator.closeScreen()
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(Modifier.height(16.dp))
            Toolbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                title = stringResource(Res.string.com_object_example),
                leftButton = ToolbarButton(
                    iconRes = Res.drawable.ic_back,
                    tooltip = "Ctrl+W"
                ) {
                    navigator.closeScreen()
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
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            TextR(
                                modifier = Modifier.fillMaxWidth(),
                                textRes = Res.string.pc_data_header,
                                style = MaterialTheme.typography.bodyLargeBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = uiState.pcData
                            )
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Spacer(Modifier.width(16.dp))
                            SlimOutlinedTextField(
                                modifier = Modifier
                                    .padding(top = 8.dp, bottom = 16.dp)
                                    .weight(1f)
                                    .onEnterKey {
                                        viewModel.openPath()
                                    },
                                value = viewModel.pathInput,
                                singleLine = true,
                                label = {
                                    TextR(Res.string.path_to_folder_or_file)
                                },
                                onValueChange = viewModel::onPathInput
                            )
                            Spacer(Modifier.width(12.dp))
                            SelectableButton(
                                modifier = Modifier.padding(vertical = 12.dp),
                                onClick = {
                                    viewModel.openPath()
                                }
                            ) {
                                TextR(Res.string.open)
                            }
                            Spacer(Modifier.width(12.dp))
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SelectableButton(
                                onClick = viewModel::minimizeAllWindows
                            ) {
                                TextR(
                                    textRes = Res.string.minimize_all_windows
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