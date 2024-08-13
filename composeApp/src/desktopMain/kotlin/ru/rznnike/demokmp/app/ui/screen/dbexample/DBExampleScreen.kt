package ru.rznnike.demokmp.app.ui.screen.dbexample

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import demokmp.composeapp.generated.resources.*
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getScreenNavigator
import ru.rznnike.demokmp.app.ui.item.DBExampleDataItem
import ru.rznnike.demokmp.app.utils.TextR
import ru.rznnike.demokmp.app.viewmodel.dbexample.DBExampleViewModel

class DBExampleScreen : NavigationScreen() {
    @Preview
    @Composable
    override fun Content() {
        val dbExampleViewModel = viewModel { DBExampleViewModel() }
        val dbExampleUiState by dbExampleViewModel.uiState.collectAsState()

        val screenNavigator = getScreenNavigator()

        MaterialTheme {
            Column(modifier = Modifier.padding(vertical = 20.dp)) {
                TextR(
                    textRes = Res.string.db_example_screen_title,
                    style = TextStyle(fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(20.dp))
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Spacer(modifier = Modifier.width(20.dp))
                    OutlinedTextField(
                        value = dbExampleUiState.nameInput,
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f),
                        onValueChange = dbExampleViewModel::onNameInput
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            dbExampleViewModel.addData()
                        }
                    ) {
                        TextR(Res.string.add)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = {
                            dbExampleViewModel.deleteAllData()
                        }
                    ) {
                        TextR(Res.string.delete_all)
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                }

                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) {
                    val state = rememberLazyListState()
                    LazyColumn(
                        state = state,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 20.dp)
                    ) {
                        itemsIndexed(dbExampleUiState.data) { index, item ->
                            DBExampleDataItem(item) {
                                dbExampleViewModel.deleteData(item)
                            }
                            if (index < dbExampleUiState.data.lastIndex) {
                                Divider()
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

                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    onClick = {
                        screenNavigator.close()
                    }
                ) {
                    TextR(Res.string.close)
                }
            }
        }
    }
}