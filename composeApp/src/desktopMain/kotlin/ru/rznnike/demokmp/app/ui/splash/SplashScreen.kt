package ru.rznnike.demokmp.app.ui.splash

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.ui.main.MainFlow
import ru.rznnike.demokmp.app.navigation.getFlowNavigator

class SplashScreen : NavigationScreen {
    @Preview
    @Composable
    override fun Content() {
        val flowNavigator = getFlowNavigator()

        MaterialTheme {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "I am splash!",
                    style = TextStyle(fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                )

                Button(
                    modifier = Modifier.padding(start = 20.dp, top = 10.dp),
                    onClick = {
                        flowNavigator.newRoot(MainFlow())
                    }
                ) {
                    Text("Go to main")
                }
            }
        }
    }
}