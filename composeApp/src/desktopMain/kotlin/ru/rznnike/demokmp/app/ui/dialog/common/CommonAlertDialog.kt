package ru.rznnike.demokmp.app.ui.dialog.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun CommonAlertDialog(
    type: AlertDialogType,
    header: String,
    message: String? = null,
    cancellable: Boolean = true,
    onCancelListener: (() -> Unit)? = null,
    actions: List<AlertDialogAction>
) {
    Dialog(
        onDismissRequest = {
            onCancelListener?.invoke()
        },
        properties = DialogProperties(
            dismissOnBackPress = cancellable,
            dismissOnClickOutside = cancellable
        )
    ) {
        Card {
            Column(
                modifier = Modifier.padding(20.dp),
            ) {
                Text(
                    text = header,
                    style = TextStyle(fontSize = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                if (!message.isNullOrBlank()) {
                    Text(
                        text = message,
                        style = TextStyle(fontSize = 16.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 10.dp)
                    )
                }
                when (type) {
                    AlertDialogType.HORIZONTAL -> Row(
                        modifier = Modifier.padding(top = 10.dp)
                    ) {
                        actions.forEachIndexed { index, action ->
                            val padding = if (index == 0) 0.dp else 10.dp
                            Button(
                                modifier = Modifier.padding(start = padding),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (action.accent) Color.Blue else Color.Gray,
                                    contentColor = Color.White
                                ),
                                onClick = {
                                    action.callback()
                                }
                            ) {
                                Text(action.text)
                            }
                        }
                    }
                    AlertDialogType.VERTICAL -> Column {
                        actions.forEach { action ->
                            Button(
                                modifier = Modifier.padding(top = 10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (action.accent) Color.Blue else Color.Gray,
                                    contentColor = Color.White
                                ),
                                onClick = {
                                    action.callback()
                                }
                            ) {
                                Text(action.text)
                            }
                        }
                    }
                }
            }
        }
    }
}
