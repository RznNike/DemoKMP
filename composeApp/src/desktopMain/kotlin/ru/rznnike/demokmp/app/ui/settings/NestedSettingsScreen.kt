package ru.rznnike.demokmp.app.ui.settings

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getScreenNavigator

class NestedSettingsScreen : NavigationScreen {
    @Preview
    @Composable
    override fun Content() {
        val screenNavigator = getScreenNavigator()

        MaterialTheme {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "I am nested settings screen!",
                    style = TextStyle(fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                )

                Button(
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp),
                    onClick = {
                        screenNavigator.close()
                    }
                ) {
                    Text("Close")
                }
            }
        }
    }
}