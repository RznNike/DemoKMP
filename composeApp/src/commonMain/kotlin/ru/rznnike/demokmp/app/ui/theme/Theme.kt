package ru.rznnike.demokmp.app.ui.theme

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.defaultScrollbarStyle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.DrawableResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.viewmodel.global.configuration.AppConfigurationViewModel
import ru.rznnike.demokmp.domain.model.common.Theme
import ru.rznnike.demokmp.generated.resources.Res
import ru.rznnike.demokmp.generated.resources.ic_compose
import ru.rznnike.demokmp.generated.resources.ic_compose_dark

private val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    outlineVariant = outlineVariantLight,
    scrim = scrimLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
    surfaceDim = surfaceDimLight,
    surfaceBright = surfaceBrightLight,
    surfaceContainerLowest = surfaceContainerLowestLight,
    surfaceContainerLow = surfaceContainerLowLight,
    surfaceContainer = surfaceContainerLight,
    surfaceContainerHigh = surfaceContainerHighLight,
    surfaceContainerHighest = surfaceContainerHighestLight,
)

private val lightContrastScheme = lightScheme.copy(
    background = backgroundLightContrast,
    surfaceVariant = surfaceVariantLightContrast,
    onSurfaceVariant = onSurfaceVariantLightContrast,
    outline = outlineLightContrast,
    outlineVariant = outlineVariantLightContrast
)

private val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    outlineVariant = outlineVariantDark,
    scrim = scrimDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
    surfaceDim = surfaceDimDark,
    surfaceBright = surfaceBrightDark,
    surfaceContainerLowest = surfaceContainerLowestDark,
    surfaceContainerLow = surfaceContainerLowDark,
    surfaceContainer = surfaceContainerDark,
    surfaceContainerHigh = surfaceContainerHighDark,
    surfaceContainerHighest = surfaceContainerHighestDark,
)

private val darkContrastScheme = darkScheme.copy(
    onSurfaceVariant = onSurfaceVariantDarkContrast,
    outline = outlineDarkContrast,
    outlineVariant = outlineVariantDarkContrast
)

@Immutable
data class CustomColorScheme(
    val outlineComponentContent: Color = Color.Unspecified,
    val surfaceContainerA50: Color = Color.Unspecified,
    val textLink: Color = Color.Unspecified,
    val logDebug: Color = Color.Unspecified,
    val logInfo: Color = Color.Unspecified,
    val logWarning: Color = Color.Unspecified,
    val logError: Color = Color.Unspecified,
    val logService: Color = Color.Unspecified,
    val logServiceAccent: Color = Color.Unspecified,
    val logNetworkSent: Color = Color.Unspecified,
    val logNetworkSuccess: Color = Color.Unspecified,
    val logNetworkError: Color = Color.Unspecified,
    val logNetworkCancelled: Color = Color.Unspecified,
    val searchSelection: Color = Color.Unspecified,
    val disabledText: Color = Color.Unspecified,
    val scrollbarAlpha: Float = 0.1f,
    val scrollbarHoverAlpha: Float = 0.5f
)

val lightCustomScheme = CustomColorScheme(
    outlineComponentContent = outlineComponentContentLight,
    surfaceContainerA50 = surfaceContainerA50Light,
    textLink = textLinkLight,
    logDebug = logDebugLight,
    logInfo = logInfoLight,
    logWarning = logWarningLight,
    logError = logErrorLight,
    logService = logServiceLight,
    logServiceAccent = logServiceAccentLight,
    logNetworkSent = logNetworkSentLight,
    logNetworkSuccess = logNetworkSuccessLight,
    logNetworkError = logNetworkErrorLight,
    logNetworkCancelled = logNetworkCancelledLight,
    searchSelection = searchSelectionLight,
    disabledText = disabledTextLight
)

private val lightContrastCustomScheme = lightCustomScheme.copy(
    outlineComponentContent = outlineComponentContentLightContrast,
    disabledText = disabledTextLightContrast,
    scrollbarAlpha = 0.3f,
    scrollbarHoverAlpha = 0.8f
)

