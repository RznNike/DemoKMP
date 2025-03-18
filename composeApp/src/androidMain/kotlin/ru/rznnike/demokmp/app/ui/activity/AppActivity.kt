package ru.rznnike.demokmp.app.ui.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.jetpack.ProvideNavigatorLifecycleKMPSupport
import org.koin.compose.KoinContext

class AppActivity : ComponentActivity() {
    @OptIn(ExperimentalVoyagerApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            KoinContext {
                ProvideNavigatorLifecycleKMPSupport {
//                    MainFrame()
                }
            }
        }
    }
}
