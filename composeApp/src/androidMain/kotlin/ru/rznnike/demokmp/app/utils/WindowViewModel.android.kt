package ru.rznnike.demokmp.app.utils

import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
actual inline fun <reified VM : ViewModel> windowViewModel(
    key: String?,
    noinline initializer: CreationExtras.() -> VM
): VM = viewModel(
    viewModelStoreOwner = LocalActivity.current as ComponentActivity,
    key = key,
    initializer = initializer
)

@Composable
actual inline fun <reified VM : ViewModel> windowViewModel(): VM = viewModel(
    viewModelStoreOwner = LocalActivity.current as ComponentActivity
)