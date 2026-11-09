package ru.rznnike.demokmp.data.comobject

import org.junit.Assume.assumeTrue
import ru.rznnike.demokmp.domain.utils.OperatingSystem
import kotlin.test.Test

/**
 * Minimal smoke coverage for the only [COMObjectWrapper] code that belongs to this project:
 * the Windows guard around `CoInitializeEx` / `CoUninitialize` in `initialize()` / `uninitialize()`.
 *
 * **Accepted gap:** `invoke()` failure paths, missing-COM-object errors and the property adapters
 * (`boolProperty`, `stringProperty`, `intProperty`, `doubleProperty`, `dateProperty`) are thin
 * delegations to JNA's `COMLateBindingObject`. Asserting them would test the third-party library
 * instead of this codebase, and every one of them needs a live COM server to exercise. They stay
 * unverified; `ShellManagerImplTest` covers the project-side handling of COM failures instead.
 *
 * Requires Windows: on any other OS `initialize()` throws by design.
 */
class COMObjectWrapperTest {
    @Test
    fun `initialize does not throw on Windows`() {
        // Arrange
        assumeTrue("COM is only available on Windows", OperatingSystem.isWindows)

        // Act & Assert (no exception expected)
        try {
            COMObjectWrapper.initialize()
        } finally {
            COMObjectWrapper.uninitialize()
        }
    }

    @Test
    fun `initialize can be called repeatedly on the same thread`() {
        // Arrange
        assumeTrue("COM is only available on Windows", OperatingSystem.isWindows)

        // Act & Assert (no exception expected; each call is balanced by an uninitialize)
        try {
            COMObjectWrapper.initialize()
            COMObjectWrapper.initialize()
        } finally {
            COMObjectWrapper.uninitialize()
            COMObjectWrapper.uninitialize()
        }
    }
}
