package ru.rznnike.demokmp.app.utils

import androidx.compose.runtime.*
import kotlinx.coroutines.ensureActive
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.error

@Composable
fun rememberResTextFile(path: String): String {
    var fileText by remember { mutableStateOf("") }
    val errorText = stringResource(Res.string.error)
    LaunchedEffect(Unit) {
        runCatching {
            val buffer = Res.readBytes(path)
            String(buffer)
        }.onSuccess {
            fileText = it.replace("\r", "")
        }.onFailure {
            ensureActive()
            fileText = errorText
        }
    }
    return fileText
}