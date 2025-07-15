package ru.rznnike.demokmp.app.utils

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.patrykandpatrick.vico.multiplatform.common.vicoTheme

@Composable
fun getCustomVicoTheme() = vicoTheme.copy(
    lineColor = MaterialTheme.colorScheme.outline,
    textColor = MaterialTheme.colorScheme.onBackground
)