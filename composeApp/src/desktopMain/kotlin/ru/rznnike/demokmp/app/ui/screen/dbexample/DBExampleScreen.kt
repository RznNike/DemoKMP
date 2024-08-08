package ru.rznnike.demokmp.app.ui.screen.dbexample

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
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
            Column(modifier = Modifier.padding(20.dp)) {
                TextR(
                    textRes = Res.string.db_example_screen_title,
                    style = TextStyle(fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                        .align(Alignment.CenterHorizontally)
                )

                Row(
                    modifier = Modifier.padding(top = 10.dp).fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = dbExampleUiState.nameInput,
                        singleLine = true,
                        modifier = Modifier.width(300.dp),
                        onValueChange = dbExampleViewModel::onNameInput
                    )
                    Button(
                        modifier = Modifier.padding(start = 10.dp),
                        onClick = {
                            dbExampleViewModel.addData()
                        }
                    ) {
                        TextR(Res.string.add)
                    }
                    Button(
                        modifier = Modifier.padding(start = 10.dp),
                        onClick = {
                            dbExampleViewModel.deleteAllData()
                        }
                    ) {
                        TextR(Res.string.delete_all)
                    }
                }

                dbExampleUiState.data.forEach {
                    DBExampleDataItem(it) {
                        dbExampleViewModel.deleteData(it)
                    }
                }

                Button(
                    modifier = Modifier.padding(top = 10.dp),
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