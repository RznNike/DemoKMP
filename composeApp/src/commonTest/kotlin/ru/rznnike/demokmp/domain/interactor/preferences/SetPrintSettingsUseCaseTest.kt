package ru.rznnike.demokmp.domain.interactor.preferences

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakePreferencesGateway
import ru.rznnike.demokmp.domain.model.print.PrintSettings
import ru.rznnike.demokmp.domain.model.print.TwoSidedPrint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class SetPrintSettingsUseCaseTest {
    @Test
    fun `forwards the print settings to the preferences gateway`() = runTest {
        // Arrange
        val settings = PrintSettings(
            printerName = "HP LaserJet",
            twoSidedPrint = TwoSidedPrint.TWO_SIDED_SHORT_EDGE
        )
        val gateway = FakePreferencesGateway()
        val useCase = SetPrintSettingsUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(settings)

        // Assert
        assertEquals(listOf(settings), gateway.setPrintSettingsValues)
        assertEquals(Unit, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of forwarding the print settings`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakePreferencesGateway().apply { errorToThrow = expected }
        val useCase = SetPrintSettingsUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase(
            PrintSettings(
                printerName = "HP LaserJet",
                twoSidedPrint = TwoSidedPrint.TWO_SIDED_SHORT_EDGE
            )
        )

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
        assertTrue(gateway.setPrintSettingsValues.isEmpty())
    }
}
