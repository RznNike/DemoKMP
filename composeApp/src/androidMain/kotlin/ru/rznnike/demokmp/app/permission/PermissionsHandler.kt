package ru.rznnike.demokmp.app.permission

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.*

class PermissionsHandler(
    private val permissions: List<String>
) {
    private var permissionsState: Any? = null
    private var callback: ((permissionsGranted: Boolean) -> Unit)? = null

    @OptIn(ExperimentalPermissionsApi::class)
    @Composable
    fun register(): PermissionsHandler {
        when (permissions.size) {
            0 -> Unit
            1 -> {
                permissionsState = rememberPermissionState(permissions.first()) { result ->
                    callback?.invoke(result)
                }
            }
            else -> {
                permissionsState = rememberMultiplePermissionsState(permissions) { results ->
                    callback?.invoke(
                        results.all { (_, result) -> result }
                    )
                }
            }
        }
        return this
    }

    @OptIn(ExperimentalPermissionsApi::class)
    val permissionsGranted: Boolean
        get() {
            return when (val state = permissionsState) {
                is PermissionState -> state.status.isGranted
                is MultiplePermissionsState -> state.allPermissionsGranted
                else -> true
            }
        }

    @OptIn(ExperimentalPermissionsApi::class)
    fun checkPermissions(callback: (permissionsGranted: Boolean) -> Unit) {
        this.callback = callback
        when {
            permissionsGranted -> callback.invoke(true)
            permissions.size == 1 -> (permissionsState as? PermissionState)?.launchPermissionRequest()
            else -> (permissionsState as? MultiplePermissionsState)?.launchMultiplePermissionRequest()
        }
    }
}

@Composable
fun rememberNotificationsPermissionHandler() = remember {
    PermissionsHandler(
        permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            listOf(
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else emptyList()
    )
}.register()