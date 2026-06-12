package ru.rznnike.demokmp.app.ui.screen.markdown

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.mikepenz.markdown.m3.Markdown
import kotlinx.serialization.Serializable
import org.jetbrains.compose.resources.stringResource
import ru.rznnike.demokmp.app.navigation.AndroidNavigationScreen
import ru.rznnike.demokmp.app.navigation.getNavigator
import ru.rznnike.demokmp.app.ui.view.Toolbar
import ru.rznnike.demokmp.app.ui.view.ToolbarButton
import ru.rznnike.demokmp.app.utils.cardBackground
import ru.rznnike.demokmp.app.utils.defaultMarkdownTypography
import ru.rznnike.demokmp.app.utils.rememberResTextFile
import ru.rznnike.demokmp.app.utils.statusBarsAndCutoutPadding
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.ic_back
import ru.rznnike.demokmp.generated.resources.markdown_example

@Serializable
class MarkdownExampleScreen : AndroidNavigationScreen() {
    @Composable
    override fun Layout() {
        val navigator = getNavigator()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .statusBarsAndCutoutPadding()
                .navigationBarsPadding()
        ) {
            Toolbar(
                modifier = Modifier.fillMaxWidth(),
                title = stringResource(Res.string.markdown_example),
                leftButton = ToolbarButton(Res.drawable.ic_back) {
                    navigator.closeScreen()
                }
            )
            Spacer(Modifier.height(16.dp))

            Markdown(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .cardBackground()
                    .clip(MaterialTheme.shapes.medium)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                content = rememberResTextFile("files/markdown_example.md"),
                typography = defaultMarkdownTypography()
            )
        }
    }
}