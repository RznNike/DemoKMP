package ru.rznnike.demokmp.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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
        displayLarge = baseline.displayLarge.copy(fontFamily = defaultFontFamily), // 57 sp
        displayMedium = baseline.displayMedium.copy(fontFamily = defaultFontFamily), // 45 sp
        displaySmall = baseline.displaySmall.copy(
            fontFamily = defaultFontFamily,
            fontSize = 36.sp,
            lineHeight = 36.sp,
            letterSpacing = 0.sp
        ), // 36 sp
        headlineLarge = baseline.headlineLarge.copy(fontFamily = defaultFontFamily), // 32 sp
        headlineMedium = baseline.headlineMedium.copy(fontFamily = defaultFontFamily), // 28 sp
        headlineSmall = baseline.headlineSmall.copy(fontFamily = defaultFontFamily), // 24 sp
        titleLarge = baseline.titleLarge.copy(fontFamily = defaultFontFamily), // 22 sp
        titleMedium = baseline.titleMedium.copy(fontFamily = defaultFontFamily), // 16 sp
        titleSmall = baseline.titleSmall.copy(fontFamily = defaultFontFamily), // 14 sp
        bodyLarge = baseline.bodyLarge.copy(
            fontFamily = defaultFontFamily,
            fontSize = 16.sp,
            lineHeight = 16.sp,
            letterSpacing = 0.sp
        ), // 16 sp, default for Text, TextField
        bodyMedium = baseline.bodyMedium.copy(
            fontFamily = defaultFontFamily,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.sp
        ), // 14 sp
        bodySmall = baseline.bodySmall.copy(
            fontFamily = defaultFontFamily,
            fontSize = 12.sp,
            lineHeight = 12.sp,
            letterSpacing = 0.sp
        ), // 12 sp
        labelLarge = baseline.labelLarge.copy(
            fontFamily = defaultFontFamily,
            fontSize = 14.sp,
            lineHeight = 14.sp,
            letterSpacing = 0.sp
        ), // 14 sp, default for Button
        labelMedium = baseline.labelMedium.copy(fontFamily = defaultFontFamily), // 12 sp
        labelSmall = baseline.labelSmall.copy(fontFamily = defaultFontFamily), // 11 sp
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
