package ru.rznnike.demokmp.app.utils

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.viewModel

val LocalWindowViewModelStoreOwner = staticCompositionLocalOf<ViewModelStoreOwner?> { null }

@Composable
actual inline fun <reified VM : ViewModel> windowViewModel(
    key: String?,
    noinline initializer: CreationExtras.() -> VM
): VM = viewModel(
    viewModelStoreOwner = checkNotNull(LocalWindowViewModelStoreOwner.current) {
        "No ViewModelStoreOwner was provided via LocalWindowViewModelStoreOwner"
    },
    key = key,
    initializer = initializer
)


private class WindowViewModelStoreOwner: ViewModelStoreOwner {
    override val viewModelStore: ViewModelStore = ViewModelStore()

    fun dispose() = viewModelStore.clear()
}

@Composable
fun WithWindowViewModelStoreOwner(
    content: @Composable () -> Unit
) {
    val viewModelStoreOwner = remember { WindowViewModelStoreOwner() }
    DisposableEffect(viewModelStoreOwner) {
        onDispose { viewModelStoreOwner.dispose() }
    }

    CompositionLocalProvider(
        value = LocalWindowViewModelStoreOwner provides viewModelStoreOwner,
        content = content
    )
}