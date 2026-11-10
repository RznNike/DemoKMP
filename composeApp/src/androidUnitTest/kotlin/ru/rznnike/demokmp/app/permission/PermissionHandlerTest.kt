package ru.rznnike.demokmp.app.permission

import android.Manifest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * `PermissionHandler` keeps its `permissionsState` in a nullable field that only the `@Composable`
 * `register()` assigns. Everything here exercises the unregistered state — the only part reachable
 * from a host JVM without a composition.
 *
 * Current contract (documented, not endorsed): with no registered state `permissionsGranted`
 * reports `true`, so `checkPermissions` short-circuits instead of launching a request.
 */
class PermissionHandlerTest {
    @Test
    fun `permissionsGranted reports true before register is called`() {
        // Arrange
        val handler = PermissionHandler(listOf(Manifest.permission.CAMERA))

        // Act
        val granted = handler.permissionsGranted

        // Assert
        assertTrue(granted)
    }

    @Test
    fun `permissionsGranted reports true for an empty permission list`() {
        // Arrange
        val handler = PermissionHandler(emptyList())

        // Act
        val granted = handler.permissionsGranted

        // Assert
        assertTrue(granted)
    }

    @Test
    fun `checkPermissions invokes the callback immediately while no state is registered`() {
        // Arrange
        val handler = PermissionHandler(listOf(Manifest.permission.CAMERA))
        var granted: Boolean? = null

        // Act
        handler.checkPermissions { granted = it }

        // Assert
        assertEquals(true, granted)
    }
}
