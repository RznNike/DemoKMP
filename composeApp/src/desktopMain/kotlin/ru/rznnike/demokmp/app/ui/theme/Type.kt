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
    displayLarge = baseline.displayLarge.copy(fontFamily = fontFamily),
    displayMedium = baseline.displayMedium.copy(fontFamily = fontFamily),
    displaySmall = baseline.displaySmall.copy(fontFamily = fontFamily),
    headlineLarge = baseline.headlineLarge.copy(fontFamily = fontFamily),
    headlineMedium = baseline.headlineMedium.copy(fontFamily = fontFamily),
    headlineSmall = baseline.headlineSmall.copy(fontFamily = fontFamily),
    titleLarge = baseline.titleLarge.copy(fontFamily = fontFamily),
    titleMedium = baseline.titleMedium.copy(fontFamily = fontFamily),
    titleSmall = baseline.titleSmall.copy(fontFamily = fontFamily),
    bodyLarge = baseline.bodyLarge.copy(fontFamily = fontFamily), // default for Text, TextField, Button
    bodyMedium = baseline.bodyMedium.copy(fontFamily = fontFamily),
    bodySmall = baseline.bodySmall.copy(fontFamily = fontFamily),
    labelLarge = baseline.labelLarge.copy(fontFamily = fontFamily),
    labelMedium = baseline.labelMedium.copy(fontFamily = fontFamily),
    labelSmall = baseline.labelSmall.copy(fontFamily = fontFamily),
)
