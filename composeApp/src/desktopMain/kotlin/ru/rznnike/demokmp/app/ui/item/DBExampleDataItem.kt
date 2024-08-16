package ru.rznnike.demokmp.app.ui.item

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import demokmp.composeapp.generated.resources.Res
import demokmp.composeapp.generated.resources.delete
import ru.rznnike.demokmp.app.utils.TextR
import ru.rznnike.demokmp.domain.model.dbexample.DBExampleData

@Composable
fun DBExampleDataItem(
    data: DBExampleData,
    onDeleteClick: () -> Unit
) {
    Row {
        Text(
            text = data.id.toString(),
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier
                .width(50.dp)
                .align(Alignment.CenterVertically)
        )
        Text(
            text = data.name,
            style = TextStyle(fontSize = 20.sp),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .align(Alignment.CenterVertically)
        )
        Button(
            modifier = Modifier.padding(start = 10.dp),
            onClick = onDeleteClick
        ) {
            TextR(Res.string.delete)
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