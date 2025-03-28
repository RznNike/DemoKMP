package ru.rznnike.demokmp.app.ui.item

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.unit.dp
import ru.rznnike.demokmp.app.ui.view.SelectableOutlinedIconButton
import ru.rznnike.demokmp.domain.model.dbexample.DBExampleData
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.ic_delete

@Composable
fun DBExampleDataItem(
    data: DBExampleData,
    onDeleteClick: () -> Unit
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        ) {
            Text(
                text = data.id.toString(),
                modifier = Modifier
                    .width(50.dp)
                    .align(Alignment.CenterVertically),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = data.name,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                style = MaterialTheme.typography.titleLarge
            )
            Spacer(Modifier.width(16.dp))
            SelectableOutlinedIconButton(
                modifier = Modifier.focusProperties { canFocus = false },
                iconRes = Res.drawable.ic_delete,
                onClick = onDeleteClick
            )
        }
    }
}