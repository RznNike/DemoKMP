package ru.rznnike.demokmp.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val extraSmallCorners = 8.dp
val appShapes = Shapes(
    extraSmall = RoundedCornerShape(extraSmallCorners),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(8.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(32.dp)
)
