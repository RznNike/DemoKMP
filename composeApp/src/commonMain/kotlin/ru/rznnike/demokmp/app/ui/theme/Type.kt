package ru.rznnike.demokmp.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

private val defaultFontFamily = FontFamily(
    Font(resource = "font/ubuntu_bold.ttf", weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(resource = "font/ubuntu_bold_italic.ttf", weight = FontWeight.Bold, style = FontStyle.Italic),
    Font(resource = "font/ubuntu_italic.ttf", weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(resource = "font/ubuntu_light.ttf", weight = FontWeight.Light, style = FontStyle.Normal),
    Font(resource = "font/ubuntu_light_italic.ttf", weight = FontWeight.Light, style = FontStyle.Italic),
    Font(resource = "font/ubuntu_medium.ttf", weight = FontWeight.Medium, style = FontStyle.Normal),
    Font(resource = "font/ubuntu_medium_italic.ttf", weight = FontWeight.Medium, style = FontStyle.Italic),
    Font(resource = "font/ubuntu_regular.ttf", weight = FontWeight.Normal, style = FontStyle.Normal)
)

private val monospaceFontFamily = FontFamily(
    Font(resource = "font/consolas.ttf", weight = FontWeight.Normal, style = FontStyle.Normal)
)

// Default Material 3 typography values
private val baseline = Typography()

val appTypography = Typography(
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
    get() = bodySmall.copy(
        fontFamily = monospaceFontFamily
    )

val Typography.bodyMediumMono: TextStyle
    get() = bodyMedium.copy(
        fontFamily = monospaceFontFamily
    )
