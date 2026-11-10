package ru.rznnike.demokmp.app.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import ru.rznnike.demokmp.BuildKonfig
import ru.rznnike.demokmp.app.ui.theme.LocalCustomColorScheme
import java.util.regex.Pattern

private const val RELEASE_BUILD_TYPE = "release"
private const val HIGHLIGHT_PATTERN_FLAGS =
    Pattern.CASE_INSENSITIVE or Pattern.UNICODE_CASE or Pattern.MULTILINE or Pattern.LITERAL

internal fun formatAppVersion(
    versionName: String,
    versionCode: Int,
    buildType: String
) = buildString {
    append("v ")
    append(versionName)
    append(".$versionCode")
    if (buildType != RELEASE_BUILD_TYPE) append(" $buildType")
}

fun getFormattedAppVersion() = formatAppVersion(
    versionName = BuildKonfig.VERSION_NAME,
    versionCode = BuildKonfig.VERSION_CODE,
    buildType = BuildKonfig.BUILD_TYPE
)

internal fun findSubstringRanges(
    text: String,
    substring: String,
): List<IntRange> {
    if (substring.isEmpty()) return emptyList()

    val matcher = substring.toPattern(HIGHLIGHT_PATTERN_FLAGS).matcher(text)
    return buildList {
        while (matcher.find()) add(matcher.start() .. matcher.end())
    }
}

@Composable
fun String.highlightSubstrings(substring: String) = buildAnnotatedString {
    append(this@highlightSubstrings)
    findSubstringRanges(
        text = this@highlightSubstrings,
        substring = substring
    ).forEach { range ->
        addStyle(
            style = SpanStyle(
                background = LocalCustomColorScheme.current.searchSelection
            ),
            start = range.first,
            end = range.last
        )
    }
}
