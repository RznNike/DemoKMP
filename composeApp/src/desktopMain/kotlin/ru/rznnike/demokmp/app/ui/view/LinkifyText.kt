package ru.rznnike.demokmp.app.ui.view

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import ru.rznnike.demokmp.app.ui.theme.LocalCustomColorScheme
import java.util.regex.Pattern

private const val URL_ANNOTATION_TAG = "URL"

@Composable
fun LinkifyText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    style: TextStyle = LocalTextStyle.current
) {
    val uriHandler = LocalUriHandler.current
    val layoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }
    val linkifiedText = text.linkify()
    Text(
        text = linkifiedText,
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures { offsetPosition ->
                    layoutResult.value?.let {
                        val position = it.getOffsetForPosition(offsetPosition)
                        linkifiedText.getStringAnnotations(position, position)
                            .firstOrNull { annotation ->
                                annotation.tag == URL_ANNOTATION_TAG
                            }
                            ?.let { annotation ->
                                uriHandler.openUri(annotation.item)
                            }
                    }
                }
            },
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = { layoutResult.value = it },
        style = style
    )
}

@Composable
private fun AnnotatedString.linkify(): AnnotatedString {
    val links = extractUrls(this)
    return buildAnnotatedString {
        append(this@linkify)
        links.forEach {
            addStyle(
                style = SpanStyle(
                    color = LocalCustomColorScheme.current.textLink,
                    textDecoration = TextDecoration.Underline
                ),
                start = it.start,
                end = it.end
            )
            addStringAnnotation(
                tag = URL_ANNOTATION_TAG,
                annotation = it.url,
                start = it.start,
                end = it.end
            )
        }
    }
}

private val urlPattern: Pattern = Pattern.compile(
    "(?:^|\\W)(((ht|f)tp|ws)(s?)://|www\\.)"
            + "(([\\w\\-]+\\.)+([\\w\\-.~]+/?)*"
            + "[\\p{Alnum}.,%_=?&#\\-+()\\[\\]*$~@!:/{};']*)",
    Pattern.CASE_INSENSITIVE or Pattern.MULTILINE or Pattern.DOTALL
)

private fun extractUrls(text: AnnotatedString): List<LinkInfo> {
    val matcher = urlPattern.matcher(text)
    var matchStart: Int
    var matchEnd: Int
    val links = arrayListOf<LinkInfo>()

    while (matcher.find()) {
        matchStart = matcher.start(1)
        matchEnd = matcher.end()

        val url = text.substring(matchStart, matchEnd)
        links.add(
            LinkInfo(
                url = url,
                start = matchStart,
                end = matchEnd
            )
        )
    }
    return links
}

private data class LinkInfo(
    val url: String,
    val start: Int,
    val end: Int
)