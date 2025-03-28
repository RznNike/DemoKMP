package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.input.key.*
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import org.jetbrains.compose.resources.painterResource
import ru.rznnike.demokmp.app.utils.onEnterKey
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.ic_expand

private val MAX_HEIGHT_DP = 500.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <ItemType> DropdownSelector(
    modifier: Modifier = Modifier,
    height: Dp? = 48.dp,
    label: String,
    items: List<ItemType>,
    selectedItem: ItemType,
    itemNameRetriever: @Composable (ItemType?) -> String,
    onItemSelected: (item: ItemType) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val canExpand = items.isNotEmpty()
    val interactionSource = remember { MutableInteractionSource() }

    fun expand() {
        if (canExpand) {
            isExpanded = true
        }
    }

    fun collapse() {
        isExpanded = false
    }

    Box(
        modifier = modifier
            .run {
                height?.let { height(height) } ?: this
            }
    ) {
        fun selectItem(offset: Int) {
            val currentIndex = items.indexOf(selectedItem)
            var newIndex = currentIndex + offset
            when {
                newIndex < 0 -> newIndex = items.lastIndex
                newIndex > items.lastIndex -> newIndex = 0
            }
            onItemSelected(items[newIndex])
        }

        SlimOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onPreviewKeyEvent { keyEvent ->
                    if (keyEvent.type == KeyEventType.KeyDown) {
                        when (keyEvent.key) {
                            Key.DirectionUp -> {
                                selectItem(-1)
                                true
                            }
                            Key.DirectionDown -> {
                                selectItem(1)
                                true
                            }
                            else -> false
                        }
                    } else false
                }
                .onEnterKey {
                    expand()
                }
                .clickable {
                    expand()
                },
            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, bottom = 8.dp),
            value = itemNameRetriever(selectedItem),
            onValueChange = { },
            singleLine = true,
            label = {
                Text(label)
            },
            readOnly = true,
            trailingIcon = {
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(if (isExpanded) 180f else 0f),
                    painter = painterResource(Res.drawable.ic_expand),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    contentDescription = null
                )
            },
            interactionSource = interactionSource
        )

        LaunchedEffect(canExpand) {
            interactionSource.interactions.collect { interaction ->
                if (interaction is PressInteraction.Release) {
                    expand()
                }
            }
        }

        if (isExpanded) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .offset(y = 8.dp)
            ) {
                Popup(
                    properties = PopupProperties(
                        focusable = true
                    ),
                    onDismissRequest = { collapse() },
                    alignment = Alignment.TopStart
                ) {
                    val focusManager = LocalFocusManager.current

                    val maxHeight = with(LocalDensity.current) {
                        val windowHeight = LocalWindowInfo.current.containerSize.height.toDp()
                        min(windowHeight - 300.dp, MAX_HEIGHT_DP)
                    }
                    Surface(
                        modifier = Modifier
                            .padding(end = 16.dp, bottom = 16.dp)
                            .width(maxWidth)
                            .requiredHeightIn(
                                max = maxHeight
                            ),
                        shadowElevation = 4.dp,
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Box(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            val scrollState = rememberLazyListState()
                            var listHeight by remember { mutableStateOf(0) }
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
                                    key = { item -> item.hashCode().toString() },
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
                                            collapse()
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
    }
}