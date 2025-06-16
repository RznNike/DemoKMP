package ru.rznnike.demokmp.app.ui.screen.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.AndroidNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.view.*
import ru.rznnike.demokmp.app.viewmodel.navigation.NavigationExampleViewModel
import ru.rznnike.demokmp.generated.resources.*

@Serializable
class NavigationExampleScreen(
    val screenNumber: Int
) : AndroidNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel {
            NavigationExampleViewModel(screenNumber = screenNumber)
        }
        val uiState by viewModel.uiState.collectAsState()

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
                title = stringResource(Res.string.navigation_example),
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
                            modifier = Modifier.padding(horizontal = 12.dp)
                        ) {
                            Spacer(Modifier.height(16.dp))
                            Text(
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .fillMaxWidth(),
                                text = "%s: %d".format(
                                    stringResource(Res.string.screen_number_in_flow),
                                    uiState.screenNumber
                                )
                            )
                            Spacer(Modifier.height(12.dp))
                            SelectableButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navigator.openScreen(
                                        NavigationExampleScreen(screenNumber = uiState.screenNumber + 1)
                                    )
                                }
                            ) {
                                TextR(Res.string.open_new_screen)
                            }
                            Spacer(Modifier.height(8.dp))
                            SelectableButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navigator.closeScreen()
                                }
                            ) {
                                TextR(Res.string.close_screen)
                            }
                            Spacer(Modifier.height(8.dp))
                            SelectableButton(
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    navigator.closeFlow()
                                }
                            ) {
                                TextR(Res.string.close_flow)
                            }
                            Spacer(Modifier.height(12.dp))
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
                                textRes = Res.string.test_counter_local,
                                modifier = Modifier
                                    .weight(1f)
                            )
                            Spacer(Modifier.width(16.dp))
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
                                    viewModel.increaseCounter()
                                }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}