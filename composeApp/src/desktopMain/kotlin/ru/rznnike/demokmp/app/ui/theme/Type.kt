package ru.rznnike.demokmp.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import demokmp.composeapp.generated.resources.*
import demokmp.composeapp.generated.resources.Res
import demokmp.composeapp.generated.resources.ubuntu_bold
import demokmp.composeapp.generated.resources.ubuntu_bold_italic
import demokmp.composeapp.generated.resources.ubuntu_italic
import org.jetbrains.compose.resources.Font

@Composable
private fun getFontFamily() = FontFamily(
    Font(Res.font.ubuntu_bold, FontWeight.Bold, FontStyle.Normal),
    Font(Res.font.ubuntu_bold_italic, FontWeight.Bold, FontStyle.Italic),
    Font(Res.font.ubuntu_italic, FontWeight.Normal, FontStyle.Italic),
    Font(Res.font.ubuntu_light, FontWeight.Light, FontStyle.Normal),
    Font(Res.font.ubuntu_light_italic, FontWeight.Light, FontStyle.Italic),
    Font(Res.font.ubuntu_medium, FontWeight.Medium, FontStyle.Normal),
    Font(Res.font.ubuntu_medium_italic, FontWeight.Medium, FontStyle.Italic),
    Font(Res.font.ubuntu_regular, FontWeight.Normal, FontStyle.Normal)
)

// Default Material 3 typography values
private val baseline = Typography()

@Composable
fun getAppTypography(): Typography {
    val fontFamily = getFontFamily()
    return Typography(
        displayLarge = baseline.displayLarge.copy(fontFamily = fontFamily),
        displayMedium = baseline.displayMedium.copy(fontFamily = fontFamily),
        displaySmall = baseline.displaySmall.copy(fontFamily = fontFamily),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = fontFamily),
        headlineMedium = baseline.headlineMedium.copy(fontFamily = fontFamily),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = fontFamily),
        titleLarge = baseline.titleLarge.copy(fontFamily = fontFamily),
        titleMedium = baseline.titleMedium.copy(fontFamily = fontFamily),
        titleSmall = baseline.titleSmall.copy(fontFamily = fontFamily),
        bodyLarge = baseline.bodyLarge.copy(fontFamily = fontFamily),
        bodyMedium = baseline.bodyMedium.copy(fontFamily = fontFamily),
        bodySmall = baseline.bodySmall.copy(fontFamily = fontFamily),
        labelLarge = baseline.labelLarge.copy(fontFamily = fontFamily),
        labelMedium = baseline.labelMedium.copy(fontFamily = fontFamily),
        labelSmall = baseline.labelSmall.copy(fontFamily = fontFamily),
    )
}

