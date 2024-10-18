package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.onClick
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
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
import kotlinx.coroutines.launch
import ru.rznnike.demokmp.app.ui.theme.bodyMediumBold
import ru.rznnike.demokmp.app.utils.onEnterKey
import ru.rznnike.demokmp.domain.utils.smartFilter
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.nothing_found
import ru.rznnike.demokmp.generated.resources.search

private val MAX_HEIGHT_DP = 500.dp

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun <ItemType> DropdownQuerySelector(
    modifier: Modifier = Modifier,
    height: Dp? = 48.dp,
    label: String,
    items: List<ItemType>?,
    selectedItem: ItemType?,
    itemNameRetriever: @Composable (ItemType?) -> String,
    onItemSelected: (item: ItemType) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val canExpand = !items.isNullOrEmpty()
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
        SlimOutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .onEnterKey {
                    expand()
                }
                .onClick {
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
                items?.let {
                    Text(
                        modifier = Modifier
                            .size(24.dp)
                            .wrapContentSize(),
                        text = items.size.coerceAtMost(999).toString(),
                        color = if (items.isEmpty()) {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        style = MaterialTheme.typography.bodyMedium
                    )
                } ?: run {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(3.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
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
                    val searchFieldFocusRequester = remember { FocusRequester() }
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
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            var query by remember { mutableStateOf("") }
                            val scrollState = rememberLazyListState()
                            val coroutineScope = rememberCoroutineScope()

                            val filteredItems = items
                                ?.map { item -> item to itemNameRetriever(item) }
                                ?.smartFilter(
                                    query = query,
                                    stringRetrievers = listOf { it.second }
                                )
                                ?.map { it.first }
                                ?: emptyList()

                            SlimOutlinedTextField(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                                    .height(48.dp)
                                    .onPreviewKeyEvent { keyEvent ->
                                        if (keyEvent.type == KeyEventType.KeyDown) {
                                            when {
                                                (keyEvent.isShiftPressed && (keyEvent.key == Key.Tab)) || (keyEvent.key == Key.DirectionUp) -> {
                                                    coroutineScope.launch {
                                                        scrollState.scrollToItem(filteredItems.lastIndex.coerceAtLeast(0))
                                                        focusManager.moveFocus(FocusDirection.Previous)
                                                    }
                                                    true
                                                }
                                                (keyEvent.key == Key.Tab) || (keyEvent.key == Key.DirectionDown) -> {
                                                    coroutineScope.launch {
                                                        scrollState.scrollToItem(0)
                                                        focusManager.moveFocus(FocusDirection.Next)
                                                    }
                                                    true
                                                }
                                                else -> false
                                            }
                                        } else false
                                    }
                                    .onEnterKey {
                                        focusManager.moveFocus(FocusDirection.Next)
                                    }
                                    .focusRequester(searchFieldFocusRequester),
                                value = query,
                                onValueChange = {
                                    query = it
                                },
                                singleLine = true,
                                label = {
                                    TextR(Res.string.search)
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                            Box {
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
                                    state = scrollState,
                                    contentPadding = PaddingValues(bottom = 16.dp)
                                ) {
                                    items(
                                        items = filteredItems,
                                        key = { item -> item.hashCode().toString() },
                                    ) { item ->
                                        DropdownSelectorItem(
                                            modifier = Modifier
                                                .padding(horizontal = 16.dp)
                                                .onPreviewKeyEvent { keyEvent ->
                                                    if (keyEvent.type == KeyEventType.KeyDown) {
                                                        when {
                                                            keyEvent.isCtrlPressed && (keyEvent.key == Key.F) -> {
                                                                searchFieldFocusRequester.requestFocus()
                                                                true
                                                            }
                                                            keyEvent.key == Key.DirectionUp -> {
                                                                focusManager.moveFocus(FocusDirection.Previous)
                                                                true
                                                            }
                                                            keyEvent.key == Key.DirectionDown -> {
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

                                if (filteredItems.isEmpty()) {
                                    TextR(
                                        modifier = Modifier
                                            .padding(top = 8.dp, bottom = 24.dp)
                                            .align(Alignment.Center),
                                        textRes = Res.string.nothing_found,
                                        style = MaterialTheme.typography.bodyMediumBold
                                    )
                                    Spacer(Modifier.height(16.dp))
                                }
                            }
                        }
                    }

                    LaunchedEffect(searchFieldFocusRequester) {
                        searchFieldFocusRequester.requestFocus()
                    }
                }
            }
        }
    }
}
