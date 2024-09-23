package ru.rznnike.demokmp.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import org.jetbrains.compose.resources.DrawableResource
import org.koin.compose.koinInject
import ru.rznnike.demokmp.app.viewmodel.configuration.AppConfigurationViewModel
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

@Immutable
data class CustomColorScheme(
    val surfaceContainerA50: Color = Color.Unspecified
)

val lightCustomScheme = CustomColorScheme(
    surfaceContainerA50 = surfaceContainerA50Light
)

val darkCustomScheme = CustomColorScheme(
    surfaceContainerA50 = surfaceContainerA50Dark
)

val localCustomColorScheme = staticCompositionLocalOf { CustomColorScheme() }

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

val localCustomDrawables = staticCompositionLocalOf { lightCustomDrawables }

@Composable
fun AppTheme(
    content: @Composable () -> Unit
) {
    val appConfigurationViewModel: AppConfigurationViewModel = koinInject()
    val appConfigurationUiState by appConfigurationViewModel.uiState.collectAsState()

    val colorScheme: ColorScheme
    val customColorScheme: CustomColorScheme
    val customDrawables: CustomDrawables

    val darkTheme = when (appConfigurationUiState.theme) {
        Theme.AUTO -> isSystemInDarkTheme()
        Theme.LIGHT -> false
        Theme.DARK -> true
    }
    if (darkTheme) {
        colorScheme = darkScheme
        customColorScheme = darkCustomScheme
        customDrawables = darkCustomDrawables
    } else {
        colorScheme = lightScheme
        customColorScheme = lightCustomScheme
        customDrawables = lightCustomDrawables
    }

    CompositionLocalProvider(
        localCustomColorScheme provides customColorScheme,
        localCustomDrawables provides customDrawables,
        LocalMinimumInteractiveComponentSize provides Dp.Unspecified
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = appTypography,
            shapes = appShapes,
            content = content
        )
    }
}
