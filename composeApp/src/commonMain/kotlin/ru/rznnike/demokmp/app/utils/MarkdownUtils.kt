package ru.rznnike.demokmp.app.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.UriHandler
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.mikepenz.markdown.m3.markdownTypography
import ru.rznnike.demokmp.app.ui.theme.headlineMediumSemiBold
import ru.rznnike.demokmp.app.ui.theme.headlineSmallSemiBold
import ru.rznnike.demokmp.app.ui.theme.titleLargeSemiBold
import ru.rznnike.demokmp.app.ui.theme.titleMediumSemiBold
import ru.rznnike.demokmp.app.ui.theme.titleSmallSemiBold

@Composable
fun defaultMarkdownTypography() = markdownTypography(
    h1 = MaterialTheme.typography.headlineMediumSemiBold,
    h2 = MaterialTheme.typography.headlineSmallSemiBold,
    h3 = MaterialTheme.typography.titleLargeSemiBold,
    h4 = MaterialTheme.typography.titleMediumSemiBold,
    h5 = MaterialTheme.typography.titleSmallSemiBold,
    h6 = MaterialTheme.typography.titleSmallSemiBold,
    textLink = TextLinkStyles(
        style = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        ).toSpanStyle()
    )
)

@Composable
fun markdownTypographyForStyle(style: TextStyle) = markdownTypography(
    h1 = style,
    h2 = style,
    h3 = style,
    h4 = style,
    h5 = style,
    h6 = style,
    text = style,
    quote = style.copy(fontStyle = FontStyle.Italic),
    paragraph = style,
    ordered = style,
    bullet = style,
    list = style,
    textLink = TextLinkStyles(
        style = style.copy(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        ).toSpanStyle()
    )
)

@Composable
fun CustomLinksHandler(
    onClick: (String) -> Unit,
    content: @Composable (() -> Unit)
) = CompositionLocalProvider(
    value = LocalUriHandler provides object : UriHandler {
        override fun openUri(uri: String) { onClick(uri) }
    },
    content = content
)