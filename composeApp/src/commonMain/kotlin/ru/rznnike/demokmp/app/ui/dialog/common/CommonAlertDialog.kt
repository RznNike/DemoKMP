package ru.rznnike.demokmp.app.ui.dialog.common

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.rznnike.demokmp.app.ui.view.SelectableButton

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
    val focusRequester = remember { FocusRequester() }

    Dialog(
        onDismissRequest = {
            onCancelListener?.invoke()
        },
        properties = DialogProperties(
            dismissOnBackPress = cancellable,
            dismissOnClickOutside = cancellable
        )
    ) {
        Card(
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text(
                    text = header,
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
                    SelectableButton(
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
                            actionsReversed.forEachIndexed { index, action ->
                                DialogButton(
                                    modifier = Modifier.let {
                                        if (index == actions.lastIndex) it.focusRequester(focusRequester) else it
                                    },
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
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .let {
                                        if (index == 0) it.focusRequester(focusRequester) else it
                                    },
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

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }
}
