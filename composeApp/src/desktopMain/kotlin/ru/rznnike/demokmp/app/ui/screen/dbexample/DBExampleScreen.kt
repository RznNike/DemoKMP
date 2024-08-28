package ru.rznnike.demokmp.app.ui.screen.dbexample

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import demokmp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getScreenNavigator
import ru.rznnike.demokmp.app.ui.item.DBExampleDataItem
import ru.rznnike.demokmp.app.ui.theme.localCustomColorScheme
import ru.rznnike.demokmp.app.ui.view.Toolbar
import ru.rznnike.demokmp.app.ui.view.ToolbarButton
import ru.rznnike.demokmp.app.utils.TextR
import ru.rznnike.demokmp.app.viewmodel.dbexample.DBExampleViewModel

class DBExampleScreen : NavigationScreen() {
    @Preview
    @Composable
    override fun Content() {
        val dbExampleViewModel = viewModel { DBExampleViewModel() }
        val dbExampleUiState by dbExampleViewModel.uiState.collectAsState()

        val screenNavigator = getScreenNavigator()

        var showToolbarMenu by remember { mutableStateOf(false) }

        Column {
            Spacer(Modifier.height(16.dp))
            Box(
                contentAlignment = Alignment.TopEnd
            ) {
                Toolbar(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    title = stringResource(Res.string.db_example),
                    leftButton = ToolbarButton(Res.drawable.ic_back) {
                        screenNavigator.close()
                    },
                    rightButton = ToolbarButton(Res.drawable.ic_menu) {
                        showToolbarMenu = true
                    }
                )
                Box(
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    DropdownMenu(
                        expanded = showToolbarMenu,
                        onDismissRequest = { showToolbarMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                TextR(Res.string.delete_all)
                            },
                            onClick = {
                                showToolbarMenu = false
                                dbExampleViewModel.deleteAllData()
                            }
                        )
                    }
                }
            }
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
                        value = dbExampleViewModel.nameInput,
                        singleLine = true,
                        label = {
                            TextR(Res.string.db_example_input_label)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .onKeyEvent { keyEvent ->
                                when {
                                    (keyEvent.key.nativeKeyCode == Key.Enter.nativeKeyCode) && (keyEvent.type == KeyEventType.KeyUp) -> {
                                        dbExampleViewModel.addData()
                                        true
                                    }
                                    else -> false
                                }
                            },
                        onValueChange = dbExampleViewModel::onNameInput
                    )
                    Spacer(Modifier.width(16.dp))
                    Button(
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxHeight(),
                        onClick = {
                            dbExampleViewModel.addData()
                        }
                    ) {
                        TextR(Res.string.add)
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
                        items(
                            items = dbExampleUiState.data,
                            key = { it.id }
                        ) { item ->
                            DBExampleDataItem(item) {
                                dbExampleViewModel.deleteData(item)
                            }
                        }
                    }

                    if (dbExampleUiState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(localCustomColorScheme.current.surfaceContainerA50),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(50.dp)
                            )
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