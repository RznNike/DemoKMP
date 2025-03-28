package ru.rznnike.demokmp.app.ui.screen.wsexample

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.DesktopNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.item.WebSocketMessageItem
import ru.rznnike.demokmp.app.ui.view.*
import ru.rznnike.demokmp.app.utils.onEnterKey
import ru.rznnike.demokmp.app.viewmodel.wsexample.WebSocketsExampleViewModel
import ru.rznnike.demokmp.domain.model.websocket.WebSocketConnectionState
import ru.rznnike.demokmp.generated.resources.*

class WebSocketsExampleScreen : DesktopNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel { WebSocketsExampleViewModel() }
        val uiState by viewModel.uiState.collectAsState()

        screenKeyEventCallback = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                when {
                    keyEvent.isCtrlPressed && (keyEvent.key == Key.W) -> navigator.closeScreen()
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Toolbar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.ws_example),
                leftButton = ToolbarButton(Res.drawable.ic_back) {
                    navigator.closeScreen()
                }
            )
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
                                viewModel.sendMessage()
                            },
                        value = viewModel.messageInput,
                        singleLine = true,
                        label = {
                            TextR(Res.string.message)
                        },
                        onValueChange = viewModel::onMessageInput
                    )
                    Spacer(Modifier.width(12.dp))
                    SelectableButton(
                        modifier = Modifier.padding(vertical = 12.dp),
                        onClick = {
                            viewModel.sendMessage()
                        }
                    ) {
                        TextR(Res.string.send)
                    }
                    Spacer(Modifier.width(12.dp))
                }
            }

            Spacer(Modifier.height(16.dp))
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface
            ) {
                Box(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val state = rememberLazyListState()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp),
                        state = state,
                        contentPadding = PaddingValues(top = 16.dp, bottom = 64.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = uiState.messages
                        ) { item ->
                            WebSocketMessageItem(item)
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight(),
                        adapter = rememberScrollbarAdapter(state)
                    )

                    LaunchedEffect(uiState.messages) {
                        state.animateScrollToItem((state.layoutInfo.totalItemsCount - 1).coerceAtLeast(0))
                    }

                    Row(
                        modifier = Modifier
                            .padding(bottom = 16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primary,
                                shape = MaterialTheme.shapes.medium
                            )
                            .padding(8.dp)
                            .align(Alignment.BottomCenter),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "${stringResource(Res.string.connection)}:",
                            style = MaterialTheme.typography.bodyLarge.let {
                                it.copy(
                                    lineHeight = it.fontSize
                                )
                            },
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    color = if (uiState.connectionState == WebSocketConnectionState.CONNECTED) {
                                        Color.Green
                                    } else {
                                        Color.Red
                                    },
                                    shape = CircleShape
                                )
                        )
                    }
                }
            }
        }
    }
}