package ru.rznnike.demokmp.domain.interactor.preferences

import kotlinx.coroutines.test.runTest
import ru.rznnike.demokmp.domain.common.FakeDispatcherProvider
import ru.rznnike.demokmp.domain.gateway.FakePreferencesGateway
import ru.rznnike.demokmp.domain.model.print.PrintSettings
import ru.rznnike.demokmp.domain.model.print.TwoSidedPrint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetPrintSettingsUseCaseTest {
    @Test
    fun `returns the print settings stored in preferences`() = runTest {
        // Arrange
        val settings = PrintSettings(
            printerName = "HP LaserJet",
            twoSidedPrint = TwoSidedPrint.TWO_SIDED_LONG_EDGE
        )
        val gateway = FakePreferencesGateway().apply { printSettings = settings }
        val useCase = GetPrintSettingsUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(settings, result.data)
        assertNull(result.error)
    }

    @Test
    fun `returns the gateway error instead of data`() = runTest {
        // Arrange
        val expected = IllegalStateException("boom")
        val gateway = FakePreferencesGateway().apply { errorToThrow = expected }
        val useCase = GetPrintSettingsUseCase(gateway, FakeDispatcherProvider())

        // Act
        val result = useCase()

        // Assert
        assertEquals(expected, result.error)
        assertNull(result.data)
    }
}
