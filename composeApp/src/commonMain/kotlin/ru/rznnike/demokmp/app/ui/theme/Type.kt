package ru.rznnike.demokmp.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import org.jetbrains.compose.resources.Font
import ru.rznnike.demokmp.generated.resources.*

@Composable
private fun getDefaultFontFamily() = FontFamily(
    Font(resource = Res.font.ubuntu_bold, weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(resource = Res.font.ubuntu_bold_italic, weight = FontWeight.Bold, style = FontStyle.Italic),
    Font(resource = Res.font.ubuntu_italic, weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(resource = Res.font.ubuntu_light, weight = FontWeight.Light, style = FontStyle.Normal),
    Font(resource = Res.font.ubuntu_light_italic, weight = FontWeight.Light, style = FontStyle.Italic),
    Font(resource = Res.font.ubuntu_medium, weight = FontWeight.Medium, style = FontStyle.Normal),
    Font(resource = Res.font.ubuntu_medium_italic, weight = FontWeight.Medium, style = FontStyle.Italic),
    Font(resource = Res.font.ubuntu_regular, weight = FontWeight.Normal, style = FontStyle.Normal)
)

@Composable
private fun getMonospaceFontFamily() = FontFamily(
    Font(resource = Res.font.consolas, weight = FontWeight.Normal, style = FontStyle.Normal)
)

// Default Material 3 typography values
private val baseline = Typography()

@Composable
fun getAppTypography(): Typography {
    val defaultFontFamily = getDefaultFontFamily()
    return Typography(
        displayLarge = baseline.displayLarge.copy(fontFamily = defaultFontFamily), // 57 dp
        displayMedium = baseline.displayMedium.copy(fontFamily = defaultFontFamily), // 45 dp
        displaySmall = baseline.displaySmall.copy(fontFamily = defaultFontFamily), // 36 dp
        headlineLarge = baseline.headlineLarge.copy(fontFamily = defaultFontFamily), // 32 dp
        headlineMedium = baseline.headlineMedium.copy(fontFamily = defaultFontFamily), // 28 dp
        headlineSmall = baseline.headlineSmall.copy(fontFamily = defaultFontFamily), // 24 dp
        titleLarge = baseline.titleLarge.copy(fontFamily = defaultFontFamily), // 22 dp
        titleMedium = baseline.titleMedium.copy(fontFamily = defaultFontFamily), // 16 dp
        titleSmall = baseline.titleSmall.copy(fontFamily = defaultFontFamily), // 14 dp
        bodyLarge = baseline.bodyLarge.copy(fontFamily = defaultFontFamily), // 16 dp, default for Text, TextField, Button
        bodyMedium = baseline.bodyMedium.copy(fontFamily = defaultFontFamily), // 14 dp
        bodySmall = baseline.bodySmall.copy(fontFamily = defaultFontFamily), // 12 dp
        labelLarge = baseline.labelLarge.copy(fontFamily = defaultFontFamily), // 14 dp
        labelMedium = baseline.labelMedium.copy(fontFamily = defaultFontFamily), // 12 dp
        labelSmall = baseline.labelSmall.copy(fontFamily = defaultFontFamily), // 11 dp
    )
}

val Typography.bodyLargeItalic: TextStyle
    get() = bodyLarge.copy(
        fontStyle = FontStyle.Italic
    )

val Typography.bodyLargeBold: TextStyle
    get() = bodyLarge.copy(
        fontWeight = FontWeight.Bold
    )

val Typography.bodyMediumBold: TextStyle
    get() = bodyMedium.copy(
        fontWeight = FontWeight.Bold
    )

val Typography.bodySmallMono: TextStyle
    @Composable
    get() = bodySmall.copy(
        fontFamily = getMonospaceFontFamily()
    )

val Typography.bodyMediumMono: TextStyle
    @Composable
    get() = bodyMedium.copy(
        fontFamily = getMonospaceFontFamily()
    )
