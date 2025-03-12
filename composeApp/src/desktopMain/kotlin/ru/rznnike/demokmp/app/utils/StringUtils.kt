package ru.rznnike.demokmp.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import ru.rznnike.demokmp.app.ui.theme.LocalCustomColorScheme
import java.util.regex.Pattern

@Composable
fun String.highlightSubstrings(substring: String): AnnotatedString {
    return buildAnnotatedString {
        val matcher = substring
            .toPattern(Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.LITERAL)
            .matcher(this@highlightSubstrings)
        append(this@highlightSubstrings)
        if (substring.isNotEmpty()) {
            while (matcher.find()) {
                addStyle(
                    style = SpanStyle(
                        background = LocalCustomColorScheme.current.searchSelection
                    ),
                    start = matcher.start(),
                    end = matcher.end()
                )
            }
        }
    }
}
