package ru.rznnike.demokmp.app.ui.screen.wsexample

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import demokmp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getScreenNavigator
import ru.rznnike.demokmp.app.ui.item.WebSocketMessageItem
import ru.rznnike.demokmp.app.ui.view.Toolbar
import ru.rznnike.demokmp.app.ui.view.ToolbarButton
import ru.rznnike.demokmp.app.utils.TextR
import ru.rznnike.demokmp.app.viewmodel.wsexample.WebSocketsExampleViewModel

class WebSocketsExampleScreen : NavigationScreen() {
    @Preview
    @Composable
    override fun Content() {
        val webSocketsExampleViewModel = viewModel { WebSocketsExampleViewModel() }
        val webSocketsExampleUiState by webSocketsExampleViewModel.uiState.collectAsState()

        val screenNavigator = getScreenNavigator()

        Column {
            Spacer(Modifier.height(16.dp))
            Toolbar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                title = stringResource(Res.string.ws_example),
                leftButton = ToolbarButton(Res.drawable.ic_back) {
                    screenNavigator.close()
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
                        value = webSocketsExampleViewModel.messageInput,
                        singleLine = true,
                        label = {
                            TextR(Res.string.message)
                        },
                        modifier = Modifier
                            .weight(1f),
                        onValueChange = webSocketsExampleViewModel::onMessageInput
                    )
                    Spacer(Modifier.width(16.dp))
                    Button(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxHeight(),
                        onClick = {
                            webSocketsExampleViewModel.sendMessage()
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
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(webSocketsExampleUiState.messages) { item ->
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
            }
        }
    }
}