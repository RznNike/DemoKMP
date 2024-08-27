package ru.rznnike.demokmp.app.ui.dialog.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@OptIn(ExperimentalLayoutApi::class)
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
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = header,
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                if (!message.isNullOrBlank()) {
                    Text(
                        text = message,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
                    )
                }

                @Composable
                fun DialogButton(modifier: Modifier = Modifier, action: AlertDialogAction) {
                    Button(
                        modifier = modifier,
                        colors = if (action.accent) {
                            ButtonDefaults.buttonColors()
                        } else {
                            ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        },
                        onClick = {
                            action.callback()
                        }
                    ) {
                        Text(action.text)
                    }
                }

                when (type) {
                    AlertDialogType.HORIZONTAL -> {
                        val actionsReversed = remember(actions) { actions.reversed() }
                        FlowRow(
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .fillMaxWidth()
                                .align(Alignment.End),
                            horizontalArrangement = Arrangement.spacedBy(
                                space = 16.dp,
                                alignment = Alignment.End
                            ),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            actionsReversed.forEach { action ->
                                DialogButton(
                                    action = action
                                )
                            }
                        }
                    }
                    AlertDialogType.VERTICAL -> Column(
                        modifier = Modifier
                            .padding(top = 16.dp)
                            .fillMaxWidth()
                    ) {
                        actions.forEachIndexed { index, action ->
                            DialogButton(
                                modifier = Modifier.align(Alignment.End),
                                action = action
                            )
                            if (index < actions.lastIndex) {
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
