package ru.rznnike.demokmp.app.ui.item

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
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
        color = MaterialTheme.colorScheme.surfaceContainer
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
            Button(
                modifier = Modifier
                    .defaultMinSize(minWidth = 1.dp, minHeight = 1.dp),
                contentPadding = PaddingValues(8.dp),
                onClick = onDeleteClick
            ) {
                Image(
                    modifier = Modifier
                        .size(24.dp),
                    painter = painterResource(Res.drawable.ic_delete),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary),
                    contentDescription = null
                )
            }
        }
    }
}

@Preview
@Composable
private fun DBExampleDataItemPreview() {
    DBExampleDataItem(
        data = DBExampleData(
            id = 42,
            name = "Preview name"
        ),
        onDeleteClick = {}
    )
}