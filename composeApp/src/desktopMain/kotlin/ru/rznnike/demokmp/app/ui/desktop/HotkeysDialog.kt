package ru.rznnike.demokmp.app.ui.desktop

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import ru.rznnike.demokmp.app.model.common.HotkeyDescription
import ru.rznnike.demokmp.app.ui.theme.bodyLargeBold
import ru.rznnike.demokmp.app.ui.theme.bodyMediumBold
import ru.rznnike.demokmp.app.ui.view.OutlinedRoundedButton
import ru.rznnike.demokmp.app.ui.view.Text
import ru.rznnike.demokmp.app.utils.cardAlterBackground
import ru.rznnike.demokmp.app.utils.cardBackground
import ru.rznnike.demokmp.generated.resources.*

private val MIN_WIDTH_DP = 400.dp
private val MAX_WIDTH_DP = 700.dp
private val MAX_HEIGHT_DP = 600.dp

@Composable
fun HotkeysDialog(
    showDialog: MutableState<Boolean>,
    screenHotkeysDescription: List<HotkeyDescription>,
    commonHotkeysDescription: List<HotkeyDescription>
) {
    if (showDialog.value) {
        Dialog(
            onDismissRequest = {
                showDialog.value = false
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .cardBackground()
                    .clip(MaterialTheme.shapes.medium)
            ) {
                Column(
                    modifier = Modifier
                        .requiredSizeIn(
                            minWidth = MIN_WIDTH_DP,
                            maxWidth = MAX_WIDTH_DP,
                            maxHeight = MAX_HEIGHT_DP
                        )
                ) {
                    @Composable
                    fun HotkeyRow(data: HotkeyDescription) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.width(130.dp)
                            ) {
                                Text(
                                    modifier = Modifier
                                        .cardAlterBackground()
                                        .padding(8.dp),
                                    text = data.hotkey,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Spacer(Modifier.width(8.dp))
                            Text(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(end = 8.dp),
                                text = data.description,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Text(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterHorizontally),
                        textRes = Res.string.hotkeys,
                        style = MaterialTheme.typography.bodyLargeBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .width(IntrinsicSize.Max)
                            .requiredSizeIn(
                                minWidth = MIN_WIDTH_DP
                            )
                    ) {
                        val scrollState = rememberScrollState()
                        var listHeight by remember { mutableIntStateOf(0) }
                        val listHeightDp = with(LocalDensity.current) {
                            listHeight.toDp()
                        }

                        Column(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .onGloballyPositioned {
                                    listHeight = it.size.height
                                }
                                .verticalScroll(
                                    state = scrollState
                                )
                        ) {
                            if (screenHotkeysDescription.isNotEmpty()) {
                                Text(
                                    textRes = Res.string.hotkeys_on_this_screen,
                                    style = MaterialTheme.typography.bodyMediumBold
                                )
                                Spacer(Modifier.height(8.dp))
                                screenHotkeysDescription.forEachIndexed { index, data ->
                                    HotkeyRow(data)
                                    if (index < screenHotkeysDescription.lastIndex) {
                                        Spacer(Modifier.height(8.dp))
                                    }
                                }
                                Spacer(Modifier.height(24.dp))
                            }

                            Text(
                                textRes = Res.string.hotkeys_common,
                                style = MaterialTheme.typography.bodyMediumBold
                            )
                            Spacer(Modifier.height(8.dp))
                            commonHotkeysDescription.forEachIndexed { index, data ->
                                HotkeyRow(data)
                                if (index < commonHotkeysDescription.lastIndex) {
                                    Spacer(Modifier.height(8.dp))
                                }
                            }
                            Spacer(Modifier.height(72.dp))
                        }
                        VerticalScrollbar(
                            modifier = Modifier
                                .height(listHeightDp)
                                .align(Alignment.CenterEnd),
                            adapter = rememberScrollbarAdapter(scrollState)
                        )

                        OutlinedRoundedButton(
                            modifier = Modifier
                                .padding(16.dp)
                                .cardBackground()
                                .align(Alignment.BottomEnd),
                            onClick = {
                                showDialog.value = false
                            }
                        ) {
                            Text(Res.string.close)
                        }
                    }
                }
            }
        }
    }
}