val darkCustomScheme = CustomColorScheme(
    outlineComponentContent = outlineComponentContentDark,
    surfaceContainerA50 = surfaceContainerA50Dark,
    textLink = textLinkDark,
    logDebug = logDebugDark,
    logInfo = logInfoDark,
    logWarning = logWarningDark,
    logError = logErrorDark,
    logService = logServiceDark,
    logServiceAccent = logServiceAccentDark,
    logNetworkSent = logNetworkSentDark,
    logNetworkSuccess = logNetworkSuccessDark,
    logNetworkError = logNetworkErrorDark,
    logNetworkCancelled = logNetworkCancelledDark,
    searchSelection = searchSelectionDark,
    disabledText = disabledTextDark,
    scrollbarAlpha = 0.2f
)

val darkContrastCustomScheme = darkCustomScheme.copy(
    outlineComponentContent = outlineComponentContentDarkContrast
)

val LocalIsDarkTheme = staticCompositionLocalOf { false }

val LocalCustomColorScheme = staticCompositionLocalOf { CustomColorScheme() }

@Suppress("PropertyName")
@Immutable
data class CustomDrawables(
    val ic_compose: DrawableResource
)

val lightCustomDrawables = CustomDrawables(
    ic_compose = Res.drawable.ic_compose
)

val darkCustomDrawables = CustomDrawables(
    ic_compose = Res.drawable.ic_compose_dark
)

val LocalCustomDrawables = staticCompositionLocalOf { lightCustomDrawables }

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val appConfigurationViewModel: AppConfigurationViewModel = koinInject()
    val appConfigurationUiState by appConfigurationViewModel.uiState.collectAsState()

    val selectedTheme = if (appConfigurationUiState.theme == Theme.AUTO) {
        if (isSystemInDarkTheme()) Theme.DARK else Theme.LIGHT
    } else appConfigurationUiState.theme
    val isDarkTheme = (appConfigurationUiState.theme == Theme.DARK) || (appConfigurationUiState.theme == Theme.DARK_CONTRAST)

    val colorScheme: ColorScheme
    val customColorScheme: CustomColorScheme
    val customDrawables: CustomDrawables

    when (selectedTheme) {
        Theme.LIGHT_CONTRAST -> {
            colorScheme = lightContrastScheme
            customColorScheme = lightContrastCustomScheme
            customDrawables = lightCustomDrawables
        }
        Theme.DARK -> {
            colorScheme = darkScheme
            customColorScheme = darkCustomScheme
            customDrawables = darkCustomDrawables
        }
        Theme.DARK_CONTRAST -> {
            colorScheme = darkContrastScheme
            customColorScheme = darkContrastCustomScheme
            customDrawables = darkCustomDrawables
        }
        else -> {
            colorScheme = lightScheme
            customColorScheme = lightCustomScheme
            customDrawables = lightCustomDrawables
        }
    }

    val scrollbarStyle = ScrollbarStyle(
        minimalHeight = 16.dp,
        thickness = 8.dp,
        shape = RoundedCornerShape(4.dp),
        hoverDurationMillis = 300,
        unhoverColor = colorScheme.onBackground.copy(alpha = customColorScheme.scrollbarAlpha),
        hoverColor = colorScheme.onBackground.copy(alpha = customColorScheme.scrollbarHoverAlpha)
    )

    CompositionLocalProvider(
        LocalIsDarkTheme provides isDarkTheme,
        LocalCustomColorScheme provides customColorScheme,
        LocalCustomDrawables provides customDrawables,
        LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
        LocalScrollbarStyle provides scrollbarStyle
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = getAppTypography(),
            shapes = appShapes,
            content = content
        )
    }
}

@Composable
fun PreviewAppTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = lightScheme
    val customColorScheme = lightCustomScheme
    val customDrawables = lightCustomDrawables

    CompositionLocalProvider(
        LocalIsDarkTheme provides false,
        LocalCustomColorScheme provides customColorScheme,
        LocalCustomDrawables provides customDrawables,
        LocalMinimumInteractiveComponentSize provides Dp.Unspecified,
        LocalScrollbarStyle provides defaultScrollbarStyle()
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = getAppTypography(),
            shapes = appShapes,
            content = content
        )
    }
}
