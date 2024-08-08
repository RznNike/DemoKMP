package ru.rznnike.demokmp.app.ui.screen.splash

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import demokmp.composeapp.generated.resources.Res
import demokmp.composeapp.generated.resources.go_to_main
import demokmp.composeapp.generated.resources.splash_screen_title
import ru.rznnike.demokmp.app.navigation.NavigationScreen
import ru.rznnike.demokmp.app.navigation.getFlowNavigator
import ru.rznnike.demokmp.app.ui.screen.home.HomeFlow
import ru.rznnike.demokmp.app.utils.TextR

class SplashScreen : NavigationScreen() {
    @Preview
    @Composable
    override fun Content() {
        val flowNavigator = getFlowNavigator()

        MaterialTheme {
            Column(modifier = Modifier.padding(20.dp)) {
                TextR(
                    textRes = Res.string.splash_screen_title,
                    style = TextStyle(fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().align(Alignment.CenterHorizontally)
                )

                Button(
                    modifier = Modifier.padding(top = 10.dp),
                    onClick = {
                        flowNavigator.newRoot(HomeFlow())
                    }
                ) {
                    TextR(Res.string.go_to_main)
                }
            }
        }
    }
}