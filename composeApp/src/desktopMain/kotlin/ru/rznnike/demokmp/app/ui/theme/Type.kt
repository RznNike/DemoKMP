package ru.rznnike.demokmp.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font

private val fontFamily = FontFamily(
    Font(resource = "font/ubuntu_bold.ttf", weight = FontWeight.Bold, style = FontStyle.Normal),
    Font(resource = "font/ubuntu_bold_italic.ttf", weight = FontWeight.Bold, style = FontStyle.Italic),
    Font(resource = "font/ubuntu_italic.ttf", weight = FontWeight.Normal, style = FontStyle.Italic),
    Font(resource = "font/ubuntu_light.ttf", weight = FontWeight.Light, style = FontStyle.Normal),
    Font(resource = "font/ubuntu_light_italic.ttf", weight = FontWeight.Light, style = FontStyle.Italic),
    Font(resource = "font/ubuntu_medium.ttf", weight = FontWeight.Medium, style = FontStyle.Normal),
    Font(resource = "font/ubuntu_medium_italic.ttf", weight = FontWeight.Medium, style = FontStyle.Italic),
    Font(resource = "font/ubuntu_regular.ttf", weight = FontWeight.Normal, style = FontStyle.Normal)
)

// Default Material 3 typography values
private val baseline = Typography()

val appTypography = Typography(
    displayLarge = baseline.displayLarge.copy(fontFamily = fontFamily), // 57 dp
    displayMedium = baseline.displayMedium.copy(fontFamily = fontFamily), // 45 dp
    displaySmall = baseline.displaySmall.copy(fontFamily = fontFamily), // 36 dp
    headlineLarge = baseline.headlineLarge.copy(fontFamily = fontFamily), // 32 dp
    headlineMedium = baseline.headlineMedium.copy(fontFamily = fontFamily), // 28 dp
    headlineSmall = baseline.headlineSmall.copy(fontFamily = fontFamily), // 24 dp
    titleLarge = baseline.titleLarge.copy(fontFamily = fontFamily), // 22 dp
    titleMedium = baseline.titleMedium.copy(fontFamily = fontFamily), // 16 dp
    titleSmall = baseline.titleSmall.copy(fontFamily = fontFamily), // 14 dp
    bodyLarge = baseline.bodyLarge.copy(fontFamily = fontFamily), // 16 dp, default for Text, TextField, Button
    bodyMedium = baseline.bodyMedium.copy(fontFamily = fontFamily), // 14 dp
    bodySmall = baseline.bodySmall.copy(fontFamily = fontFamily), // 12 dp
    labelLarge = baseline.labelLarge.copy(fontFamily = fontFamily), // 14 dp
    labelMedium = baseline.labelMedium.copy(fontFamily = fontFamily), // 12 dp
    labelSmall = baseline.labelSmall.copy(fontFamily = fontFamily), // 11 dp
)
