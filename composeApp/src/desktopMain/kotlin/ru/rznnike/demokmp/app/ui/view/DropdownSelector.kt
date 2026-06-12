package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import ru.rznnike.demokmp.app.ui.theme.LocalCustomColorScheme
import ru.rznnike.demokmp.app.utils.addIf
import ru.rznnike.demokmp.app.utils.onClick
import ru.rznnike.demokmp.app.utils.onEnterKey
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.ic_expand

@Composable
fun <ItemType> DropdownSelector(
    modifier: Modifier = Modifier,
    height: Dp? = 48.dp,
    label: String,
    alignment: DropdownSelectorAlignment = DropdownSelectorAlignment.BOTTOM,
    items: List<ItemType>,
    selectedItem: ItemType,
    itemNameRetriever: @Composable (ItemType?) -> String,
    onItemSelected: (item: ItemType) -> Unit,
    enabled: Boolean = true
) {
    val isExpanded = remember { mutableStateOf(false) }

    fun expand() {
        if (items.isNotEmpty()) {
            isExpanded.value = true
        }
    }

    Box(
        modifier = modifier.addIf(height != null) {
            height(height!!)
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
                .onEnterKey { if (enabled) expand() },
            contentPadding = PaddingValues(start = 16.dp, top = 8.dp, bottom = 8.dp),
            value = itemNameRetriever(selectedItem),
            onValueChange = { },
            singleLine = true,
            label = {
                Text(label)
            },
            readOnly = true,
            enabled = enabled,
            trailingIcon = {
                Icon(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(24.dp)
                        .rotate(if (isExpanded.value) 180f else 0f),
                    painter = painterResource(Res.drawable.ic_expand),
                    tint = LocalCustomColorScheme.current.outlineComponentContent,
                    contentDescription = null
                )
            }
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .onClick { if (enabled) expand() }
        )

        BoxWithConstraints(
            modifier = Modifier.fillMaxWidth()
        ) {
            PopupList(
                modifier = Modifier.width(maxWidth),
                showPopup = isExpanded,
                alignment = if (alignment == DropdownSelectorAlignment.BOTTOM) Alignment.TopStart else Alignment.BottomStart,
                verticalOffset = if (alignment == DropdownSelectorAlignment.BOTTOM) 56.dp else 0.dp,
                items = items,
                preselectedItem = selectedItem,
                itemNameRetriever = itemNameRetriever,
                onItemSelected = onItemSelected
            )
        }
    }

    LaunchedEffect(enabled) {
        if (!enabled) {
            isExpanded.value = false
        }
    }
}

enum class DropdownSelectorAlignment {
    TOP,
    BOTTOM
}