package ru.rznnike.demokmp.app.ui.screen.wsexample

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.item.WebSocketMessageItem
import ru.rznnike.demokmp.app.ui.view.TextR
import ru.rznnike.demokmp.app.ui.view.Toolbar
import ru.rznnike.demokmp.app.ui.view.ToolbarButton
import ru.rznnike.demokmp.app.viewmodel.wsexample.WebSocketsExampleViewModel
import ru.rznnike.demokmp.domain.model.websocket.WebSocketConnectionState
import ru.rznnike.demokmp.generated.resources.*

class WebSocketsExampleScreen : NavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        val viewModel = viewModel { WebSocketsExampleViewModel() }
        val uiState by viewModel.uiState.collectAsState()

        Column {
            Spacer(Modifier.height(16.dp))
            Toolbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                title = stringResource(Res.string.ws_example),
                leftButton = ToolbarButton(Res.drawable.ic_back) {
                    navigator.closeScreen()
                }
            )
            Spacer(Modifier.height(16.dp))

            Surface(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceContainerHigh
            ) {
                Row(
                    modifier = Modifier
                        .padding(
                            start = 16.dp,
                            end = 16.dp,
                            top = 8.dp,
                            bottom = 16.dp
                        )
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                ) {
                    OutlinedTextField(
                        value = viewModel.messageInput,
                        singleLine = true,
                        label = {
                            TextR(Res.string.message)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .onKeyEvent { keyEvent ->
                                when {
                                    (keyEvent.key.nativeKeyCode == Key.Enter.nativeKeyCode) && (keyEvent.type == KeyEventType.KeyUp) -> {
                                        viewModel.sendMessage()
                                        true
                                    }
                                    else -> false
                                }
                            },
                        onValueChange = viewModel::onMessageInput
                    )
                    Spacer(Modifier.width(16.dp))
                    Button(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxHeight(),
                        onClick = {
                            viewModel.sendMessage()
                        }
                    ) {
                        TextR(Res.string.send)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                val state = rememberLazyListState()
                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxSize()
                        .clip(
                            MaterialTheme.shapes.medium.copy(
                                bottomStart = CornerSize(0.dp),
                                bottomEnd = CornerSize(0.dp)
                            )
                        )
                ) {
                    LazyColumn(
                        state = state,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 64.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = uiState.messages,
                            key = { it.hashCode() }
                        ) { item ->
                            WebSocketMessageItem(item)
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(state)
                )

                Row(
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainerHigh,
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
                        }
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