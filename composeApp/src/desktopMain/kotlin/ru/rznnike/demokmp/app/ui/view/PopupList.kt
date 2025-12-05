package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties

private val MAX_HEIGHT_DP = 500.dp

@Composable
fun <ItemType> PopupList(
    modifier: Modifier,
    showPopup: MutableState<Boolean>,
    alignment: Alignment = Alignment.TopStart,
    verticalOffset: Dp = 0.dp,
    items: List<ItemType>,
    itemNameRetriever: @Composable (ItemType?) -> String,
    onItemSelected: (item: ItemType) -> Unit
) {
    if (showPopup.value) {
        val verticalOffsetPx = with(LocalDensity.current) {
            verticalOffset.roundToPx()
        }
        Popup(
            properties = PopupProperties(
                focusable = true
            ),
            onDismissRequest = { showPopup.value = false },
            alignment = alignment,
            offset = IntOffset(x = 0, y = verticalOffsetPx)
        ) {
            val focusManager = LocalFocusManager.current

            val maxHeight = with(LocalDensity.current) {
                val windowHeight = LocalWindowInfo.current.containerSize.height.toDp()
                min(windowHeight - 300.dp, MAX_HEIGHT_DP)
            }

            Surface(
                modifier = modifier,
                shadowElevation = 4.dp,
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .requiredHeightIn(
                            max = maxHeight
                        )
                        .padding(1.dp)
                ) {
                    val scrollState = rememberLazyListState()
                    var listHeight by remember { mutableIntStateOf(0) }
                    val listHeightDp = with(LocalDensity.current) {
                        listHeight.toDp()
                    }

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .onGloballyPositioned {
                                listHeight = it.size.height
                            },
                        state = scrollState
                    ) {
                        items(
                            items = items,
                            key = { item -> item.hashCode() },
                        ) { item ->
                            DropdownSelectorItem(
                                modifier = Modifier
                                    .onPreviewKeyEvent { keyEvent ->
                                        if (keyEvent.type == KeyEventType.KeyDown) {
                                            when (keyEvent.key) {
                                                Key.DirectionUp -> {
                                                    focusManager.moveFocus(FocusDirection.Previous)
                                                    true
                                                }
                                                Key.DirectionDown -> {
                                                    focusManager.moveFocus(FocusDirection.Next)
                                                    true
                                                }
                                                else -> false
                                            }
                                        } else false
                                    },
                                text = itemNameRetriever(item),
                                onClick = {
                                    onItemSelected(item)
                                    showPopup.value = false
                                }
                            )
                        }
                    }
                    VerticalScrollbar(
                        modifier = Modifier
                            .height(listHeightDp)
                            .align(Alignment.CenterEnd),
                        adapter = rememberScrollbarAdapter(scrollState)
                    )
                }
            }
        }
    }
}