package ru.rznnike.demokmp.app.utils

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras

@Composable
expect inline fun <reified VM : ViewModel> windowViewModel(
    key: String? = null,
    noinline initializer: CreationExtras.() -> VM
): VM