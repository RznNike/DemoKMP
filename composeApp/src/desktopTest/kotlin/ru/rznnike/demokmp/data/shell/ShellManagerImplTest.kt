package ru.rznnike.demokmp.data.shell

import kotlinx.coroutines.test.runTest
import java.util.Locale
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

private const val NOT_INITIALIZED_MESSAGE = "PowerShellWrapper is not initialized"

/**
 * Handwritten stand-in for the COM-backed [PCShell]. The production type talks to JNA, so the test
 * substitutes it through the [ShellManagerImpl] constructor seam instead of touching COM.
 */
private class FakePCShell(
    var cpuSpeedMhz: Int = 3200,
    var cpuArchitecture: String = "x64",
    var ramAmountGb: Double = 16.0,
) : PCShell {
    val calls: MutableList<String> = mutableListOf()

    override fun getCPUSpeedMhz(): Int {
        calls += "getCPUSpeedMhz"
        return cpuSpeedMhz
    }

    override fun getCPUArchitecture(): String {
        calls += "getCPUArchitecture"
        return cpuArchitecture
    }

    override fun getRAMAmountGb(): Double {
        calls += "getRAMAmountGb"
        return ramAmountGb
    }

    override fun openFolderOrFile(path: String) {
        calls += "openFolderOrFile:$path"
    }

    override fun minimizeAll() {
        calls += "minimizeAll"
    }
}

private fun createManager(
    createWrapper: () -> PCShell,
    initializeCom: () -> Unit = {},
): ShellManagerImpl = ShellManagerImpl(
    createWrapper = createWrapper,
    initializeCom = initializeCom,
)

/**
 * Lives in `desktopTest` on purpose: [ShellManagerImpl] and [PCShell] are desktop-only and the real
 * implementation wraps a Windows COM object. `internal` declarations from `desktopMain` are visible
 * here because `desktopTest` is associated with the main compilation.
 */
class ShellManagerImplTest {
    @Test
    fun `getPCData throws when the wrapper is not initialized`() = runTest {
        // Arrange
        val fake = FakePCShell()
        val manager = createManager(createWrapper = { fake })

        // Act
        val error = assertFailsWith<IllegalStateException> { manager.getPCData() }

        // Assert
        assertEquals(NOT_INITIALIZED_MESSAGE, error.message)
    }

    @Test
    fun `openFolderOrFile throws when the wrapper is not initialized`() = runTest {
        // Arrange
        val fake = FakePCShell()
        val manager = createManager(createWrapper = { fake })

        // Act
        val error = assertFailsWith<IllegalStateException> {
            manager.openFolderOrFile("C:\\temp\\file.txt")
        }

        // Assert
        assertEquals(NOT_INITIALIZED_MESSAGE, error.message)
    }

    @Test
    fun `minimizeAllWindows throws when the wrapper is not initialized`() = runTest {
        // Arrange
        val fake = FakePCShell()
        val manager = createManager(createWrapper = { fake })

        // Act
        val error = assertFailsWith<IllegalStateException> { manager.minimizeAllWindows() }

        // Assert
        assertEquals(NOT_INITIALIZED_MESSAGE, error.message)
    }

    @Test
    fun `getPCData formats the CPU and RAM values from the wrapper`() = runTest {
        // Arrange
        val fake = FakePCShell(cpuSpeedMhz = 2600, cpuArchitecture = "ARM64", ramAmountGb = 16.0)
        val manager = createManager(createWrapper = { fake })
        manager.initWrapper()
        val expected = String.format(
            Locale.getDefault(),
            "CPU %d Mhz (%s) | RAM %.2f Gb",
            fake.cpuSpeedMhz,
            fake.cpuArchitecture,
            fake.ramAmountGb,
        )

        // Act
        val result = manager.getPCData()

        // Assert
        assertEquals(expected, result)
        assertEquals(
            listOf("getCPUSpeedMhz", "getCPUArchitecture", "getRAMAmountGb"),
            fake.calls,
        )
    }

    @Test
    fun `openFolderOrFile forwards the exact path to the wrapper`() = runTest {
        // Arrange
        val fake = FakePCShell()
        val manager = createManager(createWrapper = { fake })
        manager.initWrapper()
        val path = "C:\\Users\\test\\file with spaces.txt"

        // Act
        manager.openFolderOrFile(path)

        // Assert
        assertEquals(listOf("openFolderOrFile:$path"), fake.calls)
    }

    @Test
    fun `minimizeAllWindows calls minimizeAll once`() = runTest {
        // Arrange
        val fake = FakePCShell()
        val manager = createManager(createWrapper = { fake })
        manager.initWrapper()

        // Act
        manager.minimizeAllWindows()

        // Assert
        assertEquals(listOf("minimizeAll"), fake.calls)
    }

    @Test
    fun `initWrapper creates the wrapper and initializes COM only once across repeated calls`() = runTest {
        // Arrange
        val fake = FakePCShell()
        var createWrapperCalls = 0
        var initializeComCalls = 0
        val manager = createManager(
            createWrapper = {
                createWrapperCalls++
                fake
            },
            initializeCom = { initializeComCalls++ },
        )

        // Act
        manager.initWrapper()
        manager.initWrapper()

        // Assert
        assertEquals(1, createWrapperCalls)
        assertEquals(1, initializeComCalls)
    }

    @Test
    fun `initWrapper swallows an initializeCom failure and leaves the manager uninitialized`() = runTest {
        // Arrange
        val fake = FakePCShell()
        var createWrapperCalls = 0
        val manager = createManager(
            createWrapper = {
                createWrapperCalls++
                fake
            },
            initializeCom = { throw IllegalStateException("COM initialization failed") },
        )

        // Act
        manager.initWrapper()

        // Assert
        assertEquals(0, createWrapperCalls)
        assertFailsWith<IllegalStateException> { manager.getPCData() }
    }

    @Test
    fun `initWrapper swallows a createWrapper failure and leaves the manager uninitialized`() = runTest {
        // Arrange
        val comCalls = mutableListOf<String>()
        val manager = createManager(
            createWrapper = { throw IllegalStateException("wrapper creation failed") },
            initializeCom = { comCalls += "initializeCom" },
        )

        // Act
        manager.initWrapper()

        // Assert
        assertEquals(listOf("initializeCom"), comCalls)
        assertFailsWith<IllegalStateException> { manager.getPCData() }
    }

    @Test
    fun `destroyWrapper resets the wrapper so the next call throws again`() = runTest {
        // Arrange
        val fake = FakePCShell()
        val manager = createManager(createWrapper = { fake })
        manager.initWrapper()

        // Act
        manager.destroyWrapper()

        // Assert
        val error = assertFailsWith<IllegalStateException> { manager.getPCData() }
        assertEquals(NOT_INITIALIZED_MESSAGE, error.message)
    }
}